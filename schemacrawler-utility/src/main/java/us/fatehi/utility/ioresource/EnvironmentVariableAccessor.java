/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.ioresource;

/**
 * Interface for accessing environment variables. This allows for mocking environment variables
 * during testing.
 */
@FunctionalInterface
public interface EnvironmentVariableAccessor {

  /**
   * Gets the value of the specified environment variable.
   *
   * @param name the name of the environment variable
   * @return the string value of the variable, or null if the variable is not defined
   */
  String getenv(String name);
}
