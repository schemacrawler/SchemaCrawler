/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class PostgreSQLTestUtility {

  public static JdbcDatabaseContainer<?> newPostgreSQL11Container() {
    return newPostgreSQLContainer("11.16-alpine");
  }

  public static JdbcDatabaseContainer<?> newPostgreSQLContainer() {
    return newPostgreSQLContainer("16.4-bookworm");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newPostgreSQLContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse(PostgreSQLContainer.IMAGE);
    return new PostgreSQLContainer(imageName.withTag(version));
  }

  private PostgreSQLTestUtility() {
    // Prevent instantiation
  }
}
