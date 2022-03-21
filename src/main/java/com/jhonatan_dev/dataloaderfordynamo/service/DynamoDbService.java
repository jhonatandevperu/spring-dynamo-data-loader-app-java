package com.jhonatan_dev.dataloaderfordynamo.service;

import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;

public interface DynamoDbService {

  void loadData(RequestBatchLoadDto body) throws Exception;

  void asyncLoadData(RequestBatchLoadDto body) throws Exception;
}
