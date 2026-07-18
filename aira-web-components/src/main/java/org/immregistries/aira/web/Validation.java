package org.immregistries.aira.web;

import java.util.Optional;

final class Validation {

  private Validation() {
  }

  static String requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " is required");
    }
    return value.trim();
  }

  static Optional<String> optionalText(String value) {
    if (value == null || value.isBlank()) {
      return Optional.empty();
    }
    return Optional.of(value.trim());
  }
}

