/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test.utility;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public final class DB2TestUtility {

  public static JdbcDatabaseContainer<?> newDB2Container() {
    return newDB2Container("11.5.9.0");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newDB2Container(final String version) {
    final DockerImageName imageName = DockerImageName.parse("icr.io/db2_community/db2");

    final String DBNAME = "schcrwlr";
    final String DB2INST1 = "books";
    final String DB2INST1_PASSWORD = "SchemaCrawler";

    return new Db2Container(imageName.withTag(version))
        .acceptLicense()
        // DBNAME
        .withDatabaseName(DBNAME)
        .withEnv("DBNAME", DBNAME)
        // DB2INSTANCE
        .withUsername(DB2INST1)
        .withEnv("DB2INSTANCE", DB2INST1)
        // DB2INST1_PASSWORD
        .withPassword(DB2INST1_PASSWORD)
        .withEnv("DB2INST1_PASSWORD", DB2INST1_PASSWORD)
        // Other settings
        .withEnv("TO_CREATE_SAMPLEDB", "false")
        .withEnv("REPODB", "false");
  }

  private DB2TestUtility() {
    // Prevent instantiation
  }
}
