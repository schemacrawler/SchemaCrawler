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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class PropertiesUtility {

  private static final Logger LOGGER = Logger.getLogger(PropertiesUtility.class.getName());

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
   * Copies properties into a map.
   *
   * @param properties Properties to copy
   * @return Map of properties and values
   */
  public static Map<String, String> propertiesMap(final Properties properties) {
    final Map<String, String> propertiesMap = new HashMap<>();
    if (properties != null) {
      final Set<Entry<Object, Object>> entries = properties.entrySet();
      for (final Entry<Object, Object> entry : entries) {
        propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
      }
    }
    return propertiesMap;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}
