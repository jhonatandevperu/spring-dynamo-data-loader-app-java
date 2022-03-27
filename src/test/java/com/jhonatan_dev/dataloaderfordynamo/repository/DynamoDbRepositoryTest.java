package com.jhonatan_dev.dataloaderfordynamo.repository;

import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.jhonatan_dev.dataloaderfordynamo.repository.impl.DynamoDbRepositoryImpl;
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
