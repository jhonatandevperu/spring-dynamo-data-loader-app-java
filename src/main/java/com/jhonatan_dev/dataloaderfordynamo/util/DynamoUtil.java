package com.jhonatan_dev.dataloaderfordynamo.util;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DynamoUtil {

  public static int MAX_ITEMS_BATCH_WRITE = 25;

  public static int MAX_THREADS_BATCH = 25;

  public static BatchWriteItemRequest getBatchWriteItemResult(
      String tableName, List<Map<String, Map<String, Object>>> items) {
    Map<String, List<WriteRequest>> requestItems = new HashMap<>();

    requestItems.put(tableName, getWriteRequests(items));

    return new BatchWriteItemRequest(requestItems);
  }

  public static List<WriteRequest> getWriteRequests(List<Map<String, Map<String, Object>>> items) {

    List<WriteRequest> writeRequests =
        items.stream().map(DynamoUtil::getWriteRequest).collect(Collectors.toList());

    return writeRequests;
  }

  public static WriteRequest getWriteRequest(Map<String, Map<String, Object>> item) {

    Map<String, AttributeValue> putItem = getPutItem(item);

    PutRequest putRequest = new PutRequest(putItem);

    return new WriteRequest(putRequest);
  }

  public static PutItemRequest getPutItemRequest(
      String tableName, Map<String, Map<String, Object>> item) {

    PutItemRequest putItemRequest = new PutItemRequest(tableName, getPutItem(item));

    return putItemRequest;
  }

  public static Map<String, AttributeValue> getPutItem(Map<String, Map<String, Object>> item) {

    Map<String, AttributeValue> putItem = new HashMap<>();

    item.entrySet().stream()
        .forEach(
            attribute -> {
              String attributeName = attribute.getKey();

              AtomicReference<AttributeValue> attributeValue =
                  new AtomicReference<>(new AttributeValue());

              attribute.getValue().entrySet().stream()
                  .forEach(
                      attributeType -> {
                        attributeValue.set(ItemUtils.toAttributeValue(attributeType.getValue()));
                      });

              putItem.put(attributeName, attributeValue.get());
            });

    return putItem;
  }

  public static int getOperationTimes(int itemsSize, int itemsSplitSize) {

    int operationTimes = (int) Math.ceil(itemsSize / (float) itemsSplitSize);

    return operationTimes;
  }
}
