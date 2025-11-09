/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.readconfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class SystemPropertiesConfig implements ReadConfig {

  private static final Logger LOGGER = Logger.getLogger(SystemPropertiesConfig.class.getName());

  @Override
  public boolean containsKey(final String propertyName) {
    final boolean containsKey = getSystemProperty(propertyName) != null;
    return containsKey;
  }

  /**
   * Returns the property value as a string, even if the underlying value is not a String. Returns
   * null if the key is not set.
   */
  @Override
  public String getStringValue(final String propertyName, final String defaultValue) {
    if (propertyName == null) {
      return defaultValue;
    }
    try {
      final Object value = getSystemProperty(propertyName);
      return value != null ? value.toString() : defaultValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.FINE,
          "Error reading key: " + propertyName + " = value class: " + e.getClass().getSimpleName());
      return defaultValue;
    }
  }

  private Object getSystemProperty(final String propertyName) {
    return System.getProperties().get(propertyName);
  }
}
