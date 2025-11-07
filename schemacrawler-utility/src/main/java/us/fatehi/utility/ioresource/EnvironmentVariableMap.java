/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.ioresource;

import java.util.Map;

/** Intended to be implemented functionally with System::getenv. */
@FunctionalInterface
public interface EnvironmentVariableMap extends StringValueMap {

  /**
   * Gets the value of the specified environment variable.
   *
   * @param name the name of the environment variable
   * @return the string value of the variable, or null if the variable is not defined
   */
  @Override
  default String get(final String name) {
    final Map<String, String> env = getenv();
    if (env != null) {
      return env.get(name);
    }
    return null;
  }

  Map<String, String> getenv();

  @Override
  default Map<String, String> toMap() {
    return getenv();
  }
}
