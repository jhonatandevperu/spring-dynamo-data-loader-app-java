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
  public void loadData(RequestBatchLoadDto body) throws Exception {
    log.info("Start -> dynamoDbService.loadData");

    dynamoDbRepository.loadData(body.getTableName(), body.getContent().getItems());

    log.info("End -> dynamoDbService.loadData");
  }

  @Override
  public void asyncLoadData(RequestBatchLoadDto body) throws Exception {

    String tableName = body.getTableName();

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

    for (List<Map<String, Map<String, Object>>> itemsContainer : itemsContainers) {

      if (DynamoUtil.MAX_THREADS_BATCH == calls) {
        log.info("Waiting DynamoDB {} threads calls...", DynamoUtil.MAX_THREADS_BATCH);

        CompletableFuture.allOf(
                futures.stream()
                    .filter(future -> !future.isDone())
                    .toArray(CompletableFuture[]::new))
            .join();

        calls = 0;

        log.info("DynamoDB {} threads calls, done!", DynamoUtil.MAX_THREADS_BATCH);
      }

      CompletableFuture<BatchWriteItemResult> future =
          asyncDynamoDbRepository.loadData(tableName, itemsContainer);

      futures.add(future);

      calls++;
    }
    log.info("Futures size: {}", futures.size());
    log.info("Futures done: {}", futures.stream().filter(CompletableFuture::isDone).count());
    log.info("Futures not done: {}", futures.stream().filter(future -> !future.isDone()).count());
    log.info(
        "Futures with error: {}",
        futures.stream().filter(CompletableFuture::isCompletedExceptionally).count());
  }
}
