package com.goggles.notification.application.service;

import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;
import com.goggles.notification.domain.event.NotificationEvents;
import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.model.Receiver;
import com.goggles.notification.domain.model.Reference;
import com.goggles.notification.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {
  private final NotificationEvents notificationEvents;
  private final NotificationRepository notificationRepository;

  @Override
  @Transactional
  public void sendNotification(SendNotificationCommand command) {
    Notification notification = toNotification(command);

    notification = notificationRepository.createNotification(notification);
    notificationEvents.emailSendRequested(
        new EmailSendRequestedEvent(
            notification.getId(),
            command.receiverEmail(),
            command.receiverName(),
            notification.getTitle(),
            notification.getContent(),
            command.templateVariables()
        )
    );
  }

  @Override
  @Transactional
  public void sendBulkNotification(List<SendNotificationCommand> commands) {
    List<Notification> notifications = commands.stream()
        .map(this::toNotification)
        .toList();
    notifications = notificationRepository.createNotifications(notifications);

    List<Notification> savedNotifications = notifications;
    List<BulkEmailSendRequestedEvent.BulkEmailTarget> targets =
        IntStream.range(0, commands.size())
            .mapToObj(i -> {
              SendNotificationCommand command = commands.get(i);
              UUID notificationId = savedNotifications.get(i).getId();
              return new BulkEmailSendRequestedEvent.BulkEmailTarget(
                  notificationId,
                  command.receiverEmail(),
                  command.receiverName(),
                  command.title(),
                  command.templateVariables()
              );
            })
            .toList();

    notificationEvents.bulkEmailSendRequested(
        new BulkEmailSendRequestedEvent(
            notifications.stream().map(Notification::getId).toList(),
            targets
        )
    );
  }

  public Notification toNotification(SendNotificationCommand command) {
    return Notification.create(
        new Receiver(command.receiverId(), command.receiverType()),
        new Reference(command.referenceType(), command.referenceId()),
        command.type(),
        command.channel(),
        command.title(),
        command.content());
  }
}
