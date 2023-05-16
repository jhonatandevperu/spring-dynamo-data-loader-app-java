package io.github.jhonatandevperu.springdynamodataloader.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

  private String accessKeyId;

  private String secretAccessKeyId;

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
