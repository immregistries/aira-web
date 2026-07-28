# AIRA Web

Reusable AIRA web application resources for Java Servlet applications that generate HTML directly from Java.

This repository provides a first usable shared design-system release for AIRA applications: a versioned CSS theme, small Java renderers for the common application shell, and a servlet-only demo application that consumes the same artifacts downstream applications will consume.

The shared stylesheet is for dense AIRA technical applications: collaboration workspaces, operational tools, dashboards, registries, repositories, validation tools, and administrative screens. It is not intended to replace AIRA marketing or public-facing website styles.

## Modules

- `aira-web-theme` - JAR containing shared static resources under `META-INF/resources/aira/`, including `META-INF/resources/aira/css/aira.css` and the default AIRA logo image.
- `aira-web-components` - plain Java JAR for rendering the document head, shared stylesheet links, global header, optional contextual header/navigation, main content opening, and required footer.
- `aira-web-demo` - WAR demonstrating a servlet-only application that consumes `aira-web-components`; it does not copy the shared theme CSS.
- `docs/` - standards, adoption guidance, and component usage guidance.
- `temp/` - reference and analysis material. These files are not part of the published API.

## Build

```bash
mvn clean verify
```

The project is configured for Java 17. Plugin and dependency versions are managed from the parent POM.

The public theme stylesheet is generated during the `aira-web-theme` build from topical source files in `aira-web-theme/src/main/theme-css/`. Downstream applications should continue to load only the generated `/aira/css/aira.css` resource.

## Run the Demo

Run the demo locally with Jetty:

```bash
mvn -pl aira-web-demo -am package jetty:run
```

Then inspect:

```text
http://localhost:8080/aira-web-demo/
http://localhost:8080/aira-web-demo/topic
http://localhost:8080/aira-web-demo/tables
http://localhost:8080/aira-web-demo/management
http://localhost:8080/aira-web-demo/components
```

If port 8080 is already in use:

```powershell
mvn -pl aira-web-demo -am package jetty:run "-Djetty.http.port=18080"
```

## Consuming AIRA Web

Applications that only need CSS can depend on `aira-web-theme`. Applications that want the generated shell should depend on `aira-web-components`, which brings in the theme transitively.

The shared stylesheet is served by the servlet container from the dependency JAR:

```html
<link rel="stylesheet" href="{contextPath}/aira/css/aira.css">
```

Application CSS may be loaded afterward for domain-specific composition:

```html
<link rel="stylesheet" href="{contextPath}/css/application.css">
```

Application CSS should not redefine AIRA brand tokens, the base shell, footer, form controls, primary buttons, or shared component classes.

The stylesheet is desktop-first and compact by default. It includes defensive responsiveness for wrapping headers, overflowing tables and technical figures, sidebars, right rails, and browser zoom. Mobile-optimized product flows should be explicit application-specific patterns layered on top of the shared baseline.

## Public Resource Contract

These resource paths are intended for downstream applications to reference through their servlet context path after depending on `aira-web-theme` or `aira-web-components`.

| Resource | Path | Contract |
|---|---|---|
| Shared stylesheet | `/aira/css/aira.css` | Public, stable first-release path. Also available as `AiraDefaults.STYLESHEET_PATH`. |
| Default horizontal AIRA logo | `/aira/images/AIRA_2018Logo_BlueGreen_Hor.png` | Public first-release path for the packaged default logo. Also available as `AiraDefaults.DEFAULT_LOGO_PATH`. Filename may change only with a documented breaking release. |

When using `aira-web-components`, prefer the constants in `AiraDefaults` rather than hard-coding these paths.

## Logo Integration

The theme packages the default horizontal blue/green AIRA logo. Applications that use the Java shell renderer should configure it explicitly:

```java
.logo(new AiraLogo(AiraDefaults.DEFAULT_LOGO_PATH, AiraDefaults.DEFAULT_LOGO_ALT_TEXT))
```

If no logo is configured, `AiraPage` renders a small text placeholder. That fallback is useful during early development but should not be treated as the preferred production identity.

Use concise alt text that identifies the organization, such as `American Immunization Registry Association`. Do not include words like "logo" unless needed to distinguish the image's purpose in context.

## First-Release Component Categories

The shared CSS includes:

- Two-level shell: global header, identity, search, actions, account area, environment badge, contextual title/navigation.
- Layout primitives: standard/narrow/wide containers, stacks, clusters, responsive grids, card grids, sidebar layouts, right-rail layouts, split layouts, toolbars, reading width.
- Buttons and action areas: primary, secondary, tertiary, success, danger, ghost, link, small, icon-compatible, grouped, responsive, and sticky actions.
- Forms: form containers, field rows, fieldsets, native checkboxes/radios, search fields, switches, segmented/rating/chip controls, inline forms, action areas, help, errors, and error summaries.
- Badges, tags, status text, and status dots, including subtle, outline, info, success, warning, and danger badge variants.
- Panels, cards, elevated cards, interactive cards, section cards, choice rows, resource-card structure, and empty states.
- Event lists for meetings, workshops, webinars, and other dated events.
- Topic-page primitives: topic headers, metadata chips, status cards, figures, outcomes, compact meeting rows, relationship rows, resources, recently viewed entries, and topic detail strips.
- Tables with panels, light default headers, optional branded headers, sortable-heading styles, sticky headers, empty states, overflow wrappers, and compact density.
- Alerts, management layout helpers, footer, and small utilities.

The Java component layer intentionally remains small. It renders the shared shell and leaves page-specific content to the servlet.

## Application-Specific Behavior

AIRA Web does not provide JavaScript behavior, menus, dialogs, bottom sheets, drag-and-drop boards, data grids, authentication/session handling, or domain-specific workflows. Applications remain responsible for:

- Business rules and page-specific HTML.
- Form processing and validation.
- Any required JavaScript interactions.
- Domain-specific CSS that composes shared primitives.
- Approved logo asset selection and packaging.

## Known Missing Items

- Local Open Sans font packaging is not implemented yet. The CSS uses `"Open Sans", Arial, sans-serif` without a runtime network dependency.
- Dialogs, bottom sheets, menus, and shared JavaScript behavior are intentionally deferred.
