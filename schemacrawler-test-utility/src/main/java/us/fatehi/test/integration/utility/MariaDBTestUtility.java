/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.integration.utility;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

public final class MariaDBTestUtility {

  public static JdbcDatabaseContainer<?> newMariaDBContainer() {
    return newMariaDBContainer("11.6.2-noble");
  }

  @SuppressWarnings("resource")
  public static JdbcDatabaseContainer<?> newMariaDBContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse(MariaDBContainer.NAME);
    return new MariaDBContainer(imageName.withTag(version))
        .withEnv("MARIADB_DATABASE", "books")
        .withEnv("MARIADB_ROOT_USER", "root")
        .withEnv("MARIADB_ROOT_PASSWORD", "schemacrawler")
        .withEnv("MARIADB_USER", "schemacrawler")
        .withEnv("MARIADB_PASSWORD", "schemacrawler")
        .withDatabaseName("books")
        .withUsername("root")
        .withPassword("schemacrawler");
  }

  private MariaDBTestUtility() {
    // Prevent instantiation
  }
}
