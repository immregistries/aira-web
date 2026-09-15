package org.immregistries.aira.web;

/**
 * A link or button-like link rendered in the global shell.
 *
 * @param label visible text
 * @param href destination URL
 * @param variant button variant such as primary, secondary, tertiary, ghost, or link
 * @param badgeCount optional count rendered as a badge on the action; null or non-positive means
 *     no badge
 */
public record AiraActionItem(String label, String href, String variant, Integer badgeCount) {
  /**
   * Creates an action item.
   *
   * @param label visible text
   * @param href destination URL
   * @param variant button variant
   * @param badgeCount optional badge count; null or non-positive renders no badge
   */
  public AiraActionItem {
    label = Validation.requireText(label, "label");
    href = Validation.requireText(href, "href");
    variant = Validation.optionalText(variant).orElse("ghost");
    switch (variant) {
      case "primary", "secondary", "tertiary", "success", "danger", "ghost", "link" -> {
        // Valid variant.
      }
      default -> throw new IllegalArgumentException(
          "variant must be one of: primary, secondary, tertiary, success, danger, ghost, link");
    }
    if (badgeCount != null && badgeCount <= 0) {
      badgeCount = null;
    }
  }

  /**
   * Creates an action item with no badge.
   *
   * @param label visible text
   * @param href destination URL
   * @param variant button variant
   */
  public AiraActionItem(String label, String href, String variant) {
    this(label, href, variant, null);
  }
}
