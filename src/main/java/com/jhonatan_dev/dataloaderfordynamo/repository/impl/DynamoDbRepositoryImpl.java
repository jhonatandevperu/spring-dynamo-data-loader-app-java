package com.jhonatan_dev.dataloaderfordynamo.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.jhonatan_dev.dataloaderfordynamo.exceptions.InternalServerErrorException;
import com.jhonatan_dev.dataloaderfordynamo.repository.DynamoDbRepository;
import com.jhonatan_dev.dataloaderfordynamo.util.DynamoUtil;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class DynamoDbRepositoryImpl implements DynamoDbRepository {

  private final AmazonDynamoDB amazonDynamoDB;

  @Autowired
  public DynamoDbRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
    this.amazonDynamoDB = amazonDynamoDB;
  }

  @Override
  public void loadData(String tableName, List<Map<String, Map<String, Object>>> items)
      throws InternalServerErrorException {
    log.info("Start -> dynamoDbRepository.loadData");

    try {
      BatchWriteItemRequest batchWriteItemRequest =
          DynamoUtil.getBatchWriteItemResult(tableName, items);

      BatchWriteItemResult batchWriteItemResult =
          amazonDynamoDB.batchWriteItem(batchWriteItemRequest);

      log.info(
          "batchWriteItemResult.unprocessedItems size : {}",
          batchWriteItemResult.getUnprocessedItems().size());

      log.info(
          "batchWriteItemResult.unprocessedItems: {}", batchWriteItemResult.getUnprocessedItems());

    } catch (Exception ex) {

      log.error(ex.getStackTrace());

      throw new InternalServerErrorException(ex.getMessage());
    }

    log.info("End -> dynamoDbRepository.loadData");
  }

  @Override
  public boolean isTableExists(String tableName) {
    log.info("Start -> dynamoDbRepository.isTableExists");

    boolean existsTable = false;

    try {
      DescribeTableResult describeTableResult = amazonDynamoDB.describeTable(tableName);

      existsTable = describeTableResult.getTable().getTableName().equals(tableName);
    } catch (Exception ex) {
      log.error(ex.getStackTrace());
    }

    log.info("End -> dynamoDbRepository.isTableExists");
    return existsTable;
  }
}
