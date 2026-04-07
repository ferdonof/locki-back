package com.ferdonof.locki.enums;

import lombok.Getter;

@Getter
public enum ConstraintValidationsConstants {
  UK_USERS_EMAIL("uk_users_email");

  private final String value;

  ConstraintValidationsConstants(String value) {
    this.value = value;
  }

  public static ConstraintValidationsConstants from(String value) {
    if (value == null) {
      return null;
    }
    final String normalized = value.toLowerCase();
    for (final ConstraintValidationsConstants constant : values()) {
      if (normalized.contains(constant.getValue().toLowerCase())) {
        return constant;
      }
    }
    return null;
  }
}
