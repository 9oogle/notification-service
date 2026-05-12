package com.goggles.notification.infrastructure.config;

import com.goggles.notification.application.service.EmailNotificationServiceImpl;
import com.goggles.notification.application.service.NotificationService;
import com.goggles.notification.domain.model.NotificationChannel;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class NotificationServiceConfig {

  private final EmailNotificationServiceImpl emailNotificationService;

  @Bean
  public Map<NotificationChannel, NotificationService> notificationServiceMap() {
    return Map.of(
        NotificationChannel.EMAIL, emailNotificationService
    );
  }
}
