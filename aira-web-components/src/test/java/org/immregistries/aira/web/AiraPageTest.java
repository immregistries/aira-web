package org.immregistries.aira.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiraPageTest {

  @Test
  void sharedStylesheetAppearsBeforeLocalStylesheets() {
    String html = render(pageBuilder()
        .contextPath("/demo")
        .addLocalStylesheet("/css/application.css")
        .addLocalStylesheet("/css/extra.css")
        .build());

    assertTrue(html.indexOf("/demo" + AiraDefaults.STYLESHEET_PATH)
        < html.indexOf("/demo/css/application.css"));
    assertTrue(html.indexOf("/demo/css/application.css") < html.indexOf("/demo/css/extra.css"));
  }

  @Test
  void emptyContextPathCreatesCorrectUrls() {
    String html = render(pageBuilder()
        .contextPath("")
        .addLocalStylesheet("/css/application.css")
        .context("Workspace", List.of(new AiraNavigationItem("Home", "/", true)))
        .build());

    assertTrue(html.contains("href=\"" + AiraDefaults.STYLESHEET_PATH + "\""));
    assertTrue(html.contains("href=\"/css/application.css\""));
    assertTrue(html.contains("href=\"/\" aria-current=\"page\""));
  }

  @Test
  void nonEmptyContextPathCreatesCorrectUrls() {
    String html = render(pageBuilder()
        .contextPath("/aira-web-demo")
        .addLocalStylesheet("/css/application.css")
        .context("Workspace", List.of(new AiraNavigationItem("Home", "/", true)))
        .build());

    assertTrue(html.contains("href=\"/aira-web-demo" + AiraDefaults.STYLESHEET_PATH + "\""));
    assertTrue(html.contains("href=\"/aira-web-demo/css/application.css\""));
    assertTrue(html.contains("href=\"/aira-web-demo/\" aria-current=\"page\""));
  }

  @Test
  void contextNavigationReceivesAriaCurrent() {
    String html = render(pageBuilder()
        .context("Emerging Standards", List.of(
            new AiraNavigationItem("Home", "/", true),
            new AiraNavigationItem("Topics", "/topics", false)))
        .build());

    assertTrue(html.contains("<section class=\"aira-context-header\""));
    assertTrue(html.contains("Emerging Standards"));
    assertTrue(html.contains("href=\"/\" aria-current=\"page\">Home</a>"));
    assertTrue(html.contains("href=\"/topics\">Topics</a>"));
  }

  @Test
  void contextIsOmittedWhenNoContextIsSupplied() {
    String html = render(pageBuilder().build());

    assertFalse(html.contains("aira-context-header"));
    assertFalse(html.contains("aira-context-nav"));
  }

  @Test
  void optionalHeaderControlsRenderWhenConfigured() {
    String html = render(pageBuilder()
        .addGlobalAction("Explore", "/explore", "secondary")
        .search(new AiraSearchConfig("/search", "q", "Search InteropHub", "Search topics"))
        .account(new AiraAccountConfig("Nathan", "Sign out", "/logout"))
        .environment(new AiraEnvironmentConfig("Local", "Local development environment"))
        .build());

    assertTrue(html.contains("class=\"aira-global-search\""));
    assertTrue(html.contains("name=\"q\""));
    assertTrue(html.contains("placeholder=\"Search topics\""));
    assertTrue(html.contains("class=\"aira-button aira-button--secondary\" href=\"/explore\""));
    assertTrue(html.contains("class=\"aira-account-label\">Nathan</span>"));
    assertTrue(html.contains("href=\"/logout\">Sign out</a>"));
    assertTrue(html.contains("class=\"aira-environment-badge\" title=\"Local development environment\">Local</span>"));
  }

  @Test
  void tertiaryGlobalActionVariantIsAllowed() {
    String html = render(pageBuilder()
        .addGlobalAction("Details", "/details", "tertiary")
        .build());

    assertTrue(html.contains("class=\"aira-button aira-button--tertiary\" href=\"/details\""));
  }

  @Test
  void optionalHeaderControlsAreOmittedByDefault() {
    String html = render(pageBuilder().build());

    assertFalse(html.contains("aira-global-search"));
    assertFalse(html.contains("aira-global-actions"));
    assertFalse(html.contains("aira-account-area"));
    assertFalse(html.contains("aira-environment-badge"));
  }

  @Test
  void requiredFooterTextIsPresent() {
    String html = render(pageBuilder().build());

    assertTrue(html.contains("American Immunization Registry Association (AIRA)</a> supports collaboration"));
    assertTrue(html.contains(AiraDefaults.INSTITUTIONAL_DESCRIPTION));
  }

  @Test
  void applicationNameAndVersionAppearInFooter() {
    String html = render(pageBuilder()
        .applicationName("Registry Tool")
        .applicationVersion("2.3.4")
        .build());

    assertTrue(html.contains("Registry Tool version 2.3.4"));
  }

  @Test
  void defaultLegalUrlsArePresent() {
    String html = render(pageBuilder().build());

    assertTrue(html.contains(AiraDefaults.AIRA_WEBSITE_URL));
    assertTrue(html.contains(AiraDefaults.PRIVACY_POLICY_URL));
    assertTrue(html.contains(AiraDefaults.TERMS_OF_USE_URL));
  }

  @Test
  void copyrightYearOverrideWorks() {
    String html = render(pageBuilder()
        .footerConfig(AiraFooterConfig.builder().copyrightYear(2031).build())
        .build());

    assertTrue(html.contains("Copyright 2031"));
  }

  @Test
  void textValuesAreHtmlEscaped() {
    String html = render(pageBuilder()
        .applicationName("Demo <Tool>")
        .pageHeading("Heading & More")
        .pageIntro("Intro \"quoted\"")
        .context("Context <One>", List.of(new AiraNavigationItem("A&B", "/a", true)))
        .build());

    assertTrue(html.contains("Demo &lt;Tool&gt;"));
    assertTrue(html.contains("Heading &amp; More"));
    assertTrue(html.contains("Intro &quot;quoted&quot;"));
    assertTrue(html.contains("Context &lt;One&gt;"));
    assertTrue(html.contains(">A&amp;B</a>"));
  }

  @Test
  void attributeValuesAreHtmlEscaped() {
    String html = render(pageBuilder()
        .addLocalStylesheet("/css/app\"bad.css")
        .context("Docs", List.of(new AiraNavigationItem("Docs", "/docs?x=\"<", true)))
        .logo(new AiraLogo("/images/logo\".svg", "AIRA \"Logo\""))
        .search(new AiraSearchConfig("/search?x=\"", "q\"bad", "Search", "quoted \" text"))
        .build());

    assertTrue(html.contains("href=\"/css/app&quot;bad.css\""));
    assertTrue(html.contains("href=\"/docs?x=&quot;&lt;\""));
    assertTrue(html.contains("src=\"/images/logo&quot;.svg\""));
    assertTrue(html.contains("alt=\"AIRA &quot;Logo&quot;\""));
    assertTrue(html.contains("action=\"/search?x=&quot;\""));
    assertTrue(html.contains("name=\"q&quot;bad\""));
    assertTrue(html.contains("placeholder=\"quoted &quot; text\""));
  }

  @Test
  void blankOptionalValuesDoNotGenerateMalformedMarkup() {
    String html = render(pageBuilder()
        .applicationSubtitle(" ")
        .pageHeading(" ")
        .pageIntro(" ")
        .identityHref(" ")
        .account(new AiraAccountConfig(" ", "Sign in", "/login"))
        .build());

    assertFalse(html.contains("aira-app-subtitle"));
    assertFalse(html.contains("<h1 class=\"aira-page-title\">"));
    assertFalse(html.contains("<p class=\"aira-page-intro\">"));
    assertFalse(html.contains("aira-identity-link"));
    assertFalse(html.contains("aira-account-label"));
  }

  @Test
  void requiredFieldsRejectInvalidValues() {
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().applicationName(" ").build());
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().applicationVersion(null).build());
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().documentTitle("").build());
    assertThrows(IllegalArgumentException.class, () -> new AiraNavigationItem("", "/", false));
    assertThrows(IllegalArgumentException.class, () -> new AiraSearchConfig("", "q", "Search", ""));
    assertThrows(IllegalArgumentException.class, () -> new AiraActionItem("Bad", "/", "bad class"));
  }

  @Test
  void generatedPageContainsMatchingMajorOpeningAndClosingElements() {
    String html = render(pageBuilder().build());

    assertEquals(count(html, "<html"), count(html, "</html>"));
    assertEquals(count(html, "<head>"), count(html, "</head>"));
    assertEquals(count(html, "<body>"), count(html, "</body>"));
    assertEquals(count(html, "<main "), count(html, "</main>"));
    assertEquals(count(html, "<footer "), count(html, "</footer>"));
  }

  private static AiraPage.Builder pageBuilder() {
    return AiraPage.builder()
        .applicationName("AIRA Web Demo")
        .applicationVersion("test")
        .documentTitle("AIRA Component Test");
  }

  private static String render(AiraPage page) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    page.writeStart(writer);
    writer.println("<section>Application content</section>");
    page.writeEnd(writer);
    writer.flush();
    return stringWriter.toString();
  }

  private static int count(String value, String token) {
    int count = 0;
    int index = value.indexOf(token);
    while (index >= 0) {
      count++;
      index = value.indexOf(token, index + token.length());
    }
    return count;
  }
}
