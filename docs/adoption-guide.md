# AIRA Web Adoption Guide

This guide explains how Java Servlet applications can adopt shared AIRA web resources.

## Add the Maven Dependency

Applications that only need shared CSS and assets can depend on the theme JAR:

```xml
<dependency>
  <groupId>org.immregistries</groupId>
  <artifactId>aira-web-theme</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Use a deliberate, versioned dependency. Do not load an unversioned stylesheet from a central production URL, because that would allow unrelated deployments to change an application's interface without a controlled upgrade.

Applications that also want the standardized AIRA HTML application shell should depend on `aira-web-components`:

```xml
<dependency>
  <groupId>org.immregistries</groupId>
  <artifactId>aira-web-components</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

`aira-web-components` depends on `aira-web-theme`, so the theme JAR is included transitively.

## Why a Resource JAR

The first shared asset is CSS, not Java rendering code. Packaging the theme as a normal Maven JAR lets applications keep using their existing servlet rendering model while adopting shared static resources through normal dependency management.

This also keeps upgrades explicit. An application changes its dependency version, tests the new theme, and deploys on its own schedule.

## How `META-INF/resources` Works

Servlet containers expose static resources found in dependency JARs under `META-INF/resources` as if they were part of the web application root.

The theme JAR contains:

```text
META-INF/resources/aira/css/aira.css
```

When the JAR is in `WEB-INF/lib`, the application can request:

```text
{contextPath}/aira/css/aira.css
```

No servlet or controller is needed to serve that file.

## Load the Stylesheets

Generate stylesheet links with the deployed context path:

```html
<link rel="stylesheet" href="{contextPath}/aira/css/aira.css">
<link rel="stylesheet" href="{contextPath}/css/application.css">
```

Load local CSS after `aira.css`. Application-specific CSS should extend or compose the shared theme, not duplicate it.

## Shared CSS Scope

Shared CSS should contain:

- AIRA brand and semantic custom properties.
- Typography, spacing, borders, focus states, and responsive defaults.
- Stable reusable classes prefixed with `aira-`.
- Core application shell, header, navigation, buttons, forms, tables, alerts, panels, cards, and footer styles.

Shared CSS should avoid broad layout rules for generic elements such as every `div` or `section`. The stable public contract is the CSS custom properties and the `aira-` class names.

## Application CSS Scope

Application CSS should contain:

- Layout or component details unique to one application.
- Domain-specific visual treatments.
- Small extensions that consume shared variables such as `--aira-text`, `--aira-background`, and `--aira-primary-action`.

Application-specific classes should use an application prefix. The demo uses `demo-`.

## Java Rendering Components

`aira-web-components` provides plain Java rendering for the document head, shared stylesheet links, optional local stylesheets, skip link, header, identity, primary navigation, main container, and required institutional footer.

It does not provide a base servlet. Applications use composition:

```java
AiraPage page = AiraPage.builder()
    .applicationName("Example Application")
    .applicationVersion("1.0.0")
    .documentTitle("Example Application")
    .pageHeading("Dashboard")
    .contextPath(request.getContextPath())
    .addNavigationItem("Home", "/", true)
    .addLocalStylesheet("/css/application.css")
    .build();

page.writeStart(writer);
renderApplicationContent(writer);
page.writeEnd(writer);
```

Applications continue generating page-specific content directly from Java servlets.

See `docs/components-guide.md` for detailed component usage.

## Versioned Upgrades

Theme releases should be versioned and documented. Applications should upgrade intentionally by changing the Maven dependency version, reviewing release notes, testing pages at desktop and narrow widths, and deploying through their normal release process.
