package com.goggles.notification.infrastructure.repository;

import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationJpaRepository notificationJpaRepository;

  @Override
  public Notification createNotification(Notification notification) {
    return notificationJpaRepository.save(notification);
  }

  @Override
  public List<Notification> createNotifications(List<Notification> notifications) {
    return notificationJpaRepository.saveAll(notifications);
  }

  @Override
  public void updateNotificationsSent(List<UUID> ids) {
    notificationJpaRepository.updateSentByIds(ids);
  }

  @Override
  public void updateNotificationsFailed(List<UUID> ids, String failureReason) {
    notificationJpaRepository.updateFailedByIds(ids, failureReason);
  }
}
