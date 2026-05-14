package com.goggles.notification.domain.event;

public interface NotificationEvents {
  void emailSendRequested(EmailSendRequestedEvent event);
  void bulkEmailSendRequested(BulkEmailSendRequestedEvent event);
}