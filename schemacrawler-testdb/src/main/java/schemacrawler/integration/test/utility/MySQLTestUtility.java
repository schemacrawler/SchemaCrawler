/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class MySQLTestUtility {

  public static JdbcDatabaseContainer<?> newMySQLContainer() {
    return newMySQLContainer("9.1.0");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newMySQLContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse(MySQLContainer.NAME);
    return new MySQLContainer<>(imageName.withTag(version))
        .withCommand(
            "mysqld",
            // "--skip-ssl", (Needed for MySQL 8)
            "--lower_case_table_names=1",
            "--log_bin_trust_function_creators=1");
  }

  private MySQLTestUtility() {
    // Prevent instantiation
  }
}
