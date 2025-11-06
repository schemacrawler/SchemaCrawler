/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.ioresource.PropertiesMap.systemProperties;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.ioresource.EnvironmentVariableMap;
import us.fatehi.utility.ioresource.StringValueMap;
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

    final StringValueMap systemProperties = systemProperties();
    final StringValueMap envProperties = (EnvironmentVariableMap) System::getenv;

    String value = null;

    if (systemProperties.contains(key)) {
      value = systemProperties.get(key);
      LOGGER.log(
          Level.CONFIG, new StringFormat("Using value from system property <%s=%s>", key, value));
    } else if (envProperties.contains(key)) {
      value = envProperties.get(key);
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Using value from enivronmental variable <%s=%s>", key, value));
    } else {
      value = defaultValue;
    }

    if (value == null || value.isBlank()) {
      value = "";
    }

    return value;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}
