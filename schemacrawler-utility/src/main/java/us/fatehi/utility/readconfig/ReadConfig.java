/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.readconfig;

/**
 * Interface for accessing properties from a map, without needing to make a copy of the underlying
 * data.
 */
public interface ReadConfig {

  boolean containsKey(final String key);

  default boolean getBooleanValue(final String propertyName) {
    return Boolean.parseBoolean(getStringValue(propertyName, Boolean.FALSE.toString()).strip());
  }

  default String getStringValue(final String propertyName) {
    return getStringValue(propertyName, "").strip();
  }

  /**
   * Gets the value of the specified key.
   *
   * @param name the name of the key
   * @return String value of the key, or null if the key is not defined
   */
  String getStringValue(String propertyName, String defaultValue);
}
