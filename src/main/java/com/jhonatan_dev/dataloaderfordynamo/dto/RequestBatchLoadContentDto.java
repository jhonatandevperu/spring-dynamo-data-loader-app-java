package com.jhonatan_dev.dataloaderfordynamo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBatchLoadContentDto implements Serializable {

	private static final long serialVersionUID = 7530898663271126982L;

	@NotNull
	@NotEmpty
	@JsonProperty("Items")
	private List<Map<String, Map<String, Object>>> items;
}
