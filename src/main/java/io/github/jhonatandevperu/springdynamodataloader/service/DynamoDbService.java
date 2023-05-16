package io.github.jhonatandevperu.springdynamodataloader.service;

import io.github.jhonatandevperu.springdynamodataloader.dto.RequestBatchLoadDto;
import io.github.jhonatandevperu.springdynamodataloader.exceptions.InternalServerErrorException;
import io.github.jhonatandevperu.springdynamodataloader.exceptions.NotFoundException;

public interface DynamoDbService {

  void asyncLoadData(RequestBatchLoadDto body)
      throws InternalServerErrorException, NotFoundException;
}
