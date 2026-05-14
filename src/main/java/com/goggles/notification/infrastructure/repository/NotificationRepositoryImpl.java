package com.goggles.notification.infrastructure.repository;

import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  public void updateNotificationsSent(List<UUID> ids, LocalDateTime sentAt) {
    notificationJpaRepository.updateSentByIds(ids, sentAt);
  }

  @Override
  public void updateNotificationsFailed(List<UUID> ids, String failureReason) {
    notificationJpaRepository.updateFailedByIds(ids, failureReason);
  }

  @Override
  public Optional<Notification> findById(UUID id) {
    return notificationJpaRepository.findById(id);
  }

  @Override
  public List<Notification> findByIds(List<UUID> ids) {
    return notificationJpaRepository.findAllById(ids);
  }
}
