package com.goggles.notification.infrastructure.repository;

import com.goggles.notification.domain.model.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, UUID> {

}
