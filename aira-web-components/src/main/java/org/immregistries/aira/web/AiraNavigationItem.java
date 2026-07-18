package org.immregistries.aira.web;

/**
 * A primary navigation item in the AIRA application shell.
 *
 * @param label visible link text
 * @param href link destination
 * @param active whether this item represents the current page
 */
public record AiraNavigationItem(String label, String href, boolean active) {

  /**
   * Creates a navigation item.
   *
   * @param label visible link text
   * @param href link destination
   * @param active whether the link is active
   */
  public AiraNavigationItem {
    label = Validation.requireText(label, "label");
    href = Validation.requireText(href, "href");
  }
}

