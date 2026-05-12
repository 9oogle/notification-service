package com.goggles.notification.domain.model;

import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
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
}