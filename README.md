# AIRA Web

Reusable AIRA web application resources for Java Servlet applications that generate HTML directly from Java. This repository provides shared CSS, static web resources, and plain Java renderers for the common AIRA application shell.

Applications should consume a versioned Maven dependency. They should not load an unversioned stylesheet directly from a central production URL.

## Modules

- `aira-web-theme` - JAR containing shared static web resources under `META-INF/resources/aira/`.
- `aira-web-components` - plain Java JAR for rendering the document head, stylesheet links, header, navigation, main container, and required footer.
- `aira-web-demo` - WAR demonstrating a servlet-only application that consumes `aira-web-components` and generates page-specific content itself.
- `docs/` - branding standards, adoption guidance, and component usage guidance.
- `temp/` - reference material used to seed this repository. The files are intentionally left unchanged during this stage.

## Build

```bash
mvn clean verify
```

The project is configured for Java 17. Plugin and dependency versions are managed from the parent POM.

## Run the Demo

Run the demo locally with Jetty:

```bash
mvn -pl aira-web-demo -am package jetty:run
```

Then open:

```text
http://localhost:8080/aira-web-demo/
```

If port 8080 is already in use, add a port override:

```bash
mvn -pl aira-web-demo -am package jetty:run -Djetty.http.port=18080
```

In PowerShell, quote the property:

```powershell
mvn -pl aira-web-demo -am package jetty:run "-Djetty.http.port=18080"
```

The demo loads the shared stylesheet first:

```html
<link rel="stylesheet" href="{contextPath}/aira/css/aira.css">
```

Then it loads application CSS:

```html
<link rel="stylesheet" href="{contextPath}/css/application.css">
```

This order lets applications use the shared theme as the baseline and add small local extensions without duplicating the shared theme.

## Current Status

This project currently includes:

- A multi-module Maven build.
- A resource JAR that exposes `META-INF/resources/aira/css/aira.css`.
- A reusable Java component JAR for the AIRA application shell.
- A servlet-only demo WAR.
- JUnit coverage for the reusable renderer behavior.
- A minimal CI workflow for Java 17.

Applications may consume `aira-web-theme` alone when they only need CSS and assets. Applications that also want the standardized HTML application shell should consume `aira-web-components`, which brings in the theme transitively.

## Known Missing Items

- Approved AIRA logo assets are not present in this repository. The theme JAR includes `images`, `icons`, and `fonts` directories ready for approved assets, but no logo has been fabricated or approximated.
- Local Open Sans font packaging is not implemented yet. The CSS uses `"Open Sans", Arial, sans-serif` without a runtime network dependency.
- Shared renderers currently cover the page shell only. Buttons, forms, tables, dialogs, and other component renderers remain application-generated for now.
