package io.github.jhonatandevperu.springdynamodataloader.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
public class RequestBatchLoadContentDto implements Serializable {

  @Serial private static final long serialVersionUID = 7530898663271126982L;

  @NotNull
  @NotEmpty
  @JsonProperty("Items")
  private List<Map<String, Map<String, Object>>> items;
}
