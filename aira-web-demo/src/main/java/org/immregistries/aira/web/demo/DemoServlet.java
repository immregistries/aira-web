package org.immregistries.aira.web.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
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

@WebServlet(name = "DemoServlet", urlPatterns = {"", "/management", "/components", "/topic", "/tables"})
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
    } else if ("/topic".equals(path)) {
      renderTopicPageExample(request, response);
    } else if ("/tables".equals(path)) {
      renderTablesExample(request, response);
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
        .search(new AiraSearchConfig("/topic", "q", "Search InteropHub", "Search topics or meetings"))
        .addGlobalAction("PHACT topic", "/topic", "tertiary")
        .account(new AiraAccountConfig("Nathan Bunker", "Account", "/signin"))
        .environment(new AiraEnvironmentConfig("Local", "Local development environment"))
        .context(new AiraContextConfig("Emerging Standards for Immunizations", List.of(
            new AiraNavigationItem("Workspace", "/", "home".equals(active)),
            new AiraNavigationItem("Topics", "/topic", "topics".equals(active)),
            new AiraNavigationItem("Meetings", "/management", "meetings".equals(active)),
            new AiraNavigationItem("Tables", "/tables", "tables".equals(active)),
            new AiraNavigationItem("Components", "/components", "components".equals(active)))));
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

  private static void renderTopicPageExample(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AiraPage page = shell(request, "PHACT Topic", "topics")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-stack\">");
      out.println("      <div class=\"aira-right-rail-layout\">");
      out.println("        <div class=\"aira-stack\">");
      renderTopicHeader(out);
      renderTopicStatus(out);
      renderTopicOverview(out);
      renderRecentOutcomes(out);
      renderTopicMeetings(out);
      renderRelatedTopics(out);
      renderIncludedIn(out);
      renderTopicResources(out);
      renderTopicDetailsStrip(out);
      out.println("        </div>");
      renderTopicRail(out);
      out.println("      </div>");
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static void renderTopicHeader(PrintWriter out) {
    out.println("          <header class=\"aira-topic-header\">");
    out.println("            <div class=\"aira-topic-header__top\">");
    out.println("              <div><a class=\"aira-inline-link\" href=\"#all-topics\">All Topics</a><h1 class=\"aira-topic-title\">PHACT</h1><p class=\"aira-topic-summary\">A capability-focused topic for connecting public health authority content, governance, authoring, publication, and standards implementation workflows.</p></div>");
    out.println("              <div class=\"aira-topic-actions\"><button class=\"aira-button aira-button--primary\" type=\"button\">Follow</button><a class=\"aira-button aira-button--tertiary\" href=\"#private-intake\">Send info / question</a></div>");
    out.println("            </div>");
    out.println("            <div class=\"aira-topic-meta\" aria-label=\"Topic metadata\">");
    metaChip(out, "Stage", "Growing Idea");
    metaChip(out, "Path", "Leadership Review");
    metaChip(out, "Topic type", "Capability");
    metaChip(out, "Visibility", "Private");
    metaChip(out, "Champion", "Nathan Bunker");
    metaChip(out, "Last updated", "Jul 2026");
    out.println("            </div>");
    out.println("          </header>");
  }

  private static void metaChip(PrintWriter out, String label, String value) {
    out.println("              <span class=\"aira-meta-chip\"><span class=\"aira-meta-chip__label\">"
        + escapeHtml(label) + "</span><span class=\"aira-meta-chip__value\">" + escapeHtml(value)
        + "</span></span>");
  }

  private static void renderTopicStatus(PrintWriter out) {
    out.println("          <section aria-labelledby=\"stage-path-title\" class=\"aira-stack\">");
    out.println("            <h2 class=\"aira-section-title\" id=\"stage-path-title\">Stage and Path</h2>");
    out.println("            <div class=\"aira-topic-status-grid\">");
    statusCard(out, "stage", "S", "Stage", "Growing Idea", "Active concept development and refinement.");
    statusCard(out, "path", "P", "Path", "Leadership Review", "Preparing for review, direction, and possible sponsorship.");
    out.println("            </div>");
    out.println("          </section>");
  }

  private static void statusCard(PrintWriter out, String variant, String icon, String label, String value,
      String description) {
    out.println("              <article class=\"aira-topic-status-card aira-topic-status-card--" + escapeHtml(variant)
        + "\"><span class=\"aira-topic-status-card__icon\" aria-hidden=\"true\">" + escapeHtml(icon)
        + "</span><div><p class=\"aira-topic-status-card__label\">" + escapeHtml(label)
        + "</p><p class=\"aira-topic-status-card__value\">" + escapeHtml(value)
        + "</p><p class=\"aira-topic-status-card__description\">" + escapeHtml(description)
        + "</p></div></article>");
  }

  private static void renderTopicOverview(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"overview-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"overview-title\">Overview</h2><a class=\"aira-section-card__action\" href=\"#background\">Open full background</a></div>");
    out.println("            <div class=\"aira-section-card__body aira-topic-overview\">");
    out.println("              <div class=\"aira-topic-overview__text aira-prose\"><p>PHACT organizes early capability work into a practical path from source material to standards-aware implementation support. The topic is private while the concept is refined with leadership and meeting participants.</p></div>");
    out.println("              <figure class=\"aira-figure\"><div class=\"aira-figure__frame\"><img class=\"aira-figure__image\" src=\"images/phact-concept.svg\" alt=\"Diagram explaining the PHACT concept\"></div><figcaption class=\"aira-figure__caption\">PHACT concept overview</figcaption></figure>");
    out.println("            </div>");
    out.println("          </section>");
  }

  private static void renderRecentOutcomes(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"outcomes-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"outcomes-title\">Recent Outcomes</h2></div>");
    out.println("            <div class=\"aira-section-card__body\"><div class=\"aira-outcome-list\">");
    outcomeRow(out, "Decision", "info", "Use leadership review as the next formal path checkpoint.", "Leadership Review", "Jul 2026");
    outcomeRow(out, "Next Action", "success", "Prepare a one-page summary for sponsor review.", "Building Bridges", "Jul 2026");
    outcomeRow(out, "Key Point", "subtle", "Authoring needs examples that distinguish content source from implementation use.", "Immunization Focus Group", "Jun 2026");
    outcomeRow(out, "Direction", "warning", "Keep external terminology guidance visible but separate from final recommendations.", "Leadership Review", "Jun 2026");
    out.println("            </div></div>");
    out.println("          </section>");
  }

  private static void outcomeRow(PrintWriter out, String type, String variant, String summary, String source,
      String date) {
    out.println("              <article class=\"aira-outcome-row\"><span class=\"aira-outcome-row__type aira-badge aira-badge--"
        + escapeHtml(variant) + "\">" + escapeHtml(type) + "</span><span class=\"aira-outcome-row__summary\">"
        + escapeHtml(summary) + "</span><span class=\"aira-outcome-row__source\">" + escapeHtml(source)
        + "</span><span class=\"aira-outcome-row__date\">" + escapeHtml(date)
        + "</span><a class=\"aira-outcome-row__action\" href=\"#meeting-summary\">View meeting summary</a></article>");
  }

  private static void renderTopicMeetings(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"meetings-summary-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"meetings-summary-title\">Meetings</h2></div>");
    out.println("            <div class=\"aira-section-card__body aira-stack\">");
    out.println("              <section><h3 class=\"aira-subsection-title\">Upcoming</h3><div class=\"aira-meeting-list\">");
    meetingRow(out, "Immunization Focus Group", "Aug 2026", "Open meeting");
    meetingRow(out, "Leadership Review", "Sep 2026", "Open meeting");
    out.println("              </div></section>");
    out.println("              <section><h3 class=\"aira-subsection-title\">Recent</h3><div class=\"aira-meeting-list\">");
    meetingRow(out, "Building Bridges", "Jul 2026", "View summary");
    meetingRow(out, "Leadership Review", "Jun 2026", "View summary");
    out.println("              </div></section>");
    out.println("            </div>");
    out.println("          </section>");
  }

  private static void meetingRow(PrintWriter out, String title, String date, String action) {
    out.println("                <article class=\"aira-meeting-row\"><span class=\"aira-meeting-row__title\">"
        + escapeHtml(title) + "</span><span class=\"aira-meeting-row__meta\">" + escapeHtml(date)
        + "</span><a class=\"aira-meeting-row__action\" href=\"#meeting\">" + escapeHtml(action)
        + "</a></article>");
  }

  private static void renderRelatedTopics(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"related-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"related-title\">Related Topics</h2></div>");
    out.println("            <div class=\"aira-section-card__body\"><div class=\"aira-relationship-list\">");
    relationshipRow(out, "IVC", "Informs capability framing");
    relationshipRow(out, "Vocabulary governance", "Shares governance concerns");
    relationshipRow(out, "InteropHub", "Provides collaboration workspace");
    relationshipRow(out, "Contextual Conditions", "References implementation context");
    relationshipRow(out, "HL7 external terminology guidance", "Supports external terminology alignment");
    out.println("            </div></div>");
    out.println("          </section>");
  }

  private static void relationshipRow(PrintWriter out, String title, String verb) {
    out.println("              <article class=\"aira-relationship-row\"><span class=\"aira-relationship-row__title\">"
        + escapeHtml(title) + "</span><span class=\"aira-relationship-row__verb\">" + escapeHtml(verb)
        + "</span><a class=\"aira-relationship-row__action\" href=\"#related-topic\">Open</a></article>");
  }

  private static void renderIncludedIn(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"included-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"included-title\">Included In</h2></div>");
    out.println("            <div class=\"aira-section-card__body\"><div class=\"aira-tag-list\"><span class=\"aira-tag aira-tag--info\">AIRA Strategic</span><span class=\"aira-tag aira-tag--success\">Emerging Standards</span><span class=\"aira-tag aira-tag--outline\">Building Bridges</span></div></div>");
    out.println("          </section>");
  }

  private static void renderTopicResources(PrintWriter out) {
    out.println("          <section class=\"aira-section-card\" aria-labelledby=\"resources-title\">");
    out.println("            <div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\" id=\"resources-title\">Resources</h2></div>");
    out.println("            <div class=\"aira-section-card__body\"><div class=\"aira-resource-grid\">");
    resourceLink(out, "1", "One-page summary", "Current leadership review brief");
    resourceLink(out, "D", "Introduction deck", "Meeting presentation");
    resourceLink(out, "R", "Recorded presentation", "Review session recording");
    resourceLink(out, "S", "Supporting document", "Background and scope notes");
    resourceLink(out, "N", "Research notes", "Open questions and source findings");
    resourceLink(out, "G", "GitHub repository", "Technical reference workspace");
    out.println("            </div></div>");
    out.println("          </section>");
  }

  private static void resourceLink(PrintWriter out, String icon, String title, String description) {
    out.println("              <a class=\"aira-resource-link\" href=\"#resource\"><span class=\"aira-resource-link__icon\" aria-hidden=\"true\">"
        + escapeHtml(icon) + "</span><span><span class=\"aira-resource-link__title\">" + escapeHtml(title)
        + "</span><span class=\"aira-resource-link__description\">" + escapeHtml(description)
        + "</span></span></a>");
  }

  private static void renderTopicRail(PrintWriter out) {
    out.println("        <aside class=\"aira-right-rail\" aria-label=\"Topic activity\">");
    out.println("          <section class=\"aira-section-card\"><div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\">Recently viewed</h2></div><div class=\"aira-section-card__body aira-stack aira-stack--compact\">");
    recentTopic(out, "S", "School Data Exchange", "Updated Jul 2026", false);
    recentTopic(out, "P", "Provider Enrollment Integration", "Updated Jul 2026", false);
    recentTopic(out, "C", "Certificate Renewal", "Updated Jun 2026", false);
    recentTopic(out, "C", "Contextual Conditions", "Updated Jun 2026", false);
    recentTopic(out, "V", "VIRS", "Updated May 2026", false);
    recentTopic(out, "P", "PHACT", "Current topic", true);
    out.println("          </div></section>");
    out.println("          <section class=\"aira-section-card\" id=\"private-intake\"><div class=\"aira-section-card__header\"><h2 class=\"aira-section-card__title\">Next discussion</h2></div><div class=\"aira-section-card__body aira-stack aira-stack--compact\"><p><strong>Leadership Review</strong></p><p class=\"aira-meta\">Sep 2026 - private intake routed to topic champions</p><a class=\"aira-button aira-button--small aira-button--secondary\" href=\"#meeting\">Open meeting</a></div></section>");
    out.println("        </aside>");
  }

  private static void recentTopic(PrintWriter out, String icon, String title, String meta, boolean current) {
    out.println("            <a class=\"aira-recent-topic\" href=\"#recent\"" + (current ? " aria-current=\"page\"" : "")
        + "><span class=\"aira-recent-topic__icon\" aria-hidden=\"true\">" + escapeHtml(icon)
        + "</span><span><span class=\"aira-recent-topic__title\">" + escapeHtml(title)
        + "</span><span class=\"aira-recent-topic__meta\">" + escapeHtml(meta) + "</span></span></a>");
  }

  private static void renderTopicDetailsStrip(PrintWriter out) {
    out.println("          <div class=\"aira-topic-details-strip\" aria-label=\"Topic details\"><span><strong>Topic ID:</strong> PHACT-2026</span><div class=\"aira-topic-details-strip__end\"><span class=\"aira-tag aira-tag--outline\">capability</span><span class=\"aira-tag aira-tag--outline\">governance</span><span class=\"aira-badge aira-badge--subtle\">Private</span><span class=\"aira-badge aira-badge--warning\">Internal use</span></div></div>");
  }

  private static void renderTablesExample(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String sort = allowedSort(request.getParameter("sort"));
    String direction = allowedDirection(request.getParameter("direction"));
    AiraPage page = shell(request, "Tables", "tables")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-stack\">");
      out.println("      <header><h1 class=\"aira-page-title\">Tables</h1><p class=\"aira-page-intro\">Standard table patterns for dense AIRA technical applications. Tables remain semantic, preserve columns, and use horizontal scrolling when content needs more width.</p></header>");
      renderTopicsUnderReviewTable(out);
      renderTopicMatrixTable(out);
      renderMessageProcessingTable(out);
      renderSemanticTablePanels(out);
      renderBrandHeaderTable(out);
      renderSortableTable(out, sort, direction);
      renderStickyHeaderTable(out);
      renderEmptyTable(out);
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static void renderTopicsUnderReviewTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"topics-review-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"topics-review-title\">Topics under review</h2><p class=\"aira-table-panel__description\">Selected row includes a checkbox and status text so selection is not conveyed by color alone.</p></div><div class=\"aira-table-panel__actions\"><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">Export</button><button class=\"aira-button aira-button--small aira-button--tertiary\" type=\"button\">Filter</button></div></div>");
    out.println("        <div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">Topics under review</caption><thead><tr><th scope=\"col\" class=\"aira-table__cell--primary\">Topic</th><th scope=\"col\">Stage</th><th scope=\"col\">Path</th><th scope=\"col\">Champion</th><th scope=\"col\" class=\"aira-table__cell--date\">Last Updated</th><th scope=\"col\" class=\"aira-table__cell--actions\"><span class=\"aira-visually-hidden\">Actions</span></th></tr></thead><tbody>");
    topicReviewRow(out, "PHACT", "Growing Idea", "Leadership Review", "Nathan Bunker", "Jul 2026", true);
    topicReviewRow(out, "Provider Enrollment Integration", "Monitor", "Building Bridges", "AIRA staff", "Jul 2026", false);
    topicReviewRow(out, "Certificate Renewal", "Draft", "Emerging Standards", "AIRA technical team", "Jun 2026", false);
    topicReviewRow(out, "School Data Exchange", "Proposed", "Focus Group", "Data exchange workgroup", "Jun 2026", false);
    topicReviewRow(out, "Contextual Conditions", "Revision", "Leadership Review", "Vocabulary team", "May 2026", false);
    out.println("        </tbody></table></div>");
    out.println("      </section>");
  }

  private static void topicReviewRow(PrintWriter out, String topic, String stage, String path, String champion,
      String updated, boolean selected) {
    String selectedAttrs = selected ? " class=\"is-selected\" aria-selected=\"true\"" : "";
    out.println("          <tr" + selectedAttrs + "><th scope=\"row\" class=\"aira-table__cell--primary\"><label class=\"aira-check\"><input type=\"checkbox\""
        + (selected ? " checked" : "") + "> " + escapeHtml(topic) + "</label></th><td><span class=\"aira-badge "
        + badgeClassForStage(stage) + "\">" + escapeHtml(stage) + "</span>"
        + (selected ? " <span class=\"aira-badge aira-badge--subtle\">Selected</span>" : "")
        + "</td><td><span class=\"aira-badge aira-badge--info\">" + escapeHtml(path)
        + "</span></td><td class=\"aira-table__cell--secondary\">" + escapeHtml(champion)
        + "</td><td class=\"aira-table__cell--date\">" + escapeHtml(updated)
        + "</td><td class=\"aira-table__cell--actions\"><a class=\"aira-button aira-button--small aira-button--secondary\" href=\"#topic\" aria-label=\"Open "
        + escapeHtml(topic) + "\">Open</a></td></tr>");
  }

  private static String badgeClassForStage(String stage) {
    return switch (stage) {
      case "Growing Idea", "Proposed" -> "aira-badge--info";
      case "Monitor" -> "aira-badge--success";
      case "Revision", "Draft", "Needs revision", "Postponed", "Due soon" -> "aira-badge--warning";
      case "Overdue" -> "aira-badge--danger";
      default -> "aira-badge--subtle";
    };
  }

  private static void renderTopicMatrixTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"topic-matrix-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"topic-matrix-title\">Topic board matrix</h2><p class=\"aira-table-panel__description\">Two-axis board table with sticky stage headers, sticky path headers, stacked entity cards, and disabled intersections.</p></div><div class=\"aira-table-panel__actions\"><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">Add stage</button></div></div>");
    out.println("        <div class=\"aira-matrix-table-wrap\"><table class=\"aira-matrix-table\"><caption class=\"aira-visually-hidden\">Topic board matrix by advancement path and stage</caption><thead><tr>");
    out.println("          <th scope=\"col\" class=\"aira-matrix-table__corner\"><div class=\"aira-matrix-table__header-inner\"><span class=\"aira-matrix-table__label\">Path / Stage</span></div></th>");
    matrixColumnHeader(out, "Growing Idea", true);
    matrixColumnHeader(out, "Pilot", true);
    matrixColumnHeader(out, "Monitor", false);
    out.println("        </tr></thead><tbody>");
    out.println("          <tr>");
    matrixRowHeader(out, "Leadership Review");
    matrixCellStart(out, false);
    entityCard(out, "PHACT", true, "Open");
    entityCard(out, "Contextual Conditions", false, "Open");
    matrixCellEnd(out);
    matrixCellStart(out, false);
    entityCard(out, "Certificate Renewal", true, "Open");
    matrixCellEnd(out);
    matrixCellStart(out, true);
    out.println("              <span class=\"aira-muted\">Not used for this path</span>");
    matrixCellEnd(out);
    out.println("          </tr>");
    out.println("          <tr>");
    matrixRowHeader(out, "Implementation Workgroup");
    matrixCellStart(out, false);
    entityCard(out, "FHIR Subscription Events", false, "Open");
    matrixCellEnd(out);
    matrixCellStart(out, false);
    entityCard(out, "Inventory Reconciliation", true, "Open");
    matrixCellEnd(out);
    matrixCellStart(out, false);
    entityCard(out, "Provider Enrollment Integration", false, "Open");
    matrixCellEnd(out);
    out.println("          </tr>");
    out.println("        </tbody></table></div>");
    out.println("      </section>");
  }

  private static void matrixColumnHeader(PrintWriter out, String label, boolean includeAction) {
    out.println("          <th scope=\"col\" class=\"aira-matrix-table__col-header\"><div class=\"aira-matrix-table__header-inner\"><span class=\"aira-matrix-table__label\">"
        + escapeHtml(label) + "</span>"
        + (includeAction ? "<button class=\"aira-button aira-button--small aira-button--ghost\" type=\"button\">Add</button>" : "")
        + "</div></th>");
  }

  private static void matrixRowHeader(PrintWriter out, String label) {
    out.println("            <th scope=\"row\" class=\"aira-matrix-table__row-header\"><div class=\"aira-matrix-table__header-inner\"><span class=\"aira-matrix-table__label\">"
        + escapeHtml(label) + "</span></div></th>");
  }

  private static void matrixCellStart(PrintWriter out, boolean disabled) {
    out.println("            <td class=\"aira-matrix-table__cell"
        + (disabled ? " aira-matrix-table__cell--disabled" : "")
        + "\"><div class=\"aira-stack aira-stack--compact\">");
  }

  private static void matrixCellEnd(PrintWriter out) {
    out.println("            </div></td>");
  }

  private static void entityCard(PrintWriter out, String title, boolean includeHandle, String action) {
    out.println("              <article class=\"aira-entity-card\">"
        + (includeHandle ? "<span class=\"aira-entity-card__handle\" aria-hidden=\"true\">::</span>" : "")
        + "<a class=\"aira-entity-card__title\" href=\"#topic\">" + escapeHtml(title)
        + "</a><a class=\"aira-entity-card__action\" href=\"#topic\">" + escapeHtml(action)
        + "</a></article>");
  }

  private static void renderMessageProcessingTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"processing-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"processing-title\">Message processing summary</h2><p class=\"aira-table-panel__description\">Code, numeric, date, wrapped-description, and secondary-text column treatments.</p></div></div>");
    out.println("        <div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">Message processing summary</caption><thead><tr><th scope=\"col\" class=\"aira-table__cell--code\">Jurisdiction Code</th><th scope=\"col\">Jurisdiction</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Messages Received</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Errors</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Error Rate</th><th scope=\"col\" class=\"aira-table__cell--date\">Last Submission</th><th scope=\"col\" class=\"aira-table__cell--wrap\">Notes</th></tr></thead><tbody>");
    processingRow(out, "AK-TEST", "Alaska test partner", "124,882", "38", "0.03%", "Jul 24, 2026", "Stable production-like feed with routine validation warnings.");
    processingRow(out, "AZ-SANDBOX", "Arizona sandbox", "82,410", "112", "0.14%", "Jul 23, 2026", "Recent onboarding messages include several optional segment warnings.");
    processingRow(out, "CO-PILOT", "Colorado pilot", "215,909", "64", "0.03%", "Jul 24, 2026", "Pilot feed includes new facility identifiers under review.");
    processingRow(out, "MN-UAT", "Minnesota UAT", "49,320", "21", "0.04%", "Jul 22, 2026", "UAT submissions paused briefly for certificate maintenance.");
    out.println("        </tbody></table></div>");
    out.println("      </section>");
  }

  private static void processingRow(PrintWriter out, String code, String jurisdiction, String received, String errors,
      String rate, String lastSubmission, String notes) {
    out.println("          <tr><th scope=\"row\" class=\"aira-table__cell--code\">" + escapeHtml(code)
        + "</th><td><span class=\"aira-table__cell--primary\">" + escapeHtml(jurisdiction)
        + "</span><br><span class=\"aira-table__cell--secondary\">Synthetic demonstration data</span></td><td class=\"aira-table__cell--numeric\">"
        + escapeHtml(received) + "</td><td class=\"aira-table__cell--numeric\">" + escapeHtml(errors)
        + "</td><td class=\"aira-table__cell--numeric\">" + escapeHtml(rate)
        + "</td><td class=\"aira-table__cell--date\">" + escapeHtml(lastSubmission)
        + "</td><td class=\"aira-table__cell--wrap\">" + escapeHtml(notes) + "</td></tr>");
  }

  private static void renderSemanticTablePanels(PrintWriter out) {
    out.println("      <div class=\"aira-grid\">");
    renderFlaggedAgendaTable(out, "warning", "Open items", "Carried forward from previous meetings.",
        "Prior Item", "Reason", "Re-add");
    renderFlaggedAgendaTable(out, "danger", "Curated topic cadence", "Topics due soon or overdue for discussion.",
        "Topic", "Cadence", "Quick add");
    out.println("      </div>");
  }

  private static void renderFlaggedAgendaTable(PrintWriter out, String variant, String title, String description,
      String firstColumn, String secondColumn, String action) {
    String id = "flagged-table-" + variant;
    out.println("        <section class=\"aira-table-panel aira-table-panel--" + escapeHtml(variant)
        + "\" aria-labelledby=\"" + id + "-title\">");
    out.println("          <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\""
        + id + "-title\">" + escapeHtml(title) + "</h2><p class=\"aira-table-panel__description\">"
        + escapeHtml(description) + "</p></div></div>");
    out.println("          <div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">"
        + escapeHtml(title) + "</caption><thead><tr><th scope=\"col\">" + escapeHtml(firstColumn)
        + "</th><th scope=\"col\">" + escapeHtml(secondColumn)
        + "</th><th scope=\"col\" class=\"aira-table__cell--actions\"><span class=\"aira-visually-hidden\">Actions</span></th></tr></thead><tbody>");
    if ("warning".equals(variant)) {
      flaggedAgendaRow(out, "Transport retry policy", "Needs revision", action);
      flaggedAgendaRow(out, "Query response examples", "Postponed", action);
    } else {
      flaggedAgendaRow(out, "Patient matching signals", "Overdue", action);
      flaggedAgendaRow(out, "Inventory reconciliation", "Due soon", action);
    }
    out.println("          </tbody></table></div>");
    out.println("        </section>");
  }

  private static void flaggedAgendaRow(PrintWriter out, String item, String state, String action) {
    out.println("            <tr><th scope=\"row\" class=\"aira-table__cell--primary\">" + escapeHtml(item)
        + "</th><td><span class=\"aira-badge " + badgeClassForStage(state) + "\">" + escapeHtml(state)
        + "</span></td><td class=\"aira-table__cell--actions\"><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">"
        + escapeHtml(action) + "</button></td></tr>");
  }

  private static void renderBrandHeaderTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"brand-header-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"brand-header-title\">Branded header variant</h2><p class=\"aira-table-panel__description\">The light header is the standard default. Use the navy branded header sparingly for stronger summary tables.</p></div></div>");
    out.println("        <div class=\"aira-table-wrap\"><table class=\"aira-table aira-table--brand-header\"><caption class=\"aira-visually-hidden\">Branded header table example</caption><thead><tr><th scope=\"col\">Program Area</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Active Topics</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Upcoming Meetings</th></tr></thead><tbody><tr><th scope=\"row\">Emerging Standards</th><td class=\"aira-table__cell--numeric\">18</td><td class=\"aira-table__cell--numeric\">4</td></tr><tr><th scope=\"row\">Building Bridges</th><td class=\"aira-table__cell--numeric\">9</td><td class=\"aira-table__cell--numeric\">2</td></tr></tbody></table></div>");
    out.println("      </section>");
  }

  private static void renderSortableTable(PrintWriter out, String sort, String direction) {
    List<TopicSortRow> rows = sortedTopicRows(sort, direction);
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"sortable-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"sortable-title\">Sortable headings</h2><p class=\"aira-table-panel__description\">Static server-side sorting demo using validated query parameters. Applications own sorting behavior.</p></div></div>");
    out.println("        <div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">Sortable topic table</caption><thead><tr>");
    sortableHeader(out, "topic", "Topic", sort, direction);
    out.println("<th scope=\"col\">Stage</th>");
    sortableHeader(out, "updated", "Last Updated", sort, direction);
    out.println("<th scope=\"col\" class=\"aira-table__cell--actions\"><span class=\"aira-visually-hidden\">Actions</span></th></tr></thead><tbody>");
    for (TopicSortRow row : rows) {
      out.println("          <tr><th scope=\"row\" class=\"aira-table__cell--primary\">" + escapeHtml(row.topic())
          + "</th><td><span class=\"aira-badge " + badgeClassForStage(row.stage()) + "\">" + escapeHtml(row.stage())
          + "</span></td><td class=\"aira-table__cell--date\">" + escapeHtml(row.updatedLabel())
          + "</td><td class=\"aira-table__cell--actions\"><a href=\"#topic\" aria-label=\"Open " + escapeHtml(row.topic())
          + "\">Open</a></td></tr>");
    }
    out.println("        </tbody></table></div>");
    out.println("      </section>");
  }

  private static void sortableHeader(PrintWriter out, String key, String label, String currentSort,
      String currentDirection) {
    boolean active = key.equals(currentSort);
    String nextDirection = active && "asc".equals(currentDirection) ? "desc" : "asc";
    String ariaSort = active ? ("asc".equals(currentDirection) ? "ascending" : "descending") : "none";
    String indicator = active ? ("asc".equals(currentDirection) ? " ▲" : " ▼") : "";
    out.println("<th scope=\"col\" aria-sort=\"" + ariaSort + "\"><a class=\"aira-table__sort\" href=\"?sort="
        + escapeHtml(key) + "&amp;direction=" + escapeHtml(nextDirection) + "\"><span>" + escapeHtml(label)
        + "</span><span aria-hidden=\"true\">" + indicator + "</span></a></th>");
  }

  private static List<TopicSortRow> sortedTopicRows(String sort, String direction) {
    List<TopicSortRow> rows = new ArrayList<>(List.of(
        new TopicSortRow("PHACT", "Growing Idea", "2026-07", "Jul 2026"),
        new TopicSortRow("Provider Enrollment Integration", "Monitor", "2026-07", "Jul 2026"),
        new TopicSortRow("Certificate Renewal", "Draft", "2026-06", "Jun 2026"),
        new TopicSortRow("School Data Exchange", "Proposed", "2026-06", "Jun 2026"),
        new TopicSortRow("Contextual Conditions", "Revision", "2026-05", "May 2026")));
    Comparator<TopicSortRow> comparator = "updated".equals(sort)
        ? Comparator.comparing(TopicSortRow::updatedSort).thenComparing(TopicSortRow::topic)
        : Comparator.comparing(TopicSortRow::topic);
    if ("desc".equals(direction)) {
      comparator = comparator.reversed();
    }
    rows.sort(comparator);
    return rows;
  }

  private static String allowedSort(String sort) {
    return "updated".equals(sort) ? "updated" : "topic";
  }

  private static String allowedDirection(String direction) {
    return "desc".equals(direction) ? "desc" : "asc";
  }

  private static void renderStickyHeaderTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"sticky-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"sticky-title\">Long table with sticky header</h2><p class=\"aira-table-panel__description\">Sticky headers are opt-in and only scroll inside an explicit constrained region.</p></div></div>");
    out.println("        <div class=\"aira-table-scroll\"><table class=\"aira-table aira-table--sticky-header\"><caption class=\"aira-visually-hidden\">Long table with sticky header</caption><thead><tr><th scope=\"col\">Queue</th><th scope=\"col\">Owner</th><th scope=\"col\" class=\"aira-table__cell--numeric\">Open Items</th><th scope=\"col\" class=\"aira-table__cell--date\">Oldest Item</th></tr></thead><tbody>");
    for (int i = 1; i <= 18; i++) {
      out.println("          <tr><th scope=\"row\">Validation queue " + i + "</th><td class=\"aira-table__cell--secondary\">Operations team " + ((i % 4) + 1)
          + "</td><td class=\"aira-table__cell--numeric\">" + (12 + i)
          + "</td><td class=\"aira-table__cell--date\">Jul " + String.format("%02d", (i % 24) + 1) + ", 2026</td></tr>");
    }
    out.println("        </tbody></table></div>");
    out.println("      </section>");
  }

  private static void renderEmptyTable(PrintWriter out) {
    out.println("      <section class=\"aira-table-panel\" aria-labelledby=\"empty-title\">");
    out.println("        <div class=\"aira-table-panel__header\"><div><h2 class=\"aira-table-panel__title\" id=\"empty-title\">Empty table</h2><p class=\"aira-table-panel__description\">Render a single empty-state cell with the correct colspan.</p></div></div>");
    out.println("        <div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">Empty filtered topic results</caption><thead><tr><th scope=\"col\">Topic</th><th scope=\"col\">Stage</th><th scope=\"col\">Champion</th><th scope=\"col\" class=\"aira-table__cell--date\">Last Updated</th></tr></thead><tbody><tr><td class=\"aira-table__empty\" colspan=\"4\">No matching records were found.</td></tr></tbody></table></div>");
    out.println("      </section>");
  }

  private record TopicSortRow(String topic, String stage, String updatedSort, String updatedLabel) {
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
    out.println("        <section class=\"aira-panel\" aria-labelledby=\"table-title\"><h2 class=\"aira-section-title\" id=\"table-title\">Recent topics</h2><div class=\"aira-table-wrap\"><table class=\"aira-table\"><caption class=\"aira-visually-hidden\">Recent management topics</caption><thead><tr><th scope=\"col\">Topic</th><th scope=\"col\">Stage</th><th scope=\"col\">Status</th><th scope=\"col\" class=\"aira-table__cell--actions\"><span class=\"aira-visually-hidden\">Actions</span></th></tr></thead><tbody>");
    tableRow(out, "FHIR subscription events", "Pilot", "Published", "success");
    tableRow(out, "Inventory reconciliation", "Monitor", "Draft", "warning");
    tableRow(out, "Legacy transport cleanup", "Parked", "Blocked", "danger");
    out.println("        </tbody></table></div></section>");
  }

  private static void tableRow(PrintWriter out, String topic, String stage, String status, String variant) {
    out.println("          <tr><th scope=\"row\" class=\"aira-table__cell--primary\">" + escapeHtml(topic) + "</th><td>" + escapeHtml(stage)
        + "</td><td><span class=\"aira-status aira-status--" + variant + "\"><span class=\"aira-status-dot\"></span>"
        + escapeHtml(status) + "</span></td><td class=\"aira-table__cell--actions\"><button class=\"aira-button aira-button--small aira-button--secondary\" type=\"button\">Edit</button></td></tr>");
  }

  private static void renderComponentReference(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AiraPage page = shell(request, "Component Reference", "components")
        .addLocalStylesheet("/css/application.css")
        .build();
    try (PrintWriter out = response.getWriter()) {
      page.writeStart(out);
      out.println("    <div class=\"aira-container--wide aira-stack aira-stack--loose\">");
      out.println("      <header><h1 class=\"aira-page-title\">Component reference</h1><p class=\"aira-page-intro\">First-release AIRA Web component variants for servlet-generated pages.</p></header>");
      out.println("      <section class=\"aira-panel\"><h2 class=\"aira-section-title\">Buttons</h2><div class=\"aira-action-group\"><button class=\"aira-button aira-button--primary\">Primary</button><button class=\"aira-button aira-button--secondary\">Secondary</button><button class=\"aira-button aira-button--tertiary\">Tertiary</button><button class=\"aira-button aira-button--success\">Success</button><button class=\"aira-button aira-button--danger\">Danger</button><button class=\"aira-button aira-button--ghost\">Ghost</button><a class=\"aira-button aira-button--tertiary\" href=\"#topics\">Tertiary link</a><button class=\"aira-button aira-button--small\">Small</button><button class=\"aira-button aira-button--icon\" aria-label=\"Add\">+</button></div></section>");
      out.println("      <section class=\"aira-panel\"><h2 class=\"aira-section-title\">Badges and tags</h2><div class=\"aira-tag-list\"><span class=\"aira-badge\">Neutral</span><span class=\"aira-badge aira-badge--subtle\">Subtle</span><span class=\"aira-badge aira-badge--info\">Info</span><span class=\"aira-badge aira-badge--success\">Success</span><span class=\"aira-badge aira-badge--warning\">Warning</span><span class=\"aira-badge aira-badge--danger\">Danger</span><span class=\"aira-tag aira-tag--outline\">Outline tag</span></div></section>");
      renderSearchSuggestionsExample(out);
      renderDialogExample(out);
      renderManagementForm(out, request.getContextPath());
      out.println("      <section class=\"aira-alert aira-alert--info\"><p class=\"aira-alert__title\">Information alert</p><p>Alerts use pale semantic surfaces and a strong left border.</p></section>");
      renderFeaturedCards(out);
      renderTopicChoices(out);
      renderMeetings(out);
      out.println("    </div>");
      page.writeEnd(out);
    }
  }

  private static void renderSearchSuggestionsExample(PrintWriter out) {
    out.println("      <section class=\"aira-panel demo-search-suggestions-example\" aria-labelledby=\"search-suggestions-title\">");
    out.println("        <h2 class=\"aira-section-title\" id=\"search-suggestions-title\">Search suggestions</h2>");
    out.println("        <p class=\"aira-muted\">Anchored grouped suggestion popover for combobox/listbox search behavior.</p>");
    out.println("        <div class=\"aira-global-search aira-search--has-suggestions\">");
    out.println("          <form class=\"aira-search-form\" role=\"search\" action=\"#search-results\">");
    out.println("            <input class=\"aira-search-input\" type=\"text\" name=\"q\" value=\"certificate\" role=\"combobox\" aria-expanded=\"true\" aria-controls=\"component-search-listbox\" aria-activedescendant=\"component-search-option-2\" autocomplete=\"off\" aria-label=\"Search topics or meetings\">");
    out.println("          </form>");
    out.println("          <div class=\"aira-search-popover\" id=\"component-search-popover\">");
    out.println("            <div class=\"aira-search-listbox\" id=\"component-search-listbox\" role=\"listbox\">");
    out.println("              <div class=\"aira-search-group\" role=\"group\" aria-label=\"Topics\"><p class=\"aira-search-group__label\">Topics</p>");
    out.println("                <a class=\"aira-search-option\" id=\"component-search-option-1\" role=\"option\" href=\"#topic\"><span class=\"aira-search-option__title\"><mark>Certificate</mark> Management</span><span class=\"aira-search-option__meta\">Emerging Standards &middot; Gathering Information</span></a>");
    out.println("              </div>");
    out.println("              <div class=\"aira-search-group\" role=\"group\" aria-label=\"Upcoming meetings\"><p class=\"aira-search-group__label\">Upcoming meetings</p>");
    out.println("                <a class=\"aira-search-option\" id=\"component-search-option-2\" role=\"option\" aria-selected=\"true\" href=\"#meeting\"><span class=\"aira-search-option__title\">Immunization Focus Group</span><span class=\"aira-search-option__meta\">Aug 14, 2026 &middot; 9:25 AM MDT &middot; Emerging Standards</span><span class=\"aira-search-option__match\">Agenda match: <span class=\"aira-text-highlight\">Certificate</span> Management</span></a>");
    out.println("              </div>");
    out.println("            </div>");
    out.println("            <a class=\"aira-search-option aira-search-option--action\" role=\"option\" href=\"#search-results\">View all results for &quot;certificate&quot;</a>");
    out.println("            <p class=\"aira-search-status\" hidden>No matching topics or meetings</p>");
    out.println("          </div>");
    out.println("        </div>");
    out.println("      </section>");
  }

  private static void renderDialogExample(PrintWriter out) {
    out.println("      <section class=\"aira-panel\" aria-labelledby=\"dialog-title\">");
    out.println("        <h2 class=\"aira-section-title\" id=\"dialog-title\">Dialog</h2>");
    out.println("        <p class=\"aira-muted\">Native dialog surface using shared AIRA tokens.</p>");
    out.println("        <button class=\"aira-button aira-button--secondary\" type=\"button\" data-dialog-open=\"component-dialog\">Open dialog</button>");
    out.println("        <dialog class=\"aira-dialog\" id=\"component-dialog\" aria-labelledby=\"component-dialog-title\">");
    out.println("          <h3 class=\"aira-dialog__title\" id=\"component-dialog-title\">Legal term details</h3>");
    out.println("          <div class=\"aira-dialog__body\"><p>Use native showModal() and close() behavior for focus handling, Escape-to-close, and top-layer stacking. Applications own the JavaScript that opens the dialog.</p></div>");
    out.println("          <div class=\"aira-dialog__actions aira-form-actions aira-form-actions--end\"><button type=\"button\" class=\"aira-button aira-button--secondary\" data-dialog-close=\"component-dialog\">Close</button></div>");
    out.println("        </dialog>");
    out.println("        <script>");
    out.println("          document.querySelectorAll('[data-dialog-open]').forEach(function (button) {");
    out.println("            button.addEventListener('click', function () {");
    out.println("              var dialog = document.getElementById(button.getAttribute('data-dialog-open'));");
    out.println("              if (dialog && typeof dialog.showModal === 'function') { dialog.showModal(); }");
    out.println("            });");
    out.println("          });");
    out.println("          document.querySelectorAll('[data-dialog-close]').forEach(function (button) {");
    out.println("            button.addEventListener('click', function () {");
    out.println("              var dialog = document.getElementById(button.getAttribute('data-dialog-close'));");
    out.println("              if (dialog) { dialog.close(); }");
    out.println("            });");
    out.println("          });");
    out.println("        </script>");
    out.println("      </section>");
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
