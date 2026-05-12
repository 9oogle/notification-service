package com.goggles.notification.infrastructure.repository;

import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.repository.NotificationRepository;
import java.util.List;
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
  public void createNotifications(List<Notification> notifications) {
    notificationJpaRepository.saveAll(notifications);
  }
}
