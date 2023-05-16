package io.github.jhonatandevperu.springdynamodataloader.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class NotFoundException extends Exception {

  @JsonProperty("message")
  private final String mensaje;
}
