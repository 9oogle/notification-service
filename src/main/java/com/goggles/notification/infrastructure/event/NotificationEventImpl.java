package com.goggles.notification.infrastructure.event;

import com.goggles.common.event.Events;
import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;
import com.goggles.notification.domain.event.NotificationEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(NotificationTopics.class)
public class NotificationEventImpl implements NotificationEvents {

  private final NotificationTopics notificationTopics;
  private final Events events;

  public static final String DOMAIN = "NOTIFICATION";

  @Override
  public void emailSendRequested(EmailSendRequestedEvent event) {
    events.trigger(
        event.notificationId().toString() + ":email-send-requested",
        DOMAIN,
        notificationTopics.emailSendRequested(),
        event);
  }

  @Override
  public void bulkEmailSendRequested(BulkEmailSendRequestedEvent event) {
    events.trigger(
        event.notificationIds().getFirst() + ":bulk-email-send-requested",
        DOMAIN,
        notificationTopics.bulkEmailSendRequested(),
        event);
  }
}
