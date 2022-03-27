package com.jhonatan_dev.dataloaderfordynamo.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadContentDto;
import com.jhonatan_dev.dataloaderfordynamo.dto.RequestBatchLoadDto;
import com.jhonatan_dev.dataloaderfordynamo.service.DynamoDbService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiVersion1Controller.class)
class ApiVersion1ControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private DynamoDbService dynamoDbService;

  @Test
  void testLoadData() throws Exception {

    RequestBatchLoadContentDto requestBatchLoadContentDto =
        RequestBatchLoadContentDto.builder().items(new ArrayList<>()).build();

    RequestBatchLoadDto requestBatchLoadDto =
        RequestBatchLoadDto.builder()
            .tableName("myTable")
            .content(requestBatchLoadContentDto)
            .build();

    doNothing().when(dynamoDbService).asyncLoadData(requestBatchLoadDto);

    mvc.perform(
            post("/api/v1/batchload")
                .content(objectMapper.writeValueAsBytes(requestBatchLoadDto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
