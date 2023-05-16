package io.github.jhonatandevperu.springdynamodataloader.repository;

import io.github.jhonatandevperu.springdynamodataloader.exceptions.InternalServerErrorException;
import java.util.List;
import java.util.Map;

public interface DynamoDbRepository {

  void loadData(String tableName, List<Map<String, Map<String, Object>>> items)
      throws InternalServerErrorException;

  boolean isTableExists(String tableName);
}
