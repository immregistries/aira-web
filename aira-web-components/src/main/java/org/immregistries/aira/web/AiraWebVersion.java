package org.immregistries.aira.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The AIRA Web release version, read from a properties file stamped into the components jar at build
 * time. Used to version the shared stylesheet URL so browsers fetch a fresh copy after each release.
 */
public final class AiraWebVersion {

  private static final String RESOURCE = "aira-web-version.properties";
  private static final String FALLBACK = "dev";
  private static final String VERSION = load();

  private AiraWebVersion() {
  }

  /**
   * Returns the AIRA Web release version, or {@code "dev"} when the build-time value is unavailable
   * (for example, when running from unfiltered sources in an IDE).
   *
   * @return release version
   */
  public static String get() {
    return VERSION;
  }

  private static String load() {
    try (InputStream in = AiraWebVersion.class.getResourceAsStream(RESOURCE)) {
      if (in != null) {
        Properties properties = new Properties();
        properties.load(in);
        String value = properties.getProperty("version");
        if (value != null && !value.isBlank() && !value.contains("${")) {
          return value.trim();
        }
      }
    } catch (IOException ex) {
      // Fall through to the fallback version.
    }
    return FALLBACK;
  }
}
