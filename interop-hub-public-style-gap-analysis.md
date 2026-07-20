# InteropHub Public Style Gap Analysis for AIRA Web

Generated from the local InteropHub instance at `http://localhost:8080/hub/` and the current `aira-web` project.

Supporting files:

- `hub-site-map-catalog.md`: route catalog from the crawl.
- `hub-public-style-snapshot.md`: sampled public page classes, selectors, and inline CSS inventory.
- `temp/analyze_hub_public_styles.py`: script used to generate the style snapshot.

## Scope

This review focuses on public or public-adjacent InteropHub pages that are likely to matter during rebranding:

- Login and email/magic-link entry pages.
- Campaign registration and registration completion pages.
- Meeting join and attendance pages.
- Topic browsing, topic selection, voting, review, and CDC signal pages.
- Public meeting agenda and meeting series pages.
- Application access pages.

Authenticated admin pages were not the main target, but some admin navigation styles were noted because the logged-in `/hub/welcome` route currently presents an admin-style shell.

The goal is not to reproduce InteropHub's current visual design. The useful output is a list of visual and interaction concepts that AIRA Web should support so InteropHub can be redesigned with reusable AIRA-styled pieces.

## Current AIRA Web Support

The AIRA Web project already has a strong foundation for ordinary application pages.

Supported in `aira.css` today:

- Brand and semantic tokens: navy, green, gray, warning, success, danger, focus, spacing, radius, shell dimensions.
- Base page shell: `.aira-app`, `.aira-header`, `.aira-primary-nav`, `.aira-main`, `.aira-footer`.
- Page header: `.aira-page-header`, `.aira-page-title`, `.aira-page-intro`, `.aira-page-actions`.
- Typography helpers: `.aira-section-title`, `.aira-subsection-title`, `.aira-meta`.
- Links, buttons, panels/cards, form fields, tables, alerts.
- Compact table density mode.
- Responsive shell behavior and reduced-motion defaults.

Supported by `aira-web-components` today:

- Document head and shared stylesheet loading.
- Optional local stylesheet loading after `aira.css`.
- Skip link.
- Header identity area with optional logo and application subtitle.
- Horizontal primary navigation.
- Main container and optional page heading/intro.
- Required institutional footer.

Not currently emitted by the Java component layer:

- Header action controls, even though `.aira-header-actions` exists in CSS.
- Sidebar navigation.
- Breadcrumbs.
- Tab/segmented navigation.
- Public landing/login layout variants.
- Modal/dialog/bottom-sheet structure.
- Status badges.
- Toggle/choice-card controls.
- Environment ribbon.

## InteropHub Style Systems Observed

The current InteropHub CSS is split between `/hub/css/main.css` and substantial inline/page-local styles. The shared stylesheet had 322 selectors and 173 unique class selectors. The sampled inline styles added 462 selectors and 228 unique class selectors.

Major class families observed:

- `es-*`: topic browsing, topic selection, review, CDC signals, bottom sheets, sticky action bars, table voting/results.
- `agenda-*`: public meeting agenda, inline agenda editing controls, agenda item states, presenter controls.
- `estp-*`: Emerging Standards topic portal shell with sidebar/filter navigation.
- `attend-*`: meeting attendance form, attendee list, hopes/comments, topic interest checklists.
- `mseries-*`: meeting series list pages.
- `admin-*`: admin rail/navigation, currently also seen on logged-in welcome/home.
- `doc-*`: document/article-style pages and doc cards.
- `stat-*`: dashboard statistic cards.

The public pages are not just simple forms. They include task-specific interaction surfaces: filterable topic lists, review scoring controls, fixed mobile action bars, bottom sheets, event/meeting lists, status badges, and lightweight dashboards.

## Reusable Concepts AIRA Web Should Consider Adding

### 1. Public Entry and Onboarding Layouts

InteropHub currently uses centered containers, multi-panel login pages, image banners, prominent headings, form panels, and a compact legal footer.

AIRA Web has the application shell, but it does not yet have public/login variants. For InteropHub rebranding, AIRA Web would benefit from:

- `.aira-public-page`: centered or reading-width public page wrapper.
- `.aira-login-layout`: public sign-in/onboarding layout with optional image/media panel.
- `.aira-public-panel`: slightly roomier panel variant for login, registration, confirmation, and completion screens.
- `.aira-public-title` and `.aira-public-intro`: public-facing heading scale distinct from routine application page titles.
- Standard form action row for public flows: primary submit plus secondary/cancel link.

This should stay restrained and aligned with the AIRA standard: useful for login and onboarding, not a general marketing hero system.

### 2. Public Registration and Completion Flows

Routes such as `/hub/register/{code}`, `/hub/register/complete/{code}`, and `/hub/registerForMeeting/{code}/{meeting}` use simple forms mixed with next-step cards and meeting/topic choices.

AIRA Web already supports inputs, buttons, alerts, panels, and cards. Missing reusable concepts:

- Completion/confirmation page layout.
- Next-step callout panel with one primary follow-on action.
- Multi-option checklist blocks for meetings, groups, or topics.
- Required-field and validation summary patterns for public forms.
- Secondary button-link style that works for anchors and buttons consistently.

Recommended support:

- Add a standard `.aira-next-step` or `.aira-callout--next` pattern.
- Add `.aira-choice-list`, `.aira-choice-item`, and selected/disabled states for checkbox/radio-driven choices.
- Add a validation summary pattern for form-level errors, not only field-level errors.

### 3. Topic Cards, Choice Rows, and Review Rows

The topic pages are the largest public style need. Current InteropHub uses dense repeated rows with:

- A checkbox or score control column.
- A content column with title, status, preview text, stage/neighborhood metadata.
- State modifiers such as selected, subscribed, meeting-registered, reviewed, filtered-out, and policy-blocked.
- Large repeated lists grouped by stage or neighborhood.

AIRA Web has generic cards and tables, but not a row-based choice/review list component.

Recommended reusable concepts:

- `.aira-choice-row`: a two-column selectable row for public participation workflows.
- `.aira-choice-row__control`, `__body`, `__title`, `__meta`, `__summary`.
- State modifiers for selected, completed, disabled, muted, warning, and info.
- `.aira-grouped-list` with group headings and compact spacing.
- A review/scoring control pattern that can support 1-5 or 0-5 scoring without each app inventing button grids.

This should not be InteropHub-specific. It is a reusable pattern for surveys, topic selection, standards review, meeting registration, and preference collection.

### 4. Sticky Mobile Action Bars

Current InteropHub topic and review flows rely on fixed bottom action areas:

- Sticky "continue/save/submit" actions.
- Safe-area padding for mobile.
- Fixed progress/footer bars for review completion.
- Disabled state communication.

AIRA Web currently has page actions near the title but no sticky action bar.

Recommended support:

- `.aira-sticky-actions`: fixed bottom action bar for mobile-first flows.
- A desktop behavior that either becomes a normal inline action row or remains constrained to a readable width.
- Safe-area support via `env(safe-area-inset-bottom)`.
- Guidance that sticky bars should not obscure focused fields or the institutional footer.

This is important for long topic/review pages where the main action is tied to selection state.

### 5. Bottom Sheets and Lightweight Dialogs

Topic voting pages use bottom sheets/overlays for topic detail and confirmation. Current AIRA CSS documents dialogs as a needed component but does not implement them.

Recommended support:

- `.aira-dialog` for centered modal dialogs.
- `.aira-bottom-sheet` for mobile-first detail/confirmation panels.
- `.aira-overlay`.
- Header/body/footer regions and action alignment.
- Open-state body class guidance for scroll locking.

The CSS alone is not enough for accessible dialogs. AIRA Web should document required focus trap, Escape handling, focus return, and accessible names.

### 6. Sidebar and Filter Navigation for Public Topic Portals

The Emerging Standards topic portal uses a page-local `estp-*` shell: sidebar, navigation groups, active links, summaries, overview cards, search, and responsive layout.

AIRA Web has horizontal primary nav but no sidebar component.

Recommended support:

- `.aira-layout-sidebar` or `.aira-two-column-layout`.
- `.aira-sidebar`, `.aira-sidebar-nav`, `.aira-sidebar-section`, `.aira-sidebar-link`.
- Active state using a green/navy indicator.
- Summary/count metadata inside nav links.
- Responsive collapse/stack behavior.

This would serve both public topic browsing and admin pages without tying the design to InteropHub.

### 7. Tabs, Segmented Views, and Filter Chips

Topic pages communicate peer views and filters:

- All Topics, Meetings, My Topics.
- Stage filters.
- Neighborhood filters.
- Review score filters.
- Active/inactive visual states with counts.

AIRA Web standards mention tabs but the CSS does not implement them.

Recommended support:

- `.aira-tabs` for peer views.
- `.aira-segmented-control` for compact mode switching.
- `.aira-filter-list` / `.aira-filter-chip` with active state and optional count.
- Guidance on when to use tabs versus filter chips.

This would reduce the need for custom `estp-nav-link` and repeated filter styling.

### 8. Status Badges, Pills, and Semantic Labels

InteropHub uses many small state labels:

- Topic state badges.
- Meeting status badges.
- Registration mode badges.
- Enabled/disabled status icons.
- CDC policy badges.
- Agenda item status badges.

AIRA Web standards call for badges/status labels, but the current CSS does not include a reusable badge system.

Recommended support:

- `.aira-badge` base.
- Variants: neutral, info, success, warning, danger, selected, muted.
- Optional pill shape.
- Count badge style for nav/filter labels.
- Icon/text pairing guidance so color is not the only signal.

### 9. Meeting and Event List Patterns

Meeting attendance, agenda, and meeting series pages use event-specific communication:

- Date/time blocks.
- Upcoming/past grouping.
- Meeting status badges.
- Agenda links.
- Attendance forms and attendee tables.
- "Today’s attendees" and hopes/comments sections.

AIRA Web tables and panels cover part of this, but event lists would currently be app-specific.

Recommended support:

- `.aira-event-list`, `.aira-event-row`, `.aira-event-date`, `.aira-event-title`, `.aira-event-meta`, `.aira-event-actions`.
- Group headings for upcoming/past/current.
- Small date/time block style.
- Empty-state component for "no attendees" or "no meetings".

This is broadly reusable for connectathons, workgroups, webinars, meetings, and schedules.

### 10. Inline Editing and Operational Agenda Controls

The agenda page has substantial inline/page-local styling:

- Click-to-edit regions.
- Inline forms in table rows.
- Quick-pick controls.
- Presenter role controls.
- Agenda status buttons.
- Duration warnings/OK/info.
- Print exclusion via `.no-print`.

Some of this is domain-specific and should remain in InteropHub. But AIRA Web should consider generic support for:

- Inline form rows inside tables/lists.
- Small button groups.
- Compact metadata rows.
- Semantic duration/status indicators.
- Print utility classes such as `.aira-no-print`.

The full agenda editor should not become a generic AIRA Web component unless another app needs it.

### 11. Data Summaries and Stat Cards

InteropHub uses stat cards and large counts in admin/user dashboards and registration display pages. The current AIRA theme has cards but no metric/stat style.

Recommended support:

- `.aira-stat-grid`, `.aira-stat-card`, `.aira-stat-value`, `.aira-stat-label`.
- Optional compact and display sizes.
- Guidance for accessible labels and not relying on giant numbers alone.

This would also help dashboards in other AIRA applications.

### 12. Environment Indicator

InteropHub uses a diagonal fixed `LOCAL` ribbon.

AIRA Web components do not provide an environment indicator, though the standards call out optional non-production indicators.

Recommended support:

- Add an environment indicator to the header component rather than a diagonal overlay by default.
- Provide a CSS class such as `.aira-environment-badge`.
- Optionally document a stronger non-production ribbon variant for local/demo systems only.

The current diagonal ribbon is highly visible, but it can overlap content and should not be the default shared pattern.

### 13. Legal Footer Alignment

InteropHub already includes a legal footer, but it uses a light inline footer with raw organization URL text in some pages and older legal links in the crawled content.

AIRA Web already has the stronger required footer pattern:

- Navy background.
- Green top rule.
- Linked organization name.
- Current legal destinations from the standards.

Recommendation: migrate public InteropHub pages to the AIRA Web footer as-is. Do not preserve the current InteropHub light footer unless there is a specific public-page exception.

## Current InteropHub CSS Concepts That Should Stay App-Specific

These concepts are probably too domain-specific for AIRA Web core:

- Emerging Standards-specific `es-*` terminology such as neighborhoods, stages, curation, CDC policy, subscriptions, and campaign review.
- Agenda editor details such as presenter roles, quick picks, previous agenda items, and duration calculations.
- InteropHub-specific registration cleanup, meeting attendance "hopes", and CDC signal wording.
- Exact topic row color states and gradients.
- QR generator/admin detail screens.

AIRA Web should provide the primitives: choice rows, badges, sticky actions, sidebars, filters, dialogs, event lists, tables, forms, alerts. InteropHub should provide domain naming and workflow-specific composition.

## Priority Recommendations

1. Add public/login/onboarding layout primitives.
2. Add badge/status label styles.
3. Add choice-row and grouped-list patterns for topic selection and review.
4. Add tabs/segmented controls/filter chips.
5. Add sidebar layout/navigation.
6. Add sticky action bar and bottom-sheet/dialog styles.
7. Add event/meeting list styles.
8. Add stat cards and empty-state patterns.
9. Add environment indicator support in the component header.
10. Add print utility support, at least `.aira-no-print`.

## Suggested Mapping From InteropHub to AIRA Web

| InteropHub concept | Current classes/examples | AIRA Web direction |
|---|---|---|
| Login/registration container | `.container`, `.login-page`, `.login-form` | `.aira-public-page`, `.aira-public-panel`, `.aira-field`, `.aira-button` |
| Public confirmation/next step | `.es-topics-next-step`, completion pages | `.aira-callout--next`, `.aira-page-actions` |
| Topic selection row | `.es-topic-row`, `.es-topic-checkbox-wrap`, `.es-topic-state` | `.aira-choice-row`, `.aira-badge`, `.aira-grouped-list` |
| Review score controls | `.es-review-score-btn`, `.es-review-edit-wrap` | `.aira-rating-control` or `.aira-score-control` |
| Public topic portal navigation | `.estp-sidebar`, `.estp-nav-link`, `.estp-nav-summary` | `.aira-sidebar`, `.aira-sidebar-link`, `.aira-filter-chip` |
| Sticky mobile submit | `.es-sticky-action`, `.es-review-progress-footer` | `.aira-sticky-actions` |
| Topic detail/confirmation sheet | `.es-detail-sheet`, `.es-confirm-sheet`, overlays | `.aira-bottom-sheet`, `.aira-dialog`, `.aira-overlay` |
| Meeting attendance form | `.attend-form`, `.attend-action-fieldset` | existing form classes plus `.aira-choice-list` |
| Meeting series list | `.mseries-row`, `.mseries-date`, `.mseries-badge` | `.aira-event-list`, `.aira-event-date`, `.aira-badge` |
| Agenda editing | `.agenda-inline-form`, `.agenda-status-btn`, `.no-print` | app-specific, with shared button group, status, print utility primitives |
| Dashboard numbers | `.stat-card`, `.es-reg-count` | `.aira-stat-card`, `.aira-stat-value` |
| Local environment marker | `.env-ribbon` | `.aira-environment-badge` in header; optional ribbon variant |

## Implementation Notes for AIRA Web

Keep the public API stable and prefixed with `aira-`. InteropHub should be able to load:

```html
<link rel="stylesheet" href="/hub/aira/css/aira.css">
<link rel="stylesheet" href="/hub/css/interophub.css">
```

The shared CSS should provide layout and state vocabulary. InteropHub-specific CSS should mostly set domain composition, not redefine base colors, type, footer, form controls, or primary actions.

The Java component layer may need to grow alongside CSS. The most useful next renderer additions would be:

- Header actions and environment badge.
- Optional sidebar navigation.
- Breadcrumbs.
- Optional public-page shell variant without heavy application navigation.

For the rebrand, start by replacing the global page shell, typography, footer, form controls, buttons, tables, and badges. Then migrate topic/review pages to shared primitives while leaving `es-*` workflow logic in InteropHub.

