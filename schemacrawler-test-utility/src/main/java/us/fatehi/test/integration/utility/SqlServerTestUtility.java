/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.integration.utility;

import java.time.Duration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

public final class SqlServerTestUtility {

  public static JdbcDatabaseContainer<?> newSqlServerContainer() {
    return newSqlServerContainer("2022-CU17-ubuntu-22.04");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newSqlServerContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse("mcr.microsoft.com/mssql/server");
    return new MSSQLServerContainer(imageName.withTag(version))
        .withUrlParam("encrypt", "false")
        .withPassword("Schem#Crawl3r")
        .acceptLicense()
        .withEnv("MSSQL_PID", "Express")
        .withStartupTimeout(Duration.ofMinutes(5));
  }

  private SqlServerTestUtility() {
    // Prevent instantiation
  }
}
