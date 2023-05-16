package com.jhonatan_dev.dataloaderfordynamo.util;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import java.nio.ByteBuffer;
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

    Object attributeValue = getObjectAttributeValue(attributeType, attributeGenericValue);

    return attributeValue != null ? ItemUtils.toAttributeValue(attributeValue) : null;
  }

  public static Object getObjectAttributeValue(String attributeType, Object attributeGenericValue) {
    return switch (attributeType) {
      case "B" -> getAttributeValueB(attributeGenericValue);
      case "BS" -> getAttributeValueBS(attributeGenericValue);
      case "BOOL" -> getAttributeValueBOOL(attributeGenericValue);
      case "N" -> getAttributeValueN(attributeGenericValue);
      case "NS" -> getAttributeValueNS(attributeGenericValue);
      case "NULL" -> getAttributeValueNULL(attributeGenericValue);
      case "S" -> getAttributeValueS(attributeGenericValue);
      case "SS" -> getAttributeValueSS(attributeGenericValue);
      case "L" -> getAttributeValueL(attributeGenericValue);
      default -> null;
    };
  }

  private static ByteBuffer getAttributeValueB(Object attributeGenericValue) {
    if (attributeGenericValue instanceof String strAttributeGenericValue) {
      try {
        byte[] bytes = Base64.getDecoder().decode(strAttributeGenericValue);

        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes, 0, bytes.length);
        byteBuffer.position(0);

        return byteBuffer;
      } catch (IllegalArgumentException ex) {
        log.error(
            "dynamoUtil.getNAttributeValue, parsing 'B' type, value '{}', error: {}",
            strAttributeGenericValue,
            ex.getMessage());
        ex.printStackTrace();
      }
    }
    return null;
  }

  private static List<ByteBuffer> getAttributeValueBS(Object attributeGenericValue) {
    List<ByteBuffer> byteBuffers = new ArrayList<>();
    if (attributeGenericValue instanceof List<?> listAttributeGenericValue) {
      byteBuffers.addAll(
          listAttributeGenericValue.stream()
              .map(DynamoUtil::getAttributeValueB)
              .filter(Objects::nonNull)
              .toList());
    }
    return !byteBuffers.isEmpty() ? byteBuffers : null;
  }

  @SuppressWarnings("unchecked")
  private static List<Object> getAttributeValueL(Object attributeGenericValue) {
    List<Object> objects = new ArrayList<>();
    if (attributeGenericValue instanceof List<?> listAttributeGenericValue) {
      for (Object value : listAttributeGenericValue) {
        if (value instanceof Map<?, ?> mapValue) {
          Set<Map.Entry<String, Object>> subItemData = ((Map<String, Object>) mapValue).entrySet();

          for (Map.Entry<String, Object> subItemType : subItemData) {
            Object object = getObjectAttributeValue(subItemType.getKey(), subItemType.getValue());

            if (object != null) {
              objects.add(object);
            }
          }
        }
      }
    }
    return !objects.isEmpty() ? objects : null;
  }

  private static Boolean getAttributeValueBOOL(Object attributeGenericValue) {
    return attributeGenericValue instanceof Boolean boolAttributeGenericValue
        ? boolAttributeGenericValue
        : null;
  }

  private static Number getAttributeValueN(Object attributeGenericValue) {
    if (attributeGenericValue instanceof String strAttributeGenericValue) {
      try {
        return NumberFormat.getInstance().parse(strAttributeGenericValue);
      } catch (ParseException ex) {
        log.error(
            "dynamoUtil.getNAttributeValue, parsing 'N' type, value '{}', error: {}",
            strAttributeGenericValue,
            ex.getMessage());
        ex.printStackTrace();
      }
    } else if (attributeGenericValue instanceof Number numberAttributeGenericValue) {
      return numberAttributeGenericValue;
    }
    return null;
  }

  private static List<Number> getAttributeValueNS(Object attributeGenericValue) {
    List<Number> numbers = new ArrayList<>();
    if (attributeGenericValue instanceof List<?> listAttributeGenericValue) {
      numbers.addAll(
          listAttributeGenericValue.stream()
              .map(DynamoUtil::getAttributeValueN)
              .filter(Objects::nonNull)
              .toList());
    }
    return !numbers.isEmpty() ? numbers : null;
  }

  private static Object getAttributeValueNULL(Object attributeGenericValue) {
    return attributeGenericValue instanceof Boolean boolAttributeGenericValue
            && Boolean.TRUE.equals(boolAttributeGenericValue)
        ? null
        : attributeGenericValue;
  }

  private static String getAttributeValueS(Object attributeGenericValue) {
    return attributeGenericValue instanceof String castedAttributeGenericValue
        ? castedAttributeGenericValue
        : null;
  }

  private static List<String> getAttributeValueSS(Object attributeGenericValue) {
    List<String> strings = new ArrayList<>();
    if (attributeGenericValue instanceof List<?> listAttributeGenericValue) {
      strings.addAll(
          listAttributeGenericValue.stream()
              .map(DynamoUtil::getAttributeValueS)
              .filter(Objects::nonNull)
              .toList());
    }
    return !strings.isEmpty() ? strings : null;
  }
}
