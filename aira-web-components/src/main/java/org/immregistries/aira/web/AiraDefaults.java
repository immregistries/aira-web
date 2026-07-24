package org.immregistries.aira.web;

/**
 * Shared AIRA institutional URLs and required footer wording.
 */
public final class AiraDefaults {

  /** Public resource path for the shared AIRA stylesheet. */
  public static final String STYLESHEET_PATH = "/aira/css/aira.css";

  /** Public resource path for the packaged default horizontal AIRA logo. */
  public static final String DEFAULT_LOGO_PATH = "/aira/images/AIRA_2018Logo_BlueGreen_Hor.png";

  /** Recommended alternative text for the packaged default AIRA logo. */
  public static final String DEFAULT_LOGO_ALT_TEXT = "American Immunization Registry Association";

  /** Official AIRA website URL. */
  public static final String AIRA_WEBSITE_URL = "https://www.immregistries.org/";

  /** Default official AIRA privacy policy URL. */
  public static final String PRIVACY_POLICY_URL =
      "https://aira.memberclicks.net/assets/docs/Organizational_Docs/"
          + "AIRA%20Privacy%20Policy%20-%20Updated%20Jan%202026%20Final.pdf";

  /** Default official AIRA terms of use URL. */
  public static final String TERMS_OF_USE_URL =
      "https://aira.memberclicks.net/assets/docs/Organizational_Docs/"
          + "AIRA%20Terms%20of%20Use%20-%20Updated%202026%20Final.pdf";

  /** Required institutional footer description. */
  public static final String INSTITUTIONAL_DESCRIPTION =
      "supports collaboration, standards, and shared solutions that strengthen immunization information "
          + "systems and improve the use of vaccination data to protect public health.";

  /** Required copyright owner and rights wording. */
  public static final String COPYRIGHT_WORDING =
      "American Immunization Registry Association. All rights reserved.";

  private AiraDefaults() {
  }
}
