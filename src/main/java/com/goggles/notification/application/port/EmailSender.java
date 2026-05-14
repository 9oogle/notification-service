package com.goggles.notification.application.port;

import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;

public interface EmailSender {
  void send(EmailSendRequestedEvent event);
  void sendBulk(BulkEmailSendRequestedEvent event);
}
