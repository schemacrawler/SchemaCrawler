/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.ioresource;

import java.util.Map;

/**
 * Interface for accessing System properties or environment variables. This allows for mocking
 * environment variables during testing.
 */
public interface StringValueMap {

  /**
   * Gets the value of the specified key.
   *
   * @param name the name of the key
   * @return String value of the key, or null if the key is not defined
   */
  String get(String key);

  default boolean containsKey(String key) {
    final String value = get(key);
    return value != null && !value.isBlank();
  }

  Map<String, String> toMap();
}
