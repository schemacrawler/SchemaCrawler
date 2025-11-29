/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.integration.utility;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
        .withStartupTimeout(Duration.ofMinutes(10));
  }

  public static Map<String, String> urlx() {
    final Map<String, String> urlx = new HashMap<>();
    urlx.put("restrictGetTables", "true");
    urlx.put("useFetchSizeWithLongColumn", "true");
    return urlx;
  }

  private OracleTestUtility() {
    // Prevent instantiation
  }
}
