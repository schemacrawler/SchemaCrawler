/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.readconfig;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

public final class SystemConfig implements ReadConfig {

  private static final Logger LOGGER = Logger.getLogger(SystemConfig.class.getName());

  private final ReadConfig env = (EnvironmentVariableConfig) System::getenv;
  private final ReadConfig sysProp = new SystemPropertiesConfig();

  @Override
  public boolean containsKey(final String key) {
    final boolean containsKey = sysProp.containsKey(key) || env.containsKey(key);
    return containsKey;
  }

  /**
   * Returns the property value as a string, even if the underlying value is not a String. Returns
   * null if the key is not set.
   */
  @Override
  public String getStringValue(final String key, final String defaultValue) {

    String value = null;

    if (sysProp.containsKey(key)) {
      value = sysProp.getStringValue(key, defaultValue);
      LOGGER.log(Level.CONFIG, new StringFormat("Using system property for <%s>", key));
    } else if (env.containsKey(key)) {
      value = env.getStringValue(key, defaultValue);
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
}
