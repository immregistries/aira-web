package org.immregistries.aira.web;

import java.util.Optional;

/**
 * Configuration for optional account or sign-in controls in the global header.
 *
 * @param label visible account label, such as a user name
 * @param actionLabel action link text
 * @param actionHref action link destination
 */
public record AiraAccountConfig(String label, String actionLabel, String actionHref) {

  /**
   * Creates account configuration.
   *
   * @param label visible account label
   * @param actionLabel action link text
   * @param actionHref action link destination
   */
  public AiraAccountConfig {
    label = Validation.optionalText(label).orElse("");
    actionLabel = Validation.requireText(actionLabel, "actionLabel");
    actionHref = Validation.requireText(actionHref, "actionHref");
  }

  /**
   * @return optional visible account label
   */
  public Optional<String> labelValue() {
    return Validation.optionalText(label);
  }
}
