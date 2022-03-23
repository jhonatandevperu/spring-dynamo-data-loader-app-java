package com.jhonatan_dev.dataloaderfordynamo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.jhonatan_dev.dataloaderfordynamo.properties.AwsProperties;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@EnableConfigurationProperties(value = {AwsProperties.class})
public class AwsConfig {

  private final AwsProperties awsProperties;

  @Autowired
  public AwsConfig(AwsProperties awsProperties){
    this.awsProperties = awsProperties;
  }

  @Bean("amazonDynamoDB")
  protected AmazonDynamoDB amazonDynamoDB() {

    if (awsProperties.getEndpoint() != null) {
      EndpointConfiguration endpointConfiguration =
          new EndpointConfiguration(
              awsProperties.getEndpoint(),
              awsProperties.getDynamodb().getRegion() != null
                  ? awsProperties.getDynamodb().getRegion()
                  : null);

      return AmazonDynamoDBClientBuilder.standard()
          .withCredentials(amazonAWSCredentialsProvider())
          .withEndpointConfiguration(endpointConfiguration)
          .build();
    } else if (awsProperties.getDynamodb().getRegion() != null) {
      return AmazonDynamoDBClientBuilder.standard()
          .withCredentials(amazonAWSCredentialsProvider())
          .withRegion(Regions.fromName(awsProperties.getDynamodb().getRegion()))
          .build();
    } else {
      return AmazonDynamoDBClientBuilder.defaultClient();
    }
  }

  @Bean("dynamoDB")
  protected DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
    return new DynamoDB(amazonDynamoDB);
  }

  protected AWSCredentialsProvider amazonAWSCredentialsProvider() {
    if (awsProperties.getAccessKey() != null && awsProperties.getSecretKey() != null) {

      log.info(
          "AWSCredentialsProvider access/secret keys: "
              + awsProperties.getAccessKey()
              + " , "
              + awsProperties.getSecretKey());

      return new AWSStaticCredentialsProvider(
          new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey()));
    }

    if (awsProperties.getRole() != null && awsProperties.getRole().getRoleArn() != null) {

      log.info(
          "AWSCredentialsProvider role/external ids: "
              + awsProperties.getRole().getRoleArn()
              + " , "
              + awsProperties.getRole().getExternalId());

      String randomSesionName = UUID.randomUUID().toString();

      AWSSecurityTokenService stsClient =
          AWSSecurityTokenServiceClientBuilder.standard()
              .withRegion(Regions.fromName(awsProperties.getDynamodb().getRegion()))
              .build();

      return
          new STSAssumeRoleSessionCredentialsProvider.Builder(
                  awsProperties.getRole().getRoleArn(), randomSesionName)
              .withExternalId(awsProperties.getRole().getExternalId())
              .withStsClient(stsClient)
              .withRoleSessionDurationSeconds(awsProperties.getRole().getTimeSession())
              .build();
    }

    return DefaultAWSCredentialsProviderChain.getInstance();
  }
}
