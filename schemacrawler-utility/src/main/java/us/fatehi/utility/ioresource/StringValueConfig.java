/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.ioresource;

/**
 * Interface for accessing System properties or environment variables. This allows for mocking
 * environment variables during testing.
 */
public interface StringValueConfig {

  /**
   * Gets the value of the specified key.
   *
   * @param name the name of the key
   * @return String value of the key, or null if the key is not defined
   */
  String getStringValue(String propertyName, String defaultValue);

  default boolean containsKey(final String key) {
    final String value = getStringValue(key, null);
    return value != null;
  }
}
