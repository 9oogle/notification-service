package com.goggles.notification.infrastructure.repository;

import com.goggles.notification.domain.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<Notification, UUID> {
  @Modifying(clearAutomatically = true)
  @Query(
      "UPDATE Notification n "
          + "SET n.status = com.goggles.notification.domain.model.NotificationStatus.SENT, "
          + "n.sentAt = :sentAt "
          + "WHERE n.id IN :ids")
  void updateSentByIds(@Param("ids") List<UUID> ids, @Param("sentAt") LocalDateTime sentAt);

  @Modifying(clearAutomatically = true)
  @Query(
      "UPDATE Notification n "
          + "SET n.status = com.goggles.notification.domain.model.NotificationStatus.FAILED, n.failureReason = :failureReason "
          + "WHERE n.id IN :ids")
  void updateFailedByIds(@Param("ids") List<UUID> ids, String failureReason);
}
