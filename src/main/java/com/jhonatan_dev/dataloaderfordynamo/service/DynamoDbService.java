package com.jhonatan_dev.dataloaderfordynamo.service;

import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;
import com.jhonatan_dev.dataloaderfordynamo.exceptions.InternalServerErrorException;
import com.jhonatan_dev.dataloaderfordynamo.exceptions.NotFoundException;

public interface DynamoDbService {

  void asyncLoadData(RequestBatchLoadDto body)
      throws InternalServerErrorException, NotFoundException;
}
