package org.immregistries.aira.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class AiraPageTest {

  @Test
  void sharedStylesheetAppearsBeforeLocalStylesheets() {
    String html = render(pageBuilder()
        .contextPath("/demo")
        .addLocalStylesheet("/css/application.css")
        .addLocalStylesheet("/css/extra.css")
        .build());

    assertTrue(html.indexOf("/demo/aira/css/aira.css") < html.indexOf("/demo/css/application.css"));
    assertTrue(html.indexOf("/demo/css/application.css") < html.indexOf("/demo/css/extra.css"));
  }

  @Test
  void emptyContextPathCreatesCorrectUrls() {
    String html = render(pageBuilder()
        .contextPath("")
        .addLocalStylesheet("/css/application.css")
        .addNavigationItem("Home", "/", true)
        .build());

    assertTrue(html.contains("href=\"/aira/css/aira.css\""));
    assertTrue(html.contains("href=\"/css/application.css\""));
    assertTrue(html.contains("href=\"/\" aria-current=\"page\""));
  }

  @Test
  void nonEmptyContextPathCreatesCorrectUrls() {
    String html = render(pageBuilder()
        .contextPath("/aira-web-demo")
        .addLocalStylesheet("/css/application.css")
        .addNavigationItem("Home", "/", true)
        .build());

    assertTrue(html.contains("href=\"/aira-web-demo/aira/css/aira.css\""));
    assertTrue(html.contains("href=\"/aira-web-demo/css/application.css\""));
    assertTrue(html.contains("href=\"/aira-web-demo/\" aria-current=\"page\""));
  }

  @Test
  void activeNavigationReceivesAriaCurrent() {
    String html = render(pageBuilder()
        .addNavigationItem("Home", "/", true)
        .addNavigationItem("Docs", "/docs", false)
        .build());

    assertTrue(html.contains("href=\"/\" aria-current=\"page\">Home</a>"));
    assertTrue(html.contains("href=\"/docs\">Docs</a>"));
  }

  @Test
  void navigationIsOmittedWhenNoItemsAreSupplied() {
    String html = render(pageBuilder().build());

    assertFalse(html.contains("<nav class=\"aira-primary-nav\""));
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
        .build());

    assertTrue(html.contains("Demo &lt;Tool&gt;"));
    assertTrue(html.contains("Heading &amp; More"));
    assertTrue(html.contains("Intro &quot;quoted&quot;"));
  }

  @Test
  void attributeValuesAreHtmlEscaped() {
    String html = render(pageBuilder()
        .addLocalStylesheet("/css/app\"bad.css")
        .addNavigationItem("Docs", "/docs?x=\"<", true)
        .logo(new AiraLogo("/images/logo\".svg", "AIRA \"Logo\""))
        .build());

    assertTrue(html.contains("href=\"/css/app&quot;bad.css\""));
    assertTrue(html.contains("href=\"/docs?x=&quot;&lt;\""));
    assertTrue(html.contains("src=\"/images/logo&quot;.svg\""));
    assertTrue(html.contains("alt=\"AIRA &quot;Logo&quot;\""));
  }

  @Test
  void blankOptionalValuesDoNotGenerateMalformedMarkup() {
    String html = render(pageBuilder()
        .applicationSubtitle(" ")
        .pageHeading(" ")
        .pageIntro(" ")
        .identityHref(" ")
        .build());

    assertFalse(html.contains("aira-app-subtitle"));
    assertFalse(html.contains("<h1 class=\"aira-page-title\">"));
    assertFalse(html.contains("<p class=\"aira-page-intro\">"));
    assertFalse(html.contains("aira-identity-link"));
  }

  @Test
  void requiredFieldsRejectInvalidValues() {
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().applicationName(" ").build());
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().applicationVersion(null).build());
    assertThrows(IllegalArgumentException.class, () -> pageBuilder().documentTitle("").build());
    assertThrows(IllegalArgumentException.class, () -> new AiraNavigationItem("", "/", false));
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

