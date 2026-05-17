package com.goggles.notification.domain.model;

import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
  PENDING("발송 대기") {
    @Override
    public Set<NotificationStatus> allowedTransitions() {
      return EnumSet.of(NotificationStatus.FAILED, NotificationStatus.SENT);
    }
  },
  SENT("발송 완료") {
    @Override
    public Set<NotificationStatus> allowedTransitions() {
      return EnumSet.noneOf(NotificationStatus.class);
    }
  },
  FAILED("발송 실패") {
    @Override
    public Set<NotificationStatus> allowedTransitions() {
      return EnumSet.noneOf(NotificationStatus.class);
    }
  };

  private final String displayName;

  public abstract Set<NotificationStatus> allowedTransitions();

  public boolean canTransitionTo(NotificationStatus next) {
    return allowedTransitions().contains(next);
  }
}
