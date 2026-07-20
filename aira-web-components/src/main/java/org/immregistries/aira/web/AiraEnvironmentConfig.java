package org.immregistries.aira.web;

import java.util.Optional;

/**
 * Non-production environment indicator configuration.
 *
 * @param label short visible label, such as Local or Test
 * @param description optional accessible description
 */
public record AiraEnvironmentConfig(String label, String description) {

  /**
   * Creates environment configuration.
   *
   * @param label short visible label
   * @param description optional accessible description
   */
  public AiraEnvironmentConfig {
    label = Validation.requireText(label, "label");
    description = Validation.optionalText(description).orElse("");
  }

  /**
   * @return optional accessible description
   */
  public Optional<String> descriptionValue() {
    return Validation.optionalText(description);
  }
}
