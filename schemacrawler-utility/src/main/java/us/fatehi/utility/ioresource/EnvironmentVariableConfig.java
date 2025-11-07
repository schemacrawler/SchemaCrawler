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
 * Intended to be implemented functionally like this: <br>
 * {@code StringValueConfig env = (EnvironmentVariableConfig) System::getenv;}
 */
@FunctionalInterface
public interface EnvironmentVariableConfig extends StringValueConfig {

  /**
   * Gets the value of the specified environment variable.
   *
   * @param name the name of the environment variable
   * @return the string value of the variable, or null if the variable is not defined
   */
  @Override
  default String getStringValue(final String propertyName, final String defaultValue) {
    final Map<String, String> env = getenv();
    if (env == null) {
      return defaultValue;
    }
    return env.getOrDefault(propertyName, defaultValue);
  }

  Map<String, String> getenv();

  @Override
  default Map<String, String> toStringValueMap() {
    return getenv();
  }
}
