package org.immregistries.aira.web;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Renders the reusable AIRA HTML document shell around application-generated content.
 */
public final class AiraPage {

  private static final String MAIN_CONTENT_ID = "main-content";

  private final String applicationName;
  private final String applicationVersion;
  private final Optional<String> applicationSubtitle;
  private final String documentTitle;
  private final Optional<String> pageHeading;
  private final Optional<String> pageIntro;
  private final String contextPath;
  private final Optional<String> identityHref;
  private final Optional<AiraLogo> logo;
  private final List<AiraNavigationItem> navigationItems;
  private final List<String> localStylesheets;
  private final AiraFooterConfig footerConfig;

  private AiraPage(Builder builder) {
    this.applicationName = Validation.requireText(builder.applicationName, "applicationName");
    this.applicationVersion = Validation.requireText(builder.applicationVersion, "applicationVersion");
    this.applicationSubtitle = Validation.optionalText(builder.applicationSubtitle);
    this.documentTitle = Validation.requireText(
        builder.documentTitle == null ? builder.pageTitle : builder.documentTitle, "documentTitle");
    this.pageHeading = Validation.optionalText(builder.pageHeading);
    this.pageIntro = Validation.optionalText(builder.pageIntro);
    this.contextPath = normalizeContextPath(builder.contextPath);
    this.identityHref = Validation.optionalText(builder.identityHref);
    this.logo = Optional.ofNullable(builder.logo);
    this.navigationItems = List.copyOf(builder.navigationItems);
    this.localStylesheets = List.copyOf(builder.localStylesheets);
    this.footerConfig = builder.footerConfig == null ? AiraFooterConfig.defaults() : builder.footerConfig;
  }

  /**
   * Creates a page builder.
   *
   * @return builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Writes the document start, header, optional navigation, and opening main element.
   *
   * @param writer destination writer
   */
  public void writeStart(PrintWriter writer) {
    writer.println("<!doctype html>");
    writer.println("<html lang=\"en\">");
    writer.println("<head>");
    writer.println("  <meta charset=\"UTF-8\">");
    writer.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
    writer.println("  <title>" + HtmlEscaper.escapeText(documentTitle) + "</title>");
    writer.println("  <link rel=\"stylesheet\" href=\"" + HtmlEscaper.escapeAttribute(resolvePath("/aira/css/aira.css")) + "\">");
    for (String stylesheet : localStylesheets) {
      writer.println("  <link rel=\"stylesheet\" href=\"" + HtmlEscaper.escapeAttribute(resolvePath(stylesheet)) + "\">");
    }
    writer.println("</head>");
    writer.println("<body>");
    writer.println("<div class=\"aira-app\">");
    writer.println("  <a class=\"aira-skip-link\" href=\"#" + MAIN_CONTENT_ID + "\">Skip to main content</a>");
    writeHeader(writer);
    writeNavigation(writer);
    writer.println("  <main class=\"aira-main\" id=\"" + MAIN_CONTENT_ID + "\">");
    if (pageHeading.isPresent() || pageIntro.isPresent()) {
      writer.println("    <div class=\"aira-page-header\">");
      writer.println("      <div>");
      pageHeading.ifPresent(heading ->
          writer.println("        <h1 class=\"aira-page-title\">" + HtmlEscaper.escapeText(heading) + "</h1>"));
      pageIntro.ifPresent(intro ->
          writer.println("        <p class=\"aira-page-intro\">" + HtmlEscaper.escapeText(intro) + "</p>"));
      writer.println("      </div>");
      writer.println("    </div>");
    }
  }

  /**
   * Writes the closing main element, required footer, and document end.
   *
   * @param writer destination writer
   */
  public void writeEnd(PrintWriter writer) {
    writer.println("  </main>");
    writeFooter(writer);
    writer.println("</div>");
    writer.println("</body>");
    writer.println("</html>");
  }

  /**
   * @return configured navigation items
   */
  public List<AiraNavigationItem> navigationItems() {
    return navigationItems;
  }

  /**
   * @return configured local stylesheets
   */
  public List<String> localStylesheets() {
    return localStylesheets;
  }

  private void writeHeader(PrintWriter writer) {
    writer.println("  <header class=\"aira-header\">");
    writer.println("    <div class=\"aira-brand\">");
    String identityStart = identityHref
        .map(href -> "<a class=\"aira-identity-link\" href=\"" + HtmlEscaper.escapeAttribute(resolvePath(href)) + "\">")
        .orElse("");
    String identityEnd = identityHref.isPresent() ? "</a>" : "";
    if (!identityStart.isEmpty()) {
      writer.println("      " + identityStart);
    }
    logo.ifPresentOrElse(
        airaLogo -> writer.println("        <img class=\"aira-logo\" src=\""
            + HtmlEscaper.escapeAttribute(resolvePath(airaLogo.source())) + "\" alt=\""
            + HtmlEscaper.escapeAttribute(airaLogo.alternativeText()) + "\">"),
        () -> writer.println("        <!-- Approved AIRA logo asset will be placed here once available. -->"));
    writer.println("        <span class=\"aira-app-name\">" + HtmlEscaper.escapeText(applicationName)
        + applicationSubtitle.map(subtitle -> "<span class=\"aira-app-subtitle\">"
        + HtmlEscaper.escapeText(subtitle) + "</span>").orElse("") + "</span>");
    if (!identityEnd.isEmpty()) {
      writer.println("      " + identityEnd);
    }
    writer.println("    </div>");
    writer.println("  </header>");
  }

  private void writeNavigation(PrintWriter writer) {
    if (navigationItems.isEmpty()) {
      return;
    }
    writer.println("  <nav class=\"aira-primary-nav\" aria-label=\"Primary navigation\">");
    for (AiraNavigationItem item : navigationItems) {
      writer.print("    <a href=\"" + HtmlEscaper.escapeAttribute(resolvePath(item.href())) + "\"");
      if (item.active()) {
        writer.print(" aria-current=\"page\"");
      }
      writer.println(">" + HtmlEscaper.escapeText(item.label()) + "</a>");
    }
    writer.println("  </nav>");
  }

  private void writeFooter(PrintWriter writer) {
    writer.println("  <footer class=\"aira-footer\" aria-label=\"AIRA application information\">");
    writer.println("    <p><a href=\"" + HtmlEscaper.escapeAttribute(footerConfig.airaWebsiteUrl())
        + "\">American Immunization Registry Association (AIRA)</a> "
        + HtmlEscaper.escapeText(AiraDefaults.INSTITUTIONAL_DESCRIPTION) + "</p>");
    writer.println("    <p>" + HtmlEscaper.escapeText(applicationName) + " version "
        + HtmlEscaper.escapeText(applicationVersion) + " &middot; &copy; Copyright "
        + footerConfig.copyrightYear() + ", " + HtmlEscaper.escapeText(AiraDefaults.COPYRIGHT_WORDING)
        + " <a href=\"" + HtmlEscaper.escapeAttribute(footerConfig.privacyPolicyUrl())
        + "\">Privacy Policy</a> &middot; <a href=\""
        + HtmlEscaper.escapeAttribute(footerConfig.termsOfUseUrl())
        + "\">Terms and Conditions of Use</a></p>");
    writer.println("  </footer>");
  }

  private String resolvePath(String path) {
    String value = Validation.requireText(path, "path");
    if (value.startsWith("http://") || value.startsWith("https://") || value.startsWith("#")) {
      return value;
    }
    if (!value.startsWith("/")) {
      value = "/" + value;
    }
    return contextPath + value;
  }

  private static String normalizeContextPath(String value) {
    String normalized = value == null ? "" : value.trim();
    if (normalized.isEmpty() || "/".equals(normalized)) {
      return "";
    }
    return normalized.startsWith("/") ? normalized : "/" + normalized;
  }

  /**
   * Builder for {@link AiraPage}.
   */
  public static final class Builder {
    private String applicationName;
    private String applicationVersion;
    private String applicationSubtitle;
    private String documentTitle;
    private String pageTitle;
    private String pageHeading;
    private String pageIntro;
    private String contextPath = "";
    private String identityHref;
    private AiraLogo logo;
    private final List<AiraNavigationItem> navigationItems = new ArrayList<>();
    private final List<String> localStylesheets = new ArrayList<>();
    private AiraFooterConfig footerConfig;

    private Builder() {
    }

    /** Sets the application name. */
    public Builder applicationName(String applicationName) {
      this.applicationName = applicationName;
      return this;
    }

    /** Sets the application version used in the footer. */
    public Builder applicationVersion(String applicationVersion) {
      this.applicationVersion = applicationVersion;
      return this;
    }

    /** Sets an optional application subtitle for the header. */
    public Builder applicationSubtitle(String applicationSubtitle) {
      this.applicationSubtitle = applicationSubtitle;
      return this;
    }

    /** Sets the document title. */
    public Builder documentTitle(String documentTitle) {
      this.documentTitle = documentTitle;
      return this;
    }

    /** Sets the document title for callers that prefer page-title terminology. */
    public Builder pageTitle(String pageTitle) {
      this.pageTitle = pageTitle;
      return this;
    }

    /** Sets an optional visible page heading. */
    public Builder pageHeading(String pageHeading) {
      this.pageHeading = pageHeading;
      return this;
    }

    /** Sets optional introductory page text. */
    public Builder pageIntro(String pageIntro) {
      this.pageIntro = pageIntro;
      return this;
    }

    /** Sets the servlet context path used to resolve application-relative resources. */
    public Builder contextPath(String contextPath) {
      this.contextPath = contextPath;
      return this;
    }

    /** Sets an optional header identity link. */
    public Builder identityHref(String identityHref) {
      this.identityHref = identityHref;
      return this;
    }

    /** Sets an optional approved logo. */
    public Builder logo(AiraLogo logo) {
      this.logo = logo;
      return this;
    }

    /** Adds a primary navigation item. */
    public Builder addNavigationItem(String label, String href, boolean active) {
      this.navigationItems.add(new AiraNavigationItem(label, href, active));
      return this;
    }

    /** Adds a local stylesheet to render after the shared AIRA stylesheet. */
    public Builder addLocalStylesheet(String stylesheetPath) {
      this.localStylesheets.add(Validation.requireText(stylesheetPath, "stylesheetPath"));
      return this;
    }

    /** Sets footer configuration. */
    public Builder footerConfig(AiraFooterConfig footerConfig) {
      this.footerConfig = footerConfig;
      return this;
    }

    /** Builds the page renderer. */
    public AiraPage build() {
      return new AiraPage(this);
    }
  }
}
