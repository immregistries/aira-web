# AIRA Theme CSS Sources

These files are the source for the public AIRA stylesheet.

The Maven build concatenates them, in numeric filename order, into:

`META-INF/resources/aira/css/aira.css`

Only the generated `aira.css` path is part of the public resource contract. Do not link directly to these source files from downstream applications.

Keep the numeric prefixes stable so cascade order remains explicit.
