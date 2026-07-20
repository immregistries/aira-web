package org.immregistries.aira.web;

/**
 * Configuration for the optional global search form.
 *
 * @param action form action URL
 * @param parameterName search field name
 * @param label accessible label
 * @param placeholder optional placeholder
 */
public record AiraSearchConfig(String action, String parameterName, String label, String placeholder) {

  /**
   * Creates search configuration.
   *
   * @param action form action URL
   * @param parameterName search field name
   * @param label accessible label
   * @param placeholder optional placeholder
   */
  public AiraSearchConfig {
    action = Validation.requireText(action, "action");
    parameterName = Validation.requireText(parameterName, "parameterName");
    label = Validation.requireText(label, "label");
    placeholder = Validation.optionalText(placeholder).orElse("");
  }
}
