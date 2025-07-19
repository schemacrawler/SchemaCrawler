/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import java.time.Duration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

public final class SqlServerTestUtility {

  public static JdbcDatabaseContainer<?> newSqlServerContainer() {
    return newSqlServerContainer("2022-CU17-ubuntu-22.04");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newSqlServerContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse("mcr.microsoft.com/mssql/server");
    return new MSSQLServerContainer<>(imageName.withTag(version))
        .withUrlParam("encrypt", "false")
        .withPassword("$ch3maCr@wl3r")
        .acceptLicense()
        .withEnv("MSSQL_PID", "Express")
        .withStartupTimeout(Duration.ofMinutes(5));
  }

  private SqlServerTestUtility() {
    // Prevent instantiation
  }
}
