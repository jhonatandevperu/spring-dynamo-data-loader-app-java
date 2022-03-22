package com.jhonatan_dev.dataloaderfordynamo.repository;

import java.util.List;
import java.util.Map;

public interface DynamoDbRepository {

  void loadData(String tableName, List<Map<String, Map<String, Object>>> items) throws Exception;

  boolean isTableExists(String tableName);
}
