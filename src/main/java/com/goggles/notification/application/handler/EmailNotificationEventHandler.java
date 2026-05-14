package com.goggles.notification.application.handler;

import com.goggles.notification.application.port.EmailSender;
import com.goggles.notification.application.service.NotificationStatusService;
import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationEventHandler {
  private final EmailSender emailSender;
  private final NotificationStatusService notificationStatusService;

  public void handle(EmailSendRequestedEvent event) {
    try {
      emailSender.send(event);
      notificationStatusService.markSent(event.notificationId());
    } catch (Exception e) {
      log.error("[Email] 발송 실패. notificationId: {}", event.notificationId(), e);
      notificationStatusService.markFailed(event.notificationId(), "서버 실패");
      throw e;
    }
  }

  public void bulkHandle(BulkEmailSendRequestedEvent event) {
    try {
      emailSender.sendBulk(event);
      notificationStatusService.markAllSent(event.notificationIds());
    } catch (Exception e) {
      log.error("[Bulk Email] 발송 실패. count: {}", event.notificationIds().size(), e);
      notificationStatusService.markAllFailed(event.notificationIds(), "서버 실패");
      throw e;
    }
  }
}
