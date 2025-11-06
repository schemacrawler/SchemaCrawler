/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PropertiesMap implements StringValueMap {

  private static final Logger LOGGER = Logger.getLogger(PropertiesMap.class.getName());

  public static PropertiesMap empty() {
    return new PropertiesMap(new Properties());
  }

  public static PropertiesMap fromProperties(final Properties properties) {
    return new PropertiesMap(properties);
  }

  public static PropertiesMap systemProperties() {
    return new PropertiesMap(System.getProperties());
  }

  private final Properties properties;

  private PropertiesMap(final Properties properties) {
    this.properties = requireNonNull(properties, "No properties provided");
  }

  /**
   * Returns the property value as a string, even if the underlying value is not a String. Returns
   * null if the key is not set.
   */
  @Override
  public String get(final String key) {
    if (key == null) {
      return null;
    }
    try {
      final Object value = properties.get(key);
      return value != null ? value.toString() : null;
    } catch (final Exception e) {
      LOGGER.log(
          Level.FINE,
          "Error reading key: " + key + " = value class: " + e.getClass().getSimpleName());
      return null;
    }
  }

  /**
   * Copies properties into a map. Keys are expected to be strings. Null values are converted to
   * empty strings.
   *
   * @param properties Properties to copy
   * @return Map of property names to values
   */
  @Override
  public Map<String, String> toMap() {
    final Map<String, String> propertiesMap = new HashMap<>();
    for (final Object keyObject : properties.keySet()) {
      // Filter keys that are not strings
      // See a similar issue
      // https://github.com/spring-projects/spring-framework/issues/32742
      if (keyObject instanceof final String key) {
        propertiesMap.put(key, get(key));
      }
    }
    return propertiesMap;
  }
}
