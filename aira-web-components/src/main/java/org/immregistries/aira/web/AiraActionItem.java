package org.immregistries.aira.web;

/**
 * A link or button-like link rendered in the global shell.
 *
 * @param label visible text
 * @param href destination URL
 * @param variant button variant such as primary, secondary, ghost, or link
 */
public record AiraActionItem(String label, String href, String variant) {
  /**
   * Creates an action item.
   *
   * @param label visible text
   * @param href destination URL
   * @param variant button variant
   */
  public AiraActionItem {
    label = Validation.requireText(label, "label");
    href = Validation.requireText(href, "href");
    variant = Validation.optionalText(variant).orElse("ghost");
    switch (variant) {
      case "primary", "secondary", "success", "danger", "ghost", "link" -> {
        // Valid variant.
      }
      default -> throw new IllegalArgumentException(
          "variant must be one of: primary, secondary, success, danger, ghost, link");
    }
  }
}
