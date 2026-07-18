package org.immregistries.aira.web;

import java.time.Year;

/**
 * Configuration for the required AIRA institutional footer.
 */
public final class AiraFooterConfig {

  private final String airaWebsiteUrl;
  private final String privacyPolicyUrl;
  private final String termsOfUseUrl;
  private final int copyrightYear;

  private AiraFooterConfig(Builder builder) {
    this.airaWebsiteUrl = Validation.requireText(builder.airaWebsiteUrl, "airaWebsiteUrl");
    this.privacyPolicyUrl = Validation.requireText(builder.privacyPolicyUrl, "privacyPolicyUrl");
    this.termsOfUseUrl = Validation.requireText(builder.termsOfUseUrl, "termsOfUseUrl");
    this.copyrightYear = builder.copyrightYear;
  }

  /**
   * Creates a footer configuration using approved defaults and the current year.
   *
   * @return default footer configuration
   */
  public static AiraFooterConfig defaults() {
    return builder().build();
  }

  /**
   * Creates a footer configuration builder.
   *
   * @return builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return AIRA website URL
   */
  public String airaWebsiteUrl() {
    return airaWebsiteUrl;
  }

  /**
   * @return privacy policy URL
   */
  public String privacyPolicyUrl() {
    return privacyPolicyUrl;
  }

  /**
   * @return terms of use URL
   */
  public String termsOfUseUrl() {
    return termsOfUseUrl;
  }

  /**
   * @return copyright year
   */
  public int copyrightYear() {
    return copyrightYear;
  }

  /**
   * Builder for {@link AiraFooterConfig}.
   */
  public static final class Builder {
    private String airaWebsiteUrl = AiraDefaults.AIRA_WEBSITE_URL;
    private String privacyPolicyUrl = AiraDefaults.PRIVACY_POLICY_URL;
    private String termsOfUseUrl = AiraDefaults.TERMS_OF_USE_URL;
    private int copyrightYear = Year.now().getValue();

    private Builder() {
    }

    /**
     * Overrides the AIRA website URL.
     *
     * @param airaWebsiteUrl AIRA website URL
     * @return this builder
     */
    public Builder airaWebsiteUrl(String airaWebsiteUrl) {
      this.airaWebsiteUrl = airaWebsiteUrl;
      return this;
    }

    /**
     * Overrides the privacy policy URL.
     *
     * @param privacyPolicyUrl privacy policy URL
     * @return this builder
     */
    public Builder privacyPolicyUrl(String privacyPolicyUrl) {
      this.privacyPolicyUrl = privacyPolicyUrl;
      return this;
    }

    /**
     * Overrides the terms of use URL.
     *
     * @param termsOfUseUrl terms of use URL
     * @return this builder
     */
    public Builder termsOfUseUrl(String termsOfUseUrl) {
      this.termsOfUseUrl = termsOfUseUrl;
      return this;
    }

    /**
     * Overrides the copyright year.
     *
     * @param copyrightYear copyright year
     * @return this builder
     */
    public Builder copyrightYear(int copyrightYear) {
      this.copyrightYear = copyrightYear;
      return this;
    }

    /**
     * Builds the footer configuration.
     *
     * @return footer configuration
     */
    public AiraFooterConfig build() {
      return new AiraFooterConfig(this);
    }
  }
}

