package com.jhonatan_dev.dataloaderfordynamo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "amazon")
public class AwsProperties {

	private String accessKey;

	private String secretKey;

	private String endpoint;

	private AwsRoleAccessProperties role;

	private AwsDynamoDBProperties dynamodb;

	@Data
	@NoArgsConstructor
	@ConfigurationProperties(prefix = "role")
	public static class AwsRoleAccessProperties {
		private String roleArn;

		private String externalId;

		private int timeSession;
	}

	@Data
	@NoArgsConstructor
	@ConfigurationProperties(prefix = "dynamodb")
	public static class AwsDynamoDBProperties {
		private String region;

	}

}
