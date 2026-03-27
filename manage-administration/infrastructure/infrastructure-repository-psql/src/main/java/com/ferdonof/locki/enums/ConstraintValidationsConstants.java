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
    for (final ConstraintValidationsConstants constant : values()) {
      if (constant.getValue().equalsIgnoreCase(value)) {
        return constant;
      }
    }
    return null;
  }
}
