package com.goggles.notification.infrastructure.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics.notification")
public record NotificationTopics(String emailSendRequested, String bulkEmailSendRequested) {}
