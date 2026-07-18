package org.immregistries.aira.web;

final class HtmlEscaper {

  private HtmlEscaper() {
  }

  static String escapeText(String value) {
    return escape(value);
  }

  static String escapeAttribute(String value) {
    return escape(value);
  }

  private static String escape(String value) {
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

