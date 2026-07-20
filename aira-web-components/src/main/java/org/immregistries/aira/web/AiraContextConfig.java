package org.immregistries.aira.web;

import java.util.List;

/**
 * Optional contextual title and navigation rendered below the global header.
 *
 * @param title contextual title
 * @param navigationItems contextual navigation links
 */
public record AiraContextConfig(String title, List<AiraNavigationItem> navigationItems) {

  /**
   * Creates contextual shell configuration.
   *
   * @param title contextual title
   * @param navigationItems contextual navigation links
   */
  public AiraContextConfig {
    title = Validation.requireText(title, "title");
    navigationItems = navigationItems == null ? List.of() : List.copyOf(navigationItems);
  }
}
