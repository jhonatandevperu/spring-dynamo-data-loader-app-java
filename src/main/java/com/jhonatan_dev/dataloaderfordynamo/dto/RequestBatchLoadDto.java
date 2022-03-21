package com.jhonatan_dev.dataloaderfordynamo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBatchLoadDto implements Serializable {

  private static final long serialVersionUID = 541066496524587973L;

  @NotNull
  @NotEmpty
  @JsonProperty("content")
  private RequestBatchLoadContentDto content;

  @NotNull
  @NotEmpty
  @NotBlank
  @JsonProperty("table_name")
  private String tableName;
}
