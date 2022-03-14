package com.jhonatan_dev.dataloaderfordynamo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;
import com.jhonatan_dev.dataloaderfordynamo.repository.DynamoDbRepository;
import com.jhonatan_dev.dataloaderfordynamo.service.DynamoDbService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DynamoDbServiceImpl implements DynamoDbService {

	@Autowired
	private DynamoDbRepository dynamoDbRepository;

	@Override
	public void loadData(RequestBatchLoadDto body) throws Exception {
		log.info("Start -> dynamoDbService.loadData");

		dynamoDbRepository.loadData(body.getTableName(), body.getContent().getItems());

		log.info("End -> dynamoDbService.loadData");
	}

}
