package com.jhonatan_dev.dataloaderfordynamo.service.impl;

import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;
import com.jhonatan_dev.dataloaderfordynamo.repository.AsyncDynamoDbRepository;
import com.jhonatan_dev.dataloaderfordynamo.repository.DynamoDbRepository;
import com.jhonatan_dev.dataloaderfordynamo.service.DynamoDbService;
import com.jhonatan_dev.dataloaderfordynamo.util.DynamoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class DynamoDbServiceImpl implements DynamoDbService {

  private final DynamoDbRepository dynamoDbRepository;

  private final AsyncDynamoDbRepository asyncDynamoDbRepository;

  @Autowired
  public DynamoDbServiceImpl(
      DynamoDbRepository dynamoDbRepository, AsyncDynamoDbRepository asyncDynamoDbRepository) {
    this.dynamoDbRepository = dynamoDbRepository;
    this.asyncDynamoDbRepository = asyncDynamoDbRepository;
  }

  @Override
  public void asyncLoadData(RequestBatchLoadDto body) throws Exception {

    String tableName = body.getTableName();

    if (!dynamoDbRepository.isTableExists(tableName)) {
      throw new Exception(String.format("Table %s doesn't exists.", body.getTableName()));
    }

    List<Map<String, Map<String, Object>>> items = body.getContent().getItems();

    log.info("items size: {}", items.size());

    List<List<Map<String, Map<String, Object>>>> itemsContainers =
        new ArrayList<>(
            IntStream.range(0, items.size())
                .boxed()
                .collect(
                    Collectors.groupingBy(
                        partition -> (partition / DynamoUtil.MAX_ITEMS_BATCH_WRITE),
                        Collectors.mapping(items::get, Collectors.toList())))
                .values());

    List<CompletableFuture<BatchWriteItemResult>> futures = new ArrayList<>();

    int calls = 0;

    int maxThreadsPerBatch = 10;

    for (List<Map<String, Map<String, Object>>> itemsContainer : itemsContainers) {

      if (maxThreadsPerBatch == calls) {
        log.info("Waiting DynamoDB {} threads calls...", maxThreadsPerBatch);

        waitingForCompletableFuturesPendingExecution(futures);

        calls = 0;

        log.info("DynamoDB {} threads calls, done!", maxThreadsPerBatch);
      }

      CompletableFuture<BatchWriteItemResult> future =
          asyncDynamoDbRepository.loadData(tableName, itemsContainer);

      futures.add(future);

      calls++;
    }

    waitingForCompletableFuturesPendingExecution(futures);

    log.info("CompletableFutures size: {}", futures.size());
    log.info(
        "CompletableFutures done: {}", futures.stream().filter(CompletableFuture::isDone).count());
    log.info(
        "CompletableFutures not done: {}",
        futures.stream().filter(future -> !future.isDone()).count());
    log.info(
        "CompletableFutures with error: {}",
        futures.stream().filter(CompletableFuture::isCompletedExceptionally).count());
  }

  private void waitingForCompletableFuturesPendingExecution(
      List<CompletableFuture<BatchWriteItemResult>> futures) {
    List<CompletableFuture<BatchWriteItemResult>> futuresNotExecuted =
        futures.stream()
            .filter(
                future ->
                    !future.isDone() && !future.isCompletedExceptionally() && !future.isCancelled())
            .collect(Collectors.toList());

    if (!futuresNotExecuted.isEmpty()) {
      CompletableFuture.allOf(futuresNotExecuted.toArray(CompletableFuture[]::new)).join();
    }
  }

}
