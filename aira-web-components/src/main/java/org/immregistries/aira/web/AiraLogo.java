package org.immregistries.aira.web;

/**
 * Optional approved AIRA logo configuration for the application header.
 *
 * @param source application-relative or absolute image source
 * @param alternativeText accessible alternative text for the approved logo
 */
public record AiraLogo(String source, String alternativeText) {

  /**
   * Creates an approved logo reference.
   *
   * @param source application-relative or absolute image source
   * @param alternativeText accessible alternative text
   */
  public AiraLogo {
    source = Validation.requireText(source, "source");
    alternativeText = Validation.requireText(alternativeText, "alternativeText");
  }
}

