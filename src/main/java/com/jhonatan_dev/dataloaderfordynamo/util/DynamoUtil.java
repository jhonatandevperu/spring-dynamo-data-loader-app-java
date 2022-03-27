package com.jhonatan_dev.dataloaderfordynamo.util;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class DynamoUtil {

  public static final int MAX_ITEMS_BATCH_WRITE = 25;

  public static BatchWriteItemRequest getBatchWriteItemResult(
      String tableName, List<Map<String, Map<String, Object>>> items) {
    Map<String, List<WriteRequest>> requestItems = new HashMap<>();

    requestItems.put(tableName, getWriteRequests(items));

    return new BatchWriteItemRequest(requestItems);
  }

  public static List<WriteRequest> getWriteRequests(List<Map<String, Map<String, Object>>> items) {

    List<WriteRequest> writeRequests = new ArrayList<>();

    for (Map<String, Map<String, Object>> item : items) {
      writeRequests.add(getWriteRequest(item));
    }

    return writeRequests;
  }

  public static WriteRequest getWriteRequest(Map<String, Map<String, Object>> item) {

    Map<String, AttributeValue> putItem = getPutItem(item);

    PutRequest putRequest = new PutRequest(putItem);

    return new WriteRequest(putRequest);
  }

  public static PutItemRequest getPutItemRequest(
      String tableName, Map<String, Map<String, Object>> item) {

    return new PutItemRequest(tableName, getPutItem(item));
  }

  public static Map<String, AttributeValue> getPutItem(Map<String, Map<String, Object>> item) {

    Map<String, AttributeValue> putItem = new HashMap<>();

    for (Map.Entry<String, Map<String, Object>> itemData : item.entrySet()) {
      String attributeName = itemData.getKey();

      AttributeValue attributeValue = new AttributeValue().withNULL(Boolean.FALSE);

      for (Map.Entry<String, Object> itemType : itemData.getValue().entrySet()) {

        attributeValue = getAttributeValue(itemType.getKey(), itemType.getValue());
      }

      if (attributeValue != null) {
        putItem.put(attributeName, attributeValue);
      }
    }

    return putItem;
  }

  public static AttributeValue getAttributeValue(
      String attributeType, Object attributeGenericValue) {
    switch (attributeType) {
      case "B":
        return getAttributeValueB(attributeGenericValue);
      case "BS":
        return getAttributeValueBS(attributeGenericValue);
      case "BOOL":
        return getAttributeValueBOOL(attributeGenericValue);
      case "N":
        return getAttributeValueN(attributeGenericValue);
      case "NS":
        return getAttributeValueNS(attributeGenericValue);
      case "NULL":
        return getAttributeValueNULL(attributeGenericValue);
      case "S":
        return getAttributeValueS(attributeGenericValue);
      case "SS":
        return getAttributeValueSS(attributeGenericValue);
      default:
        return null;
    }
  }

  private static AttributeValue getAttributeValueB(Object attributeGenericValue) {
    if (attributeGenericValue instanceof String) {
      try {
        byte[] byteArray = Base64.getDecoder().decode((String) attributeGenericValue);
        return ItemUtils.toAttributeValue(byteArray);
      } catch (IllegalArgumentException ex) {
        log.error("dynamoUtil.getAttributeValueB, parsing 'B' type, error: {}", ex.getMessage());
        ex.printStackTrace();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static AttributeValue getAttributeValueBS(Object attributeGenericValue) {
    if (attributeGenericValue instanceof List) {
      List<byte[]> byteArrays = new ArrayList<>();

      List<Object> values = (List<Object>) attributeGenericValue;

      for (Object value : values) {
        if (value instanceof String) {
          try {
            byte[] byteArray = Base64.getDecoder().decode((String) attributeGenericValue);
            byteArrays.add(byteArray);
          } catch (IllegalArgumentException ex) {
            log.error(
                "dynamoUtil.getAttributeValueBS, parsing 'BS' type, error: {}", ex.getMessage());
            ex.printStackTrace();
          }
        }
      }

      if (!byteArrays.isEmpty()) {
        return ItemUtils.toAttributeValue(byteArrays);
      }
    }
    return null;
  }

  private static AttributeValue getAttributeValueBOOL(Object attributeGenericValue) {
    if (attributeGenericValue instanceof Boolean) {
      return new AttributeValue().withBOOL((Boolean) attributeGenericValue);
    }
    return null;
  }

  private static AttributeValue getAttributeValueN(Object attributeGenericValue) {
    if (attributeGenericValue instanceof String) {
      try {
        Number number = NumberFormat.getInstance().parse((String) attributeGenericValue);

        return ItemUtils.toAttributeValue(number);
      } catch (ParseException ex) {
        log.error("dynamoUtil.getNAttributeValue, parsing 'N' type, error: {}", ex.getMessage());
        ex.printStackTrace();
      }
    } else if (attributeGenericValue instanceof Number) {
      return ItemUtils.toAttributeValue(attributeGenericValue);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static AttributeValue getAttributeValueNS(Object attributeGenericValue) {
    if (attributeGenericValue instanceof List) {
      List<Number> numbers = new ArrayList<>();

      List<Object> values = (List<Object>) attributeGenericValue;

      for (Object value : values) {
        if (value instanceof String) {
          try {
            Number number = NumberFormat.getInstance().parse((String) value);

            numbers.add(number);
          } catch (ParseException ex) {
            log.error(
                "dynamoUtil.getNSAttributeValue, parsing 'NS' type, error: {}", ex.getMessage());
            ex.printStackTrace();
          }
        } else if (attributeGenericValue instanceof Number) {
          numbers.add((Number) attributeGenericValue);
        }
      }

      if (!numbers.isEmpty()) {
        return ItemUtils.toAttributeValue(numbers);
      }
    }
    return null;
  }

  private static AttributeValue getAttributeValueNULL(Object attributeGenericValue) {
    if (attributeGenericValue instanceof Boolean) {
      return new AttributeValue().withNULL((Boolean) attributeGenericValue);
    }
    return null;
  }

  private static AttributeValue getAttributeValueS(Object attributeGenericValue) {
    if (attributeGenericValue instanceof String) {
      return new AttributeValue().withS((String) attributeGenericValue);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static AttributeValue getAttributeValueSS(Object attributeGenericValue) {
    if (attributeGenericValue instanceof List) {
      List<String> strings = new ArrayList<>();

      List<Object> values = (List<Object>) attributeGenericValue;

      for (Object value : values) {
        if (value instanceof String) {
          strings.add((String) value);
        }
      }

      if (!strings.isEmpty()) {
        return ItemUtils.toAttributeValue(strings);
      }
    }
    return null;
  }

  public static int getOperationTimes(int itemsSize, int itemsSplitSize) {
    return (int) Math.ceil(itemsSize / (float) itemsSplitSize);
  }
}
