package io.github.jhonatandevperu.springdynamodataloader.repository;

import static org.mockito.Mockito.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import io.github.jhonatandevperu.springdynamodataloader.exceptions.InternalServerErrorException;
import io.github.jhonatandevperu.springdynamodataloader.repository.impl.DynamoDbRepositoryImpl;
import io.github.jhonatandevperu.springdynamodataloader.util.DynamoUtil;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DynamoDbRepositoryTest {

  @Mock private AmazonDynamoDB amazonDynamoDB;

  @InjectMocks private DynamoDbRepositoryImpl dynamoDbRepository;

  @Test
  void testLoadData() throws InternalServerErrorException {
    String tableName = "myTable";

    Map<String, Object> itemValue = new HashMap<>();
    itemValue.put("S", "partitionKeyValue");

    Map<String, Map<String, Object>> item = new HashMap<>();
    item.put("partitionKey", itemValue);

    List<Map<String, Map<String, Object>>> items = new ArrayList<>(List.of(item));

    BatchWriteItemRequest batchWriteItemRequest =
        DynamoUtil.getBatchWriteItemResult(tableName, items);

    BatchWriteItemResult batchWriteItemResult = new BatchWriteItemResult();
    batchWriteItemResult.setUnprocessedItems(new HashMap<>());

    when(amazonDynamoDB.batchWriteItem(batchWriteItemRequest)).thenReturn(batchWriteItemResult);

    dynamoDbRepository.loadData(tableName, items);

    Mockito.verify(amazonDynamoDB).batchWriteItem(batchWriteItemRequest);
  }

  @Test
  void testLoadData_Exception() {
    String tableName = "myTable";

    Map<String, Object> itemValue = new HashMap<>();
    itemValue.put("S", "partitionKeyValue");

    Map<String, Map<String, Object>> item = new HashMap<>();
    item.put("partitionKey", itemValue);

    List<Map<String, Map<String, Object>>> items = new ArrayList<>(List.of(item));

    BatchWriteItemRequest batchWriteItemRequest =
        DynamoUtil.getBatchWriteItemResult(tableName, items);

    BatchWriteItemResult batchWriteItemResult = new BatchWriteItemResult();
    batchWriteItemResult.setUnprocessedItems(new HashMap<>());

    when(amazonDynamoDB.batchWriteItem(batchWriteItemRequest))
        .thenThrow(
            new ProvisionedThroughputExceededException(
                "You exceeded your maximum allowed provisioned throughput for a table"));

    Assertions.assertThrows(
        InternalServerErrorException.class,
        () -> {
          dynamoDbRepository.loadData(tableName, items);
        });
  }

  @Test
  void testIsTableExists() {
    String tableName = "myTable";

    DescribeTableResult describeTableResult = Mockito.mock(DescribeTableResult.class);
    TableDescription tableDescription = Mockito.mock(TableDescription.class);

    when(amazonDynamoDB.describeTable(tableName)).thenReturn(describeTableResult);
    when(describeTableResult.getTable()).thenReturn(tableDescription);
    when(tableDescription.getTableName()).thenReturn(tableName);

    boolean result = dynamoDbRepository.isTableExists(tableName);

    Assertions.assertTrue(result);
  }
}
