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
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

public final class OracleTestUtility {

  @SuppressWarnings("resource")
  public static JdbcDatabaseContainer<?> newOracleContainer() {
    return newOracleContainer("23.9-slim-faststart");
  }

  @SuppressWarnings("resource")
  private static OracleContainer newOracleContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse("gvenzl/oracle-free");
    return new OracleContainer(imageName.withTag(version))
        .withStartupTimeout(Duration.ofMinutes(5));
  }

  private OracleTestUtility() {
    // Prevent instantiation
  }
}
