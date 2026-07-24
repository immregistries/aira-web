package org.immregistries.aira.web.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import org.immregistries.aira.web.AiraAccountConfig;
import org.immregistries.aira.web.AiraDefaults;
import org.immregistries.aira.web.AiraContextConfig;
import org.immregistries.aira.web.AiraEnvironmentConfig;
import org.immregistries.aira.web.AiraLogo;
import org.immregistries.aira.web.AiraNavigationItem;
import org.immregistries.aira.web.AiraPage;
import org.immregistries.aira.web.AiraSearchConfig;

@WebServlet(name = "DemoServlet", urlPatterns = {"", "/management", "/components"})
public class DemoServlet extends HttpServlet {

  private static final String APPLICATION_NAME = "InteropHub";
  private static final String APPLICATION_VERSION = loadApplicationVersion();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    String path = request.getServletPath();
    if (path == null || path.isBlank() || "/".equals(path)) {
      renderPublicExample(request, response);
    } else if ("/management".equals(path)) {
      renderManagementExample(request, response);
    } else {
      renderComponentReference(request, response);
    }
  }

  private static AiraPage.Builder shell(HttpServletRequest request, String title, String active) {
    return AiraPage.builder()
        .applicationName(APPLICATION_NAME)
        .applicationSubtitle("AIRA Web visual review")
        .applicationVersion(APPLICATION_VERSION)
        .documentTitle(title + " - AIRA Web Demo")
        .contextPath(request.getContextPath())
        .identityHref("/")
        .logo(new AiraLogo(AiraDefaults.DEFAULT_LOGO_PATH, AiraDefaults.DEFAULT_LOGO_ALT_TEXT))
        .search(new AiraSearchConfig("/components", "q", "Search InteropHub", "Search topics or meetings"))
        .addGlobalAction("Explore", "/", "secondary")
        .account(new AiraAccountConfig("Nathan Bunker", "Account", "/signin"))
        .environment(new AiraEnvironmentConfig("Local", "Local development environment"))
        .context(new AiraContextConfig("Emerging Standards for Immunizations", List.of(
            new AiraNavigationItem("Home", "/", "home".equals(active)),
            new AiraNavigationItem("Topics", "/components", "topics".equals(active)),
            new AiraNavigationItem("Meetings", "/management", "meetings".equals(active)))));
  }

  private static void renderPublicExample(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AiraPage page = shell(request, "Public Engagement", "home")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-stack aira-stack--loose\">");
      out.println("      <header class=\"aira-page-header\">");
      out.println("        <div class=\"aira-reading\">");
      out.println("          <h1 class=\"aira-public-title\">Help shape immunization interoperability priorities</h1>");
      out.println("          <p class=\"aira-public-intro\">Review active topics, follow the work that matters to you, and join upcoming conversations with the community.</p>");
      out.println("        </div>");
      out.println("        <div class=\"aira-page-actions\"><a class=\"aira-button aira-button--primary\" href=\"#topics\">Choose topics</a><a class=\"aira-button aira-button--ghost\" href=\"#meetings\">View meetings</a></div>");
      out.println("      </header>");
      renderFeaturedCards(out);
      renderTopicChoices(out);
      renderMeetings(out);
      renderLabsAndEmptyState(out);
      out.println("      <div class=\"aira-sticky-actions aira-no-print\"><span class=\"aira-meta\">3 topics selected</span><button class=\"aira-button aira-button--primary\" type=\"button\">Save selections</button></div>");
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static void renderFeaturedCards(PrintWriter out) {
    out.println("      <section aria-labelledby=\"featured-title\" class=\"aira-stack\">");
    out.println("        <h2 class=\"aira-section-title\" id=\"featured-title\">Featured topics</h2>");
    out.println("        <div class=\"aira-card-grid\">");
    resourceCard(out, "FHIR bulk data", "Technical exchange", "Evaluating bulk export patterns for immunization reporting.", "High interest", "info");
    resourceCard(out, "Patient updates", "HL7 v2 and FHIR", "Coordinating event-based updates across systems.", "Following", "success");
    resourceCard(out, "Vocabulary collaboration", "Standards operations", "Maintaining shared vocabulary practices for implementers.", "Meeting active", "warning");
    out.println("        </div>");
    out.println("      </section>");
  }

  private static void resourceCard(PrintWriter out, String title, String type, String description, String badge, String variant) {
    out.println("          <article class=\"aira-card\">");
    out.println("            <div class=\"aira-card__body aira-stack aira-stack--compact\">");
    out.println("              <div class=\"aira-cluster aira-cluster--between\"><span class=\"aira-meta\">" + escapeHtml(type)
        + "</span><span class=\"aira-badge aira-badge--" + variant + "\">" + escapeHtml(badge) + "</span></div>");
    out.println("              <h3 class=\"aira-card__title\">" + escapeHtml(title) + "</h3>");
    out.println("              <p class=\"aira-card__description\">" + escapeHtml(description) + "</p>");
    out.println("            </div>");
    out.println("            <div class=\"aira-card__footer\"><a class=\"aira-button aira-button--link\" href=\"#topics\">Review topic</a></div>");
    out.println("          </article>");
  }

  private static void renderTopicChoices(PrintWriter out) {
    out.println("      <section class=\"aira-stack\" id=\"topics\" aria-labelledby=\"topics-title\">");
    out.println("        <div class=\"aira-cluster aira-cluster--between\"><h2 class=\"aira-section-title\" id=\"topics-title\">Topic choices</h2><div class=\"aira-chip-list\"><a class=\"aira-chip is-selected\" href=\"#topics\" aria-selected=\"true\">All</a><a class=\"aira-chip\" href=\"#topics\">My topics</a><a class=\"aira-chip\" href=\"#topics\">Meetings</a></div></div>");
    out.println("        <div class=\"aira-choice-list\">");
    choiceRow(out, true, "Patient matching signals", "Selected", "success", "Identity, deduplication, and confidence signals across systems.", "Stage: Gather · Neighborhood: Data Quality");
    choiceRow(out, false, "FHIR subscription events", "Followed", "info", "Patterns for subscribing to update events across immunization partners.", "Stage: Pilot · Neighborhood: FHIR");
    choiceRow(out, false, "Inventory reconciliation", "Registered", "warning", "Upcoming discussion about inventory movement and reconciliation.", "Stage: Monitor · Neighborhood: Inventory");
    choiceRow(out, false, "Legacy transport cleanup", "Disabled", "danger", "This topic is closed for the current review period.", "Stage: Parked · Neighborhood: Support");
    out.println("        </div>");
    out.println("      </section>");
  }

  private static void choiceRow(PrintWriter out, boolean checked, String title, String badge, String variant,
      String description, String meta) {
    out.println("          <article class=\"aira-choice-row" + (checked ? " is-selected" : "") + "\"" + (checked ? " aria-selected=\"true\"" : "") + ">");
    out.println("            <div class=\"aira-choice-row__control\"><input type=\"checkbox\"" + (checked ? " checked" : "") + " aria-label=\"Select " + escapeHtml(title) + "\"></div>");
    out.println("            <div class=\"aira-choice-row__content\">");
    out.println("              <div class=\"aira-cluster aira-cluster--between\"><h3 class=\"aira-choice-row__title\">" + escapeHtml(title)
        + "</h3><span class=\"aira-badge aira-badge--" + variant + "\">" + escapeHtml(badge) + "</span></div>");
    out.println("              <p class=\"aira-choice-row__description\">" + escapeHtml(description) + "</p>");
    out.println("              <p class=\"aira-choice-row__meta\">" + escapeHtml(meta) + "</p>");
    out.println("              <div class=\"aira-choice-row__actions aira-action-group\"><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">Details</button><button class=\"aira-button aira-button--small aira-button--ghost\" type=\"button\">Follow</button></div>");
    out.println("            </div>");
    out.println("          </article>");
  }

  private static void renderMeetings(PrintWriter out) {
    out.println("      <section class=\"aira-event-section\" id=\"meetings\" aria-labelledby=\"meetings-title\">");
    out.println("        <h2 class=\"aira-section-title\" id=\"meetings-title\">Upcoming meetings</h2>");
    out.println("        <div class=\"aira-event-list\">");
    eventRow(out, "Jul 23", "10:00 AM ET", "ImmDS + HALO", "Draft agenda · 6 topics", "Upcoming", "info", false);
    eventRow(out, "Aug 6", "2:00 PM ET", "Vocabulary Collaboration", "Quarterly topic review", "Registered", "success", false);
    eventRow(out, "Jun 18", "Past", "Consumer access roundtable", "Notes and recording available", "Complete", "neutral", true);
    out.println("        </div>");
    out.println("      </section>");
  }

  private static void eventRow(PrintWriter out, String date, String time, String title, String meta, String status,
      String variant, boolean past) {
    out.println("          <article class=\"aira-event-row" + (past ? " aira-event-row--past" : "") + "\">");
    out.println("            <div class=\"aira-event-date\"><span>" + escapeHtml(date) + "</span><span class=\"aira-event-time\">" + escapeHtml(time) + "</span></div>");
    out.println("            <div><h3 class=\"aira-event-title\">" + escapeHtml(title) + "</h3><p class=\"aira-event-meta\">" + escapeHtml(meta) + "</p></div>");
    out.println("            <div class=\"aira-action-group aira-action-group--end\"><span class=\"aira-badge"
        + ("neutral".equals(variant) ? "" : " aira-badge--" + variant) + "\">" + escapeHtml(status)
        + "</span><a class=\"aira-button aira-button--small aira-button--secondary\" href=\"#meetings\">Open</a></div>");
    out.println("          </article>");
  }

  private static void renderLabsAndEmptyState(PrintWriter out) {
    out.println("      <div class=\"aira-grid\">");
    out.println("        <section class=\"aira-panel\"><h2 class=\"aira-section-title\">Applications and Interoperability Labs</h2><p class=\"aira-muted\">Use shared app links to reach validation tools, sandboxes, and collaboration spaces.</p><div class=\"aira-tag-list\"><span class=\"aira-tag\">Validator</span><span class=\"aira-tag aira-tag--info\">Sandbox</span><span class=\"aira-tag aira-tag--success\">Available</span></div></section>");
    out.println("        <section class=\"aira-empty-state\"><h2 class=\"aira-empty-state__title\">No saved filters</h2><p>Create a filter from a topic list to see it here.</p><p><button class=\"aira-button aira-button--secondary\" type=\"button\">Create filter</button></p></section>");
    out.println("      </div>");
  }

  private static void renderManagementExample(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AiraPage page = shell(request, "Management", "meetings")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-sidebar-layout\">");
      renderManagementSidebar(out, request.getContextPath());
      out.println("      <div class=\"aira-management-shell\">");
      out.println("        <header class=\"aira-management-header\"><div><h1 class=\"aira-page-title\">Topic management</h1><p class=\"aira-page-intro\">Manage review settings, publication state, and meeting coordination.</p></div><div class=\"aira-action-group aira-action-group--end\"><button class=\"aira-button aira-button--secondary\" type=\"button\">Export</button><button class=\"aira-button aira-button--primary\" type=\"button\">New topic</button></div></header>");
      out.println("        <div class=\"aira-toolbar\"><div class=\"aira-cluster\"><span class=\"aira-badge aira-badge--success\">Published</span><span class=\"aira-badge aira-badge--warning\">3 drafts</span><span class=\"aira-badge aira-badge--danger\">1 blocked</span></div><div class=\"aira-action-group\"><button class=\"aira-button aira-button--small aira-button--ghost\" type=\"button\">Refresh</button></div></div>");
      renderManagementForm(out, request.getContextPath());
      renderManagementTable(out);
      out.println("      </div>");
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static void renderManagementSidebar(PrintWriter out, String contextPath) {
    out.println("      <aside class=\"aira-panel aira-sidebar aira-sidebar--sticky\" aria-label=\"Management navigation\">");
    out.println("        <div class=\"aira-sidebar-section\"><p class=\"aira-sidebar-title\">Content</p><nav class=\"aira-sidebar-nav\"><a class=\"aira-sidebar-link\" href=\"" + escapeHtml(contextPath) + "/management\" aria-current=\"page\">Topics <span class=\"aira-badge\">124</span></a><a class=\"aira-sidebar-link\" href=\"" + escapeHtml(contextPath) + "/management\">Campaigns</a><a class=\"aira-sidebar-link\" href=\"" + escapeHtml(contextPath) + "/management\">Surveys</a></nav></div>");
    out.println("        <div class=\"aira-sidebar-section\"><p class=\"aira-sidebar-title\">People</p><nav class=\"aira-sidebar-nav\"><a class=\"aira-sidebar-link\" href=\"" + escapeHtml(contextPath) + "/management\">Participants</a><a class=\"aira-sidebar-link\" href=\"" + escapeHtml(contextPath) + "/management\">Organizations</a></nav></div>");
    out.println("      </aside>");
  }

  private static void renderManagementForm(PrintWriter out, String contextPath) {
    out.println("        <section class=\"aira-panel\" aria-labelledby=\"edit-title\"><div class=\"aira-panel__header\"><h2 class=\"aira-panel__title\" id=\"edit-title\">Edit display settings</h2></div><div class=\"aira-panel__body\">");
    out.println("          <form class=\"aira-form\" method=\"get\" action=\"" + escapeHtml(contextPath) + "/management\">");
    out.println("            <div class=\"aira-field-row\"><div class=\"aira-field\"><label for=\"topic-name\">Topic name <span class=\"aira-required\">required</span></label><input class=\"aira-input\" id=\"topic-name\" name=\"topicName\" value=\"FHIR subscription events\" required><span class=\"aira-field-help\">Use the public display name.</span></div><div class=\"aira-field\"><label for=\"stage\">Stage</label><select class=\"aira-select\" id=\"stage\" name=\"stage\"><option>Pilot</option><option>Gather</option><option>Monitor</option></select></div></div>");
    out.println("            <fieldset class=\"aira-fieldset\"><legend class=\"aira-legend\">Visibility</legend><label class=\"aira-radio\"><input type=\"radio\" name=\"visibility\" checked> Public</label><label class=\"aira-radio\"><input type=\"radio\" name=\"visibility\"> Draft</label><label class=\"aira-check\"><input type=\"checkbox\" checked> Allow participants to follow this topic</label></fieldset>");
    out.println("            <label class=\"aira-switch\"><input type=\"checkbox\" checked> Include in meeting recommendations</label>");
    out.println("            <div class=\"aira-form-actions aira-form-actions--end\"><button class=\"aira-button aira-button--ghost\" type=\"button\">Cancel</button><button class=\"aira-button aira-button--primary\" type=\"submit\">Save settings</button></div>");
    out.println("          </form></div></section>");
  }

  private static void renderManagementTable(PrintWriter out) {
    out.println("        <section class=\"aira-panel aira-density-compact\" aria-labelledby=\"table-title\"><h2 class=\"aira-section-title\" id=\"table-title\">Recent topics</h2><div class=\"aira-table-wrap\"><table class=\"aira-table\"><thead><tr><th scope=\"col\">Topic</th><th scope=\"col\">Stage</th><th scope=\"col\">Status</th><th scope=\"col\">Actions</th></tr></thead><tbody>");
    tableRow(out, "FHIR subscription events", "Pilot", "Published", "success");
    tableRow(out, "Inventory reconciliation", "Monitor", "Draft", "warning");
    tableRow(out, "Legacy transport cleanup", "Parked", "Blocked", "danger");
    out.println("        </tbody></table></div></section>");
  }

  private static void tableRow(PrintWriter out, String topic, String stage, String status, String variant) {
    out.println("          <tr><td>" + escapeHtml(topic) + "</td><td>" + escapeHtml(stage)
        + "</td><td><span class=\"aira-status aira-status--" + variant + "\"><span class=\"aira-status-dot\"></span>"
        + escapeHtml(status) + "</span></td><td><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">Edit</button></td></tr>");
  }

  private static void renderComponentReference(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AiraPage page = shell(request, "Component Reference", "topics")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-stack aira-stack--loose\">");
      out.println("      <header><h1 class=\"aira-page-title\">Component reference</h1><p class=\"aira-page-intro\">First-release AIRA Web component variants for servlet-generated pages.</p></header>");
      out.println("      <section class=\"aira-panel\"><h2 class=\"aira-section-title\">Buttons</h2><div class=\"aira-action-group\"><button class=\"aira-button aira-button--primary\">Primary</button><button class=\"aira-button aira-button--secondary\">Secondary</button><button class=\"aira-button aira-button--tertiary\">Tertiary</button><button class=\"aira-button aira-button--success\">Success</button><button class=\"aira-button aira-button--danger\">Danger</button><button class=\"aira-button aira-button--ghost\">Ghost</button><a class=\"aira-button aira-button--tertiary\" href=\"#topics\">Tertiary link</a><button class=\"aira-button aira-button--small\">Small</button><button class=\"aira-button aira-button--icon\" aria-label=\"Add\">+</button></div></section>");
      out.println("      <section class=\"aira-panel\"><h2 class=\"aira-section-title\">Badges and tags</h2><div class=\"aira-tag-list\"><span class=\"aira-badge\">Neutral</span><span class=\"aira-badge aira-badge--subtle\">Subtle</span><span class=\"aira-badge aira-badge--info\">Info</span><span class=\"aira-badge aira-badge--success\">Success</span><span class=\"aira-badge aira-badge--warning\">Warning</span><span class=\"aira-badge aira-badge--danger\">Danger</span><span class=\"aira-tag aira-tag--outline\">Outline tag</span></div></section>");
      renderManagementForm(out, request.getContextPath());
      out.println("      <section class=\"aira-alert aira-alert--info\"><p class=\"aira-alert__title\">Information alert</p><p>Alerts use pale semantic surfaces and a strong left border.</p></section>");
      renderFeaturedCards(out);
      renderTopicChoices(out);
      renderMeetings(out);
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static String loadApplicationVersion() {
    Properties properties = new Properties();
    try (InputStream inputStream = DemoServlet.class.getResourceAsStream("/aira-web-demo.properties")) {
      if (inputStream != null) {
        properties.load(inputStream);
        String version = properties.getProperty("application.version");
        if (version != null && !version.isBlank() && !version.startsWith("${")) {
          return version.trim();
        }
      }
    } catch (IOException ignored) {
      // Fall through to a readable local-development fallback.
    }
    return "development";
  }

  private static String escapeHtml(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder escaped = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char character = value.charAt(i);
      switch (character) {
        case '&' -> escaped.append("&amp;");
        case '<' -> escaped.append("&lt;");
        case '>' -> escaped.append("&gt;");
        case '"' -> escaped.append("&quot;");
        case '\'' -> escaped.append("&#39;");
        default -> escaped.append(character);
      }
    }
    return escaped.toString();
  }
}
