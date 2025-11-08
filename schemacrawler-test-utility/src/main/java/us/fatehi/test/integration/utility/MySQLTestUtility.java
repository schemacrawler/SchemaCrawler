/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.integration.utility;

import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class MySQLTestUtility {

  public static JdbcDatabaseContainer<?> newMySQLContainer() {
    return newMySQLContainer("9.1.0");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newMySQLContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse(MySQLContainer.NAME);
    return new MySQLContainer(imageName.withTag(version))
        .withCommand(
            "mysqld",
            // "--skip-ssl", (Needed for MySQL 8)
            "--lower_case_table_names=1",
            "--log_bin_trust_function_creators=1");
  }

  public static Map<String, String> urlx() {
    // Use default connection properties from MySQLDatabaseConnector
    final Map<String, String> urlx = new HashMap<>();
    urlx.put("nullNamePatternMatchesAll", "true");
    urlx.put("getProceduresReturnsFunctions", "false");
    urlx.put("noAccessToProcedureBodies", "true");
    urlx.put("logger", "Jdk14Logger");
    urlx.put("dumpQueriesOnException", "true");
    urlx.put("dumpMetadataOnColumnNotFound", "true");
    urlx.put("maxQuerySizeToLog", "4096");
    urlx.put("disableMariaDbDriver", "true");
    urlx.put("useInformationSchema", "true");

    return urlx;
  }

  private MySQLTestUtility() {
    // Prevent instantiation
  }
}
