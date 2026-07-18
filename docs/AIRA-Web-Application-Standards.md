# AIRA Web Application Brand and Interface Standards

**Companion to the AIRA Brand Guide**  
**Version 1.0 - Draft, July 2026**

## 1. Purpose and authority

This standard translates the AIRA Brand Guide into practical requirements for AIRA-owned technical web applications. It does not replace the Brand Guide. The Brand Guide remains authoritative for the logo, approved colors, and organizational identity. This document adds the web-specific rules needed for responsive layouts, application components, accessibility, interaction states, and required institutional content.

These standards apply to public and authenticated AIRA web applications, including dashboards, registries, repositories, validation tools, collaboration tools, administrative interfaces, prototypes released for operational use, and application login and error pages.

When requirements conflict, use this order of precedence:

1. Legal, privacy, security, and accessibility requirements.
2. Functional usability for the intended application.
3. These web application standards.
4. Literal reproduction of print-oriented examples in the AIRA Brand Guide.

The goal is not for every application to look identical. The goal is for each application to clearly belong to AIRA, use predictable interface patterns, and remain usable for sustained technical work.

## 2. Core design principles

- **Clearly AIRA.** Use the approved logo, AIRA palette, Open Sans typography, and restrained navy/green visual language.
- **Built for work.** Technical applications should use space efficiently. Do not copy the public website's large hero areas or generous promotional whitespace onto operational screens.
- **Consistent before custom.** Reuse the shared application shell, components, tokens, and footer before adding application-specific styling.
- **Accessible by default.** Meet WCAG 2.2 Level AA and do not rely on brand colors in combinations that fail screen contrast requirements.
- **Restrained, not decorative.** Use the angled AIRA motif, photography, and large display text selectively. Operational screens should prioritize data, controls, and clear hierarchy.
- **Predictable.** Navigation, account controls, help, legal links, and page actions should appear in consistent locations.

## 3. Relationship to the public AIRA website

AIRA applications should look like siblings of immregistries.org, not embedded copies of it. Adopt the public site's organizational identity, strong headings, navy/green palette, direct calls to action, and institutional footer conventions. Deliberately depart from its promotional page density, large hero imagery, and extensive vertical spacing.

Use public-site treatments for login pages, public landing pages, onboarding, and major announcements. Use denser application patterns for tables, forms, dashboards, configuration pages, and other recurring work.

## 4. Application shell and page layout

### 4.1 Default shell

Every application should use the following structure:

1. A compact AIRA application header.
2. Primary application navigation.
3. Optional secondary navigation.
4. A fluid main content area.
5. The required institutional footer.

Recommended desktop dimensions:

- Green top rule: 4 px.
- Header: 64 px, including logo clear space.
- Horizontal navigation: 44-48 px when used.
- Expanded sidebar: 240 px.
- Collapsed sidebar: 64-72 px.
- Main content gutter: 24 px desktop, 16 px tablet, 12-16 px mobile.
- Reading-width content: maximum 72 characters per line.
- Data-intensive pages: fluid width, normally capped at 1,600 px only when extremely wide displays would reduce readability.

### 4.2 Page density

The default application density is compact but not crowded. Use a 4 px spacing base and the following scale: 4, 8, 12, 16, 24, 32, 48, and 64 px.

Use 16-24 px between major regions, 12-16 px inside ordinary components, and 8-12 px inside dense tables and toolbars. Avoid 48-96 px promotional gaps on working pages.

### 4.3 Page header

The page title and primary page actions should share one row on desktop and wrap naturally on narrow screens. Introductory prose is optional and should be brief. Do not place a large hero banner above routine application work.

## 5. Logo and organizational identity

- Use only approved AIRA logo files. Do not redraw the logo with text or CSS.
- Use SVG when available. Use transparent PNG only when SVG is not supported.
- Use the full-color horizontal logo on white or very light backgrounds.
- Use the approved white logo on navy, dark gray, or approved green backgrounds.
- Do not recolor, rotate, stretch, crop, outline, add effects, or place imagery within the logo clear space.
- Maintain clear space equal to at least half the height of the logo icon.
- Desktop header: horizontal logo approximately 160-220 px wide, with a visual height near 32 px.
- Mobile header: approved logomark or compact logo near 32 px high.
- Separate the application name from the logo with clear space or a thin neutral divider. The application name is not part of the logo.
- Use the approved logomark for the favicon and application icon.

The AIRA logo should normally link to the application home page. Provide a separate footer link to immregistries.org.

## 6. Color standards

### 6.1 Core AIRA palette

| Token | Hex | Primary web role | Restrictions |
|---|---|---|---|
| AIRA navy | `#1C3C50` | Primary text, headings, navigation, primary buttons, table headers, footer | Preferred high-contrast brand color |
| AIRA green | `#629F44` | Brand accents, rules, large icons, decorative geometry | Do not use for normal text on white or white normal text on green |
| Light green | `#C9E1C9` | Selected rows, badges, highlighted panels | Use navy or black text |
| Blue-gray | `#8098A4` | Input borders, secondary icons, metadata | Do not use for ordinary body text on white |
| Light gray | `#CCCCCB` | Decorative borders, dividers, disabled backgrounds | Not sufficient by itself for essential control boundaries |
| Dark gray | `#6F6E73` | Secondary text, captions, metadata | Suitable for normal text on white |
| Yellow | `#ECBC0A` | Limited highlight or warning accent | Do not use as text on white |
| Off-white | `#F2F2F2` | Page background, table striping, subtle surfaces | Use navy/dark text |

### 6.2 Web-only functional colors

The print palette does not provide all colors needed for interactive software. The following functional colors are permitted for accessibility and semantic meaning:

| Token | Hex | Use |
|---|---|---|
| Accessible green | `#3F732B` | Optional green button or link treatment with white text; affirmative action |
| Focus blue | `#005EA8` | Keyboard focus ring and informational emphasis |
| Success | `#0B6B3A` | Success messages and validated states |
| Warning text | `#6B4E00` | Warning message text on a pale yellow surface |
| Danger | `#B42318` | Errors and destructive actions |
| Dark navy hover | `#132D3D` | Primary button and dark navigation hover |

Functional colors do not replace the AIRA brand palette. They exist to communicate application state and meet contrast requirements.

### 6.3 Required color behavior

- Normal text should meet at least 4.5:1 contrast.
- Large text should meet at least 3:1 contrast.
- Essential control boundaries, icons, and states should meet at least 3:1 contrast.
- Use navy for ordinary links and primary actions.
- Do not communicate status by color alone. Pair color with text, icons, shape, or placement.
- Do not create application-specific brand palettes without approval.
- A dark mode is not part of this standard. Do not invent one independently.

## 7. Typography

### 7.1 Primary typeface

Use Open Sans throughout AIRA applications.

```css
font-family: "Open Sans", Arial, sans-serif;
```

Use weight 400 for body text, 600 for labels and interface emphasis, and 700 sparingly. Weight 300 is permitted only for large display headings on public or login pages. Do not use very light text for ordinary application headings.

Use a system monospace stack for code, identifiers, HL7/FHIR examples, logs, and machine-readable content.

### 7.2 Type scale

| Element | Size | Line height | Weight |
|---|---:|---:|---:|
| Public display title | 2.25rem / 36 px | 1.15 | 300-400 |
| Application page title | 1.75rem / 28 px | 1.2 | 400 |
| Section heading | 1.375rem / 22 px | 1.25 | 400-600 |
| Subsection heading | 1.125rem / 18 px | 1.35 | 600 |
| Body text | 1rem / 16 px | 1.5 | 400 |
| Labels and controls | 0.875rem / 14 px | 1.35 | 600 |
| Dense table text | 0.875rem / 14 px | 1.35 | 400 |
| Caption and metadata | 0.8125rem / 13 px | 1.4 | 400 |

Use sentence case. Avoid all-caps headings except very short utility labels. Do not use font size alone to create hierarchy; combine size, weight, spacing, and semantic heading levels.

## 8. Surfaces, borders, corners, and imagery

- Page background: off-white `#F2F2F2`.
- Primary content surfaces: white.
- Cards and panels: white with a 1 px light-gray border.
- Form controls: 1 px blue-gray border so the boundary remains visible.
- Corner radius: 4 px for controls; 6 px for cards, dialogs, and panels.
- Shadows: none by default. Use only a subtle shadow for floating menus, dialogs, and overlays.
- Gradients: do not use unless formally added to the brand system.
- Angled green or navy geometry: use for major section accents, login pages, or selected public-facing panels; do not apply it to every card or button.
- Photography: use primarily on landing, login, onboarding, and announcement pages. Do not place photography behind working data or the AIRA logo.

## 9. Navigation and page structure

### 9.1 Header

The application header should contain:

- Approved AIRA logo.
- Application name.
- Optional environment indicator for non-production systems.
- Help or documentation link when applicable.
- User/account control and sign-out action.

The default header is white with a 4 px green top rule and a subtle bottom border. Account controls should remain in a consistent location across applications.

### 9.2 Primary navigation

Use horizontal navigation when the application has five or fewer stable primary areas. Use a sidebar when the application has a deeper hierarchy or repeated administrative work.

Active navigation should use navy text plus a strong green indicator, such as a 3-4 px left border or bottom rule. Do not rely on green text alone.

### 9.3 Breadcrumbs

Use breadcrumbs only when users move through a true hierarchy. Do not add them to flat applications or repeat the primary navigation path unnecessarily.

## 10. Component standards

### 10.1 Links

- Body links: navy and underlined.
- Navigation links may omit underlines when location and interaction are clear.
- Hover must create a visible change beyond color alone where practical.
- Focus must use the standard focus ring.
- Do not use AIRA green as ordinary link text on white.

### 10.2 Buttons

- Primary: navy background, white text.
- Secondary: white background, navy border and text.
- Tertiary: text-style button using navy.
- Affirmative/success: accessible green only when the semantic distinction is useful.
- Destructive: danger red, with explicit action wording.
- Minimum height: 40 px; prefer 44 px on touch-oriented screens.
- Minimum icon-only target: 40 by 40 px and an accessible name.
- Use action verbs. Do not use all caps.
- Put the safest/common action first. Visually separate destructive actions.

### 10.3 Forms

- Place labels above fields.
- Do not use placeholder text as the only label.
- Use input height of at least 40 px.
- Use blue-gray borders and a 2 px focus ring in focus blue.
- Place help text below the label or field and errors immediately adjacent to the field.
- Identify required fields in text, not only with color.
- Preserve entered values after validation errors.
- Group related controls with `fieldset` and `legend` when appropriate.

### 10.4 Tables

- Header background: AIRA navy; header text: white, weight 600.
- Body rows: white with optional off-white striping.
- Selected row: light green with navy text.
- Hover row: very pale green or off-white, distinct from selected state.
- Comfortable cell padding: 12 px vertical, 16 px horizontal.
- Compact cell padding: 8 px vertical, 12 px horizontal.
- Support keyboard access, visible sort indicators, and clear pagination.
- Use sticky headers for long tables when they do not obscure focused content.
- Allow horizontal scrolling for genuinely tabular data on narrow screens. Do not automatically convert complex technical tables into cards.
- Provide meaningful empty, loading, and error states.

### 10.5 Cards and panels

Use cards to group related content, not to place every field inside a separate box. Default padding is 16 px; use 24 px for major panels. Card headings should be left aligned and close to their content.

### 10.6 Tabs

Use tabs for a small number of peer views that share a page context. Use a 3 px navy or green active indicator and full keyboard behavior. Do not use tabs for sequential steps.

### 10.7 Badges and status labels

Use pale surfaces with dark text. A light-green badge should use navy or accessible-green text, not white. Include a text label and do not rely solely on color.

### 10.8 Alerts

Alerts should include an icon, a concise heading, and message text. Use pale semantic surfaces with dark text and a stronger left border. Do not use large saturated color blocks behind paragraphs.

### 10.9 Dialogs and notifications

Dialogs must trap focus, provide a visible close control, support Escape when safe, and return focus to the invoking control. Notifications that disappear automatically must remain long enough to be read and must also be exposed to assistive technology.

## 11. Responsive design

- Begin with a usable narrow-screen layout and progressively enhance.
- At approximately 768 px, collapse horizontal navigation or move it into a menu.
- At approximately 1,024 px, allow the full desktop shell and expanded sidebar.
- Do not hard-code content to one breakpoint; components should respond to available space.
- Page title actions should stack below the title on narrow screens.
- Forms should normally use one column on mobile.
- Preserve tables as tables when their relationships matter; use horizontal scrolling and frozen identifiers where needed.
- Ensure content remains usable at 400% zoom and at a 320 CSS-pixel viewport, except for content that inherently requires two-dimensional presentation.

## 12. Accessibility requirements

All applications should target WCAG 2.2 Level AA.

Required practices include:

- Semantic HTML landmarks and a logical heading hierarchy.
- A "Skip to main content" link.
- Complete keyboard operation.
- A visible focus indicator that is not hidden by sticky headers or footers.
- Text and interface contrast meeting the ratios in this standard.
- Touch and pointer targets of at least 24 by 24 CSS px under WCAG, with an AIRA application standard of 40 px where practical.
- Form labels, instructions, errors, and status messages that are programmatically associated.
- Meaningful alternative text for informative images; empty alt text for decorative images.
- Support for browser zoom, reflow, text spacing overrides, and reduced-motion preferences.
- No forced disabling of password managers, paste, or accessible authentication mechanisms.
- Do not use a sticky footer that can obscure focused controls.

## 13. Content and data presentation

- Use concise page titles and action-oriented button labels.
- Use established technical terminology when the audience is expert, but define uncommon organizational terms.
- Keep prose in readable-width columns even when data grids use the full page width.
- Use dates, times, versions, and statuses consistently within each application.
- Place version and build details in the footer or an About dialog rather than inside page headings.
- Use plain-language error messages that state what happened and how to correct it.
- Avoid decorative icons without labels in administrative and technical workflows.

## 14. Required institutional footer and legal links

The footer is a required institutional component, not an optional visual treatment. It must appear on all application pages, including login pages, authenticated pages, public pages, maintenance pages, and application-generated error pages.

### 14.1 Required wording

**Line 1**  
American Immunization Registry Association (AIRA) supports collaboration, standards, and shared solutions that strengthen immunization information systems and improve the use of vaccination data to protect public health.

The organization name should link to `https://www.immregistries.org/` rather than displaying the raw URL.

**Line 2**  
`{Application Name} version {version} · © Copyright {year}, American Immunization Registry Association. All rights reserved. Privacy Policy · Terms and Conditions of Use`

"Privacy Policy" and "Terms and Conditions of Use" must link to the current official AIRA documents.

### 14.2 Footer presentation

- Use a compact, non-sticky footer.
- Preferred background: AIRA navy with white text and a 4 px green top rule.
- Use 13-14 px text and 8-12 px vertical padding per line.
- Underline legal and organizational links.
- Allow both lines to wrap naturally on mobile.
- Do not hide the footer behind authentication, infinite scrolling, or fixed overlays.
- Application name, version, and year should come from centralized build/configuration values.
- Legal URLs should be centrally configured so they can be updated without editing every page.

### 14.3 Current official legal destinations

As of July 2026:

- Privacy Policy: `https://aira.memberclicks.net/assets/docs/Organizational_Docs/AIRA%20Privacy%20Policy%20-%20Updated%20Jan%202026%20Final.pdf`
- Terms and Conditions of Use: `https://aira.memberclicks.net/assets/docs/Organizational_Docs/AIRA%20Terms%20of%20Use%20-%20Updated%202026%20Final.pdf`
- Organizational documents index: `https://www.immregistries.org/organizational-documents`

The organizational documents index is the source of truth when direct document URLs change.

## 15. Implementation and shared CSS

AIRA should maintain a shared, versioned theme package used across applications. At minimum, it should provide:

- CSS custom properties for brand, semantic colors, typography, spacing, radii, borders, and shell dimensions.
- Header, navigation, page-header, footer, button, form, table, card, tab, badge, alert, dialog, and loading-state styles.
- A compact density mode for data-heavy pages.
- Framework adapters where applications use Bootstrap or another component library.
- Automated accessibility and visual-regression tests for core components.

Applications may add domain-specific components, but should not redefine the logo, base typography, legal footer, or core brand tokens.

Load Open Sans from an approved hosted or packaged source. Include Arial and sans-serif fallbacks. Do not distribute font files as part of informal templates without confirming licensing and organizational packaging requirements.

## 16. Quality assurance checklist

Before release, confirm:

### Brand
- Approved logo asset and correct background variant.
- Required clear space and no distortion.
- Open Sans used for the interface.
- Core colors use approved tokens.
- Green and yellow are not used in inaccessible text combinations.

### Layout and components
- Shared application shell is used.
- Routine pages are compact and task-oriented.
- Page actions, navigation, and account controls are consistently placed.
- Forms, tables, buttons, and dialogs follow component standards.
- Responsive behavior works at mobile, tablet, desktop, and browser zoom.

### Accessibility
- WCAG 2.2 AA review completed.
- Keyboard-only operation works.
- Focus is visible and not obscured.
- Contrast is verified.
- Labels, errors, headings, landmarks, and status messages are programmatically correct.

### Institutional requirements
- Required footer appears on every page type.
- Organization, Privacy Policy, and Terms links are correct.
- Application name, version, and year are current.
- Copyright and legal text are not obscured or omitted.

## 17. Governance, maintenance, and exceptions

This standard should be maintained as a versioned companion to the AIRA Brand Guide. Review it when the Brand Guide, main AIRA website, legal documents, accessibility requirements, or shared application architecture changes.

Application-specific exceptions should be documented with the reason, scope, and approving owner. Accessibility or usability should never be reduced merely to preserve a print-oriented visual treatment.

The shared CSS package should have its own version number and release notes so applications can adopt updates deliberately.

## Appendix A - Starter CSS tokens

See the accompanying `aira-web-theme-starter.css` file for an implementation-ready starting point.

## Appendix B - Reference footer HTML

See the accompanying `aira-footer-reference.html` file.

## Appendix C - Source basis

- AIRA Brand Guide, supplied by AIRA.
- AIRA public website: `https://www.immregistries.org/`.
- AIRA Organizational Documents: `https://www.immregistries.org/organizational-documents`.
- W3C Web Content Accessibility Guidelines 2.2.

