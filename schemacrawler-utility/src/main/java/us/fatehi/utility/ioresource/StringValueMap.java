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
 * Interface for accessing environment variables. This allows for mocking environment variables
 * during testing.
 */
public interface StringValueMap {

  /**
   * Gets the value of the specified variable.
   *
   * @param name the name of the variable
   * @return the string value of the variable, or null if the variable is not defined
   */
  String get(String name);

  default boolean containsKey(String name) {
    final String value = get(name);
    return value != null && !value.isBlank();
  }

  Map<String, String> toMap();
}
