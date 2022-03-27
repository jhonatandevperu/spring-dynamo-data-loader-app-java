package com.jhonatan_dev.dataloaderfordynamo.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.jhonatan_dev.dataloaderfordynamo.exceptions.InternalServerErrorException;
import com.jhonatan_dev.dataloaderfordynamo.repository.AsyncDynamoDbRepository;
import com.jhonatan_dev.dataloaderfordynamo.util.DynamoUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class AsyncDynamoDbRepositoryImpl implements AsyncDynamoDbRepository {

  private final AmazonDynamoDB amazonDynamoDB;

  @Autowired
  public AsyncDynamoDbRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
    this.amazonDynamoDB = amazonDynamoDB;
  }

  @Async("taskExecutor")
  @Override
  public CompletableFuture<BatchWriteItemResult> loadData(
      String tableName, List<Map<String, Map<String, Object>>> items) {
    log.info(
        "Start -> asyncDynamoDbRepository.loadData, thread {}", Thread.currentThread().getName());

    try {

      BatchWriteItemRequest batchWriteItemRequest =
          DynamoUtil.getBatchWriteItemResult(tableName, items);

      BatchWriteItemResult batchWriteItemResult =
          amazonDynamoDB.batchWriteItem(batchWriteItemRequest);

      log.info(
          "asyncDynamoDbRepository.loadData, thread {}, batchWriteItemResult.unprocessedItems size : {}",
          Thread.currentThread().getName(),
          batchWriteItemResult.getUnprocessedItems().size());

      log.info(
          "asyncDynamoDbRepository.loadData, thread {}, batchWriteItemResult.unprocessedItems: {}",
          Thread.currentThread().getName(),
          batchWriteItemResult.getUnprocessedItems());

      log.info(
          "End -> asyncDynamoDbRepository.loadData, thread {}", Thread.currentThread().getName());
      return CompletableFuture.completedFuture(batchWriteItemResult);
    } catch (Exception ex) {
      log.error(
          "asyncDynamoDbRepository.loadData, thread {} error: {}",
          Thread.currentThread().getName(),
          ex.getMessage());

      ex.printStackTrace();

      return CompletableFuture.failedFuture(new InternalServerErrorException(ex.getMessage()));
    }
  }
}
