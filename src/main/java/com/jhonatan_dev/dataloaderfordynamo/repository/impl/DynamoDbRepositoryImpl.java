package com.jhonatan_dev.dataloaderfordynamo.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.jhonatan_dev.dataloaderfordynamo.repository.DynamoDbRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class DynamoDbRepositoryImpl implements DynamoDbRepository {

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Override
	public void loadData(String tableName, List<Map<String, Map<String, Object>>> items) throws Exception {
		log.info("Start -> dynamoDbRepository.loadData");

		log.info("tableName: " + tableName);

		log.info("items size: " + items.size());

		BatchWriteItemRequest batchWriteItemRequest = getBatchWriteItemResult(tableName, items);

		BatchWriteItemResult batchWriteItemResult = amazonDynamoDB.batchWriteItem(batchWriteItemRequest);

		log.info("batchWriteItemResult: " + batchWriteItemResult);

		log.info("End -> dynamoDbRepository.loadData");
	}

	private BatchWriteItemRequest getBatchWriteItemResult(String tableName,
			List<Map<String, Map<String, Object>>> items) {
		log.info("Start -> dynamoDbRepository.getBatchWriteItemResult");
		Map<String, List<WriteRequest>> requestItems = new HashMap<>();

		requestItems.put(tableName, getWriteRequests(items));

		log.info("Start -> dynamoDbRepository.getBatchWriteItemResult");
		return new BatchWriteItemRequest(requestItems);
	}

	private List<WriteRequest> getWriteRequests(List<Map<String, Map<String, Object>>> items) {
		log.info("Start -> dynamoDbRepository.getWriteRequests");

		List<WriteRequest> writeRequests = items.stream().map(this::getWriteRequest).collect(Collectors.toList());

		log.info("Start -> dynamoDbRepository.getWriteRequests");
		return writeRequests;
	}

	private WriteRequest getWriteRequest(Map<String, Map<String, Object>> item) {
		log.info("Start -> dynamoDbRepository.getWriteRequest");

		Map<String, AttributeValue> putItem = getPutItem(item);

		PutRequest putRequest = new PutRequest(putItem);

		log.info("End -> dynamoDbRepository.getWriteRequest");
		return new WriteRequest(putRequest);
	}

	private PutItemRequest getPutItemRequest(String tableName, Map<String, Map<String, Object>> item) {
		log.info("Start -> dynamoDbRepository.getPutItemRequest");

		PutItemRequest putItemRequest = new PutItemRequest(tableName, getPutItem(item));

		log.info("End -> dynamoDbRepository.getPutItemRequest");
		return putItemRequest;

	}

	private Map<String, AttributeValue> getPutItem(Map<String, Map<String, Object>> item) {
		log.info("Start -> dynamoDbRepository.getPutItem");

		Map<String, AttributeValue> putItem = new HashMap<>();

		item.entrySet().stream().forEach(attribute -> {
			String attributeName = attribute.getKey();

			AtomicReference<AttributeValue> attributeValue = new AtomicReference<>(new AttributeValue());

			attribute.getValue().entrySet().stream().forEach(attributeType -> {
				attributeValue.set(ItemUtils.toAttributeValue(attributeType.getValue()));
			});

			putItem.put(attributeName, attributeValue.get());
		});

		log.info("End -> dynamoDbRepository.getPutItem");
		return putItem;
	}

}
