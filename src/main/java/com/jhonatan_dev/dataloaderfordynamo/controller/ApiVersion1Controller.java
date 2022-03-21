package com.jhonatan_dev.dataloaderfordynamo.controller;

import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;
import com.jhonatan_dev.dataloaderfordynamo.service.DynamoDbService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/v1")
public class ApiVersion1Controller {

  @Autowired private DynamoDbService dynamoDbService;

  @PostMapping(
      value = "/batchload",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  void loadData(@RequestBody RequestBatchLoadDto body) throws Exception {
    log.info("Start -> v1Controller.loadData");

    dynamoDbService.asyncLoadData(body);

    log.info("End -> v1Controller.loadData");
  }
}
