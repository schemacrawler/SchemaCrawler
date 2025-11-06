/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.isBlank;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class PropertiesUtility {

  private static final Logger LOGGER = Logger.getLogger(PropertiesUtility.class.getName());

  /**
   * Returns the system property value as a string, even if the underlying value is not a String.
   * Returns null if the key is not set.
   */
  public static String get(final Properties properties, final String key) {
    if (properties == null || key == null) {
      return null;
    }
    try {
      final Object value = properties.get(key);
      return value != null ? value.toString() : null;
    } catch (final Exception e) {
      return "Error reading key: " + key + " = value class: " + e.getClass().getSimpleName();
    }
  }

  public static boolean getBooleanSystemConfigurationProperty(final String key) {
    return Boolean.parseBoolean(getSystemConfigurationProperty(key, Boolean.FALSE.toString()));
  }

  public static String getSystemConfigurationProperty(final String key) {
    return getSystemConfigurationProperty(key, "");
  }

  public static String getSystemConfigurationProperty(final String key, final String defaultValue) {
    final String systemPropertyValue = get(System.getProperties(), key);
    if (!isBlank(systemPropertyValue)) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Using value from system property <%s=%s>", key, systemPropertyValue));
      return systemPropertyValue;
    }

    final String envVariableValue = System.getenv(key);
    if (!isBlank(envVariableValue)) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat(
              "Using value from enivronmental variable <%s=%s>", key, envVariableValue));
      return envVariableValue;
    }

    return defaultValue;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}
