package com.goggles.notification.infrastructure.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;

@Configuration
public class AwsSesConfig {

  @Value("${aws.ses.access-key}")
  private String accessKey;

  @Value("${aws.ses.secret-key}")
  private String secretKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.ses.endpoint-override:}")
  private String endpointOverride;

  @Bean
  @Profile("!load-test & !test")
  public SesV2Client amazonSimpleEmailService() {
    AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);

    var builder =
        SesV2Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials));

    if (!endpointOverride.isEmpty()) {
      builder.endpointOverride(URI.create(endpointOverride));
    }

    return builder.build();
  }

  @Bean
  @Profile({"load-test","test"})
  public SesV2Client mockSesV2Client() {
    SesV2Client mock = Mockito.mock(SesV2Client.class);
    when(mock.sendEmail(any(SendEmailRequest.class)))
        .thenReturn(SendEmailResponse.builder().messageId("mock-message-id").build());
    return mock;
  }
}
