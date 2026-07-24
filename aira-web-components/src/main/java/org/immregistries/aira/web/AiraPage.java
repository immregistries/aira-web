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
  private final String mainClass;
  private final String contextPath;
  private final Optional<String> identityHref;
  private final Optional<AiraLogo> logo;
  private final List<String> localStylesheets;
  private final List<AiraActionItem> globalActions;
  private final Optional<AiraSearchConfig> searchConfig;
  private final Optional<AiraAccountConfig> accountConfig;
  private final Optional<AiraEnvironmentConfig> environmentConfig;
  private final Optional<AiraContextConfig> contextConfig;
  private final AiraFooterConfig footerConfig;

  private AiraPage(Builder builder) {
    this.applicationName = Validation.requireText(builder.applicationName, "applicationName");
    this.applicationVersion = Validation.requireText(builder.applicationVersion, "applicationVersion");
    this.applicationSubtitle = Validation.optionalText(builder.applicationSubtitle);
    this.documentTitle = Validation.requireText(
        builder.documentTitle == null ? builder.pageTitle : builder.documentTitle, "documentTitle");
    this.pageHeading = Validation.optionalText(builder.pageHeading);
    this.pageIntro = Validation.optionalText(builder.pageIntro);
    this.mainClass = Validation.optionalText(builder.mainClass).orElse("aira-main");
    this.contextPath = normalizeContextPath(builder.contextPath);
    this.identityHref = Validation.optionalText(builder.identityHref);
    this.logo = Optional.ofNullable(builder.logo);
    this.localStylesheets = List.copyOf(builder.localStylesheets);
    this.globalActions = List.copyOf(builder.globalActions);
    this.searchConfig = Optional.ofNullable(builder.searchConfig);
    this.accountConfig = Optional.ofNullable(builder.accountConfig);
    this.environmentConfig = Optional.ofNullable(builder.environmentConfig);
    this.contextConfig = Optional.ofNullable(builder.contextConfig);
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
   * Writes the document start, global shell, optional contextual shell, and opening main element.
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
    writer.println("  <link rel=\"stylesheet\" href=\""
        + HtmlEscaper.escapeAttribute(resolvePath(AiraDefaults.STYLESHEET_PATH)) + "\">");
    for (String stylesheet : localStylesheets) {
      writer.println("  <link rel=\"stylesheet\" href=\"" + HtmlEscaper.escapeAttribute(resolvePath(stylesheet)) + "\">");
    }
    writer.println("</head>");
    writer.println("<body>");
    writer.println("<div class=\"aira-app\">");
    writer.println("  <a class=\"aira-skip-link\" href=\"#" + MAIN_CONTENT_ID + "\">Skip to main content</a>");
    writeGlobalHeader(writer);
    writeContextHeader(writer);
    writer.println("  <main class=\"" + HtmlEscaper.escapeAttribute(mainClass) + "\" id=\"" + MAIN_CONTENT_ID + "\">");
    if (pageHeading.isPresent() || pageIntro.isPresent()) {
      writer.println("    <div class=\"aira-container--wide\">");
      writer.println("      <div class=\"aira-page-header\">");
      writer.println("        <div>");
      pageHeading.ifPresent(heading ->
          writer.println("          <h1 class=\"aira-page-title\">" + HtmlEscaper.escapeText(heading) + "</h1>"));
      pageIntro.ifPresent(intro ->
          writer.println("          <p class=\"aira-page-intro\">" + HtmlEscaper.escapeText(intro) + "</p>"));
      writer.println("        </div>");
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

  private void writeGlobalHeader(PrintWriter writer) {
    writer.println("  <header class=\"aira-global-header\">");
    writeIdentity(writer);
    searchConfig.ifPresent(search -> writeSearch(writer, search));
    if (!globalActions.isEmpty()) {
      writer.println("    <nav class=\"aira-global-actions\" aria-label=\"Global actions\">");
      for (AiraActionItem action : globalActions) {
        writer.println("      <a class=\"aira-button aira-button--" + HtmlEscaper.escapeAttribute(action.variant())
            + "\" href=\"" + HtmlEscaper.escapeAttribute(resolvePath(action.href())) + "\">"
            + HtmlEscaper.escapeText(action.label()) + "</a>");
      }
      writer.println("    </nav>");
    }
    accountConfig.ifPresent(account -> writeAccount(writer, account));
    environmentConfig.ifPresent(environment -> writeEnvironment(writer, environment));
    writer.println("  </header>");
  }

  private void writeIdentity(PrintWriter writer) {
    writer.println("    <div class=\"aira-identity\">");
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
        () -> writer.println("        <span class=\"aira-logo-placeholder\" aria-label=\"AIRA\">AIRA</span>"));
    writer.println("        <span class=\"aira-app-name\">" + HtmlEscaper.escapeText(applicationName)
        + applicationSubtitle.map(subtitle -> "<span class=\"aira-app-subtitle\">"
        + HtmlEscaper.escapeText(subtitle) + "</span>").orElse("") + "</span>");
    if (!identityEnd.isEmpty()) {
      writer.println("      " + identityEnd);
    }
    writer.println("    </div>");
  }

  private void writeSearch(PrintWriter writer, AiraSearchConfig search) {
    writer.println("    <div class=\"aira-global-search\">");
    writer.println("      <form class=\"aira-search-form\" method=\"get\" action=\""
        + HtmlEscaper.escapeAttribute(resolvePath(search.action())) + "\">");
    writer.println("        <label class=\"aira-visually-hidden\" for=\"aira-global-search-input\">"
        + HtmlEscaper.escapeText(search.label()) + "</label>");
    writer.println("        <input class=\"aira-search-input\" id=\"aira-global-search-input\" type=\"search\" name=\""
        + HtmlEscaper.escapeAttribute(search.parameterName()) + "\" placeholder=\""
        + HtmlEscaper.escapeAttribute(search.placeholder()) + "\">");
    writer.println("        <button class=\"aira-button aira-button--secondary aira-button--small\" type=\"submit\">Search</button>");
    writer.println("      </form>");
    writer.println("    </div>");
  }

  private void writeAccount(PrintWriter writer, AiraAccountConfig account) {
    writer.println("    <div class=\"aira-account-area\">");
    account.labelValue().ifPresent(label ->
        writer.println("      <span class=\"aira-account-label\">" + HtmlEscaper.escapeText(label) + "</span>"));
    writer.println("      <a class=\"aira-button aira-button--ghost\" href=\""
        + HtmlEscaper.escapeAttribute(resolvePath(account.actionHref())) + "\">"
        + HtmlEscaper.escapeText(account.actionLabel()) + "</a>");
    writer.println("    </div>");
  }

  private void writeEnvironment(PrintWriter writer, AiraEnvironmentConfig environment) {
    String title = environment.descriptionValue()
        .map(description -> " title=\"" + HtmlEscaper.escapeAttribute(description) + "\"")
        .orElse("");
    writer.println("    <span class=\"aira-environment-badge\"" + title + ">"
        + HtmlEscaper.escapeText(environment.label()) + "</span>");
  }

  private void writeContextHeader(PrintWriter writer) {
    contextConfig.ifPresent(context -> {
      writer.println("  <section class=\"aira-context-header\" aria-label=\"Context\">");
      writer.println("    <div class=\"aira-context-inner\">");
      writer.println("      <p class=\"aira-context-title\">" + HtmlEscaper.escapeText(context.title()) + "</p>");
      if (!context.navigationItems().isEmpty()) {
        writer.println("      <nav class=\"aira-context-nav\" aria-label=\"Context navigation\">");
        for (AiraNavigationItem item : context.navigationItems()) {
          writer.print("        <a href=\"" + HtmlEscaper.escapeAttribute(resolvePath(item.href())) + "\"");
          if (item.active()) {
            writer.print(" aria-current=\"page\"");
          }
          writer.println(">" + HtmlEscaper.escapeText(item.label()) + "</a>");
        }
        writer.println("      </nav>");
      }
      writer.println("    </div>");
      writer.println("  </section>");
    });
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
    private String mainClass;
    private String contextPath = "";
    private String identityHref;
    private AiraLogo logo;
    private final List<String> localStylesheets = new ArrayList<>();
    private final List<AiraActionItem> globalActions = new ArrayList<>();
    private AiraSearchConfig searchConfig;
    private AiraAccountConfig accountConfig;
    private AiraEnvironmentConfig environmentConfig;
    private AiraContextConfig contextConfig;
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

    /** Sets the class attribute used on the main element. */
    public Builder mainClass(String mainClass) {
      this.mainClass = mainClass;
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

    /** Adds a local stylesheet to render after the shared AIRA stylesheet. */
    public Builder addLocalStylesheet(String stylesheetPath) {
      this.localStylesheets.add(Validation.requireText(stylesheetPath, "stylesheetPath"));
      return this;
    }

    /** Adds a global shell action link. */
    public Builder addGlobalAction(String label, String href, String variant) {
      this.globalActions.add(new AiraActionItem(label, href, variant));
      return this;
    }

    /** Sets optional global search configuration. */
    public Builder search(AiraSearchConfig searchConfig) {
      this.searchConfig = searchConfig;
      return this;
    }

    /** Sets optional account/sign-in configuration. */
    public Builder account(AiraAccountConfig accountConfig) {
      this.accountConfig = accountConfig;
      return this;
    }

    /** Sets optional non-production environment configuration. */
    public Builder environment(AiraEnvironmentConfig environmentConfig) {
      this.environmentConfig = environmentConfig;
      return this;
    }

    /** Sets optional contextual title and navigation. */
    public Builder context(AiraContextConfig contextConfig) {
      this.contextConfig = contextConfig;
      return this;
    }

    /** Adds a contextual navigation item, creating an unnamed context is intentionally not supported. */
    public Builder context(String title, List<AiraNavigationItem> navigationItems) {
      this.contextConfig = new AiraContextConfig(title, navigationItems);
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
