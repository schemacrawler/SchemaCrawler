/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class PropertiesUtility {

  private static final Logger LOGGER = Logger.getLogger(PropertiesUtility.class.getName());

  public static boolean getBooleanSystemConfigurationProperty(final String key) {
    return Boolean.parseBoolean(getSystemConfigurationProperty(key, Boolean.FALSE.toString()));
  }

  public static String getSystemConfigurationProperty(final String key) {
    return getSystemConfigurationProperty(key, "");
  }

  public static String getSystemConfigurationProperty(final String key, final String defaultValue) {
    final String systemPropertyValue = System.getProperty(key);
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

  /**
   * Copies properties into a map. Keys are expected to be strings. Null values are converted to
   * empty strings.
   *
   * @param properties Properties to copy
   * @return Map of property names to values
   */
  public static Map<String, String> propertiesMap(final Properties properties) {
    final Map<String, String> propertiesMap = new HashMap<>();
    if (properties != null) {
      for (final Entry<Object, Object> entry : properties.entrySet()) {
        // Filter keys that are not strings
        // See a similar issue https://github.com/spring-projects/spring-framework/issues/32742
        if (entry.getKey() instanceof final String key) {
          Object value = entry.getValue();
          if (value == null) {
            value = "";
          }
          propertiesMap.put(key, value.toString());
        }
      }
    }
    return propertiesMap;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}
