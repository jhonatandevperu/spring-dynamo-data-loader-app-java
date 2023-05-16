package io.github.jhonatandevperu.springdynamodataloader.repository;

import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncDynamoDbRepository {

  CompletableFuture<BatchWriteItemResult> loadData(
      String tableName, List<Map<String, Map<String, Object>>> items);
}
