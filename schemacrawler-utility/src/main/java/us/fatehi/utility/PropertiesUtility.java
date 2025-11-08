/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.ioresource.PropertiesConfig.systemProperties;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.ioresource.EnvironmentVariableConfig;
import us.fatehi.utility.ioresource.StringValueConfig;
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

    final StringValueConfig systemProperties = systemProperties();
    final StringValueConfig envProperties = (EnvironmentVariableConfig) System::getenv;

    String value = null;

    if (systemProperties.containsKey(key)) {
      value = systemProperties.getStringValue(key, defaultValue);
      LOGGER.log(Level.CONFIG, new StringFormat("Using system property for <%s>", key));
    } else if (envProperties.containsKey(key)) {
      value = envProperties.getStringValue(key, defaultValue);
      LOGGER.log(Level.CONFIG, new StringFormat("Using environmental variable for <%s>", key));
    } else {
      value = defaultValue;
    }

    if (value == null) {
      value = "";
    }
    value = value.strip();
    LOGGER.log(Level.CONFIG, new StringFormat("Configuration value <%s>=<%s>", key, value));

    return value;
  }

  private PropertiesUtility() {
    // Prevent instantiation
  }
}
