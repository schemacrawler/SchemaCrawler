/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.jdbc.JDBCDataSource;

public final class DataSourceTestUtility {

  private DataSourceTestUtility() {
    // Prevent instantiation
  }

  public static DataSource newEmbeddedDatabase(final String script) {
    try {
      // Create data source
      final String randomDatabaseName = RandomStringUtils.secure().nextAlphabetic(7);
      final JDBCDataSource hsqlDataSource = new JDBCDataSource();
      hsqlDataSource.setDatabase("jdbc:hsqldb:mem:" + randomDatabaseName);
      // Read script
      final String sql = IOUtils.resourceToString(script, StandardCharsets.UTF_8);
      String[] statements = sql.split(";");
      // Create a QueryRunner to execute the SQL statements
      final QueryRunner runner = new QueryRunner(hsqlDataSource);
      for (String statement : statements) {
        statement = statement.trim();
        if (!statement.isEmpty()) {
          runner.update(statement);
        }
      }
      return hsqlDataSource;
    } catch (IOException | SQLException e) {
      return fail("Could not create a data source", e);
    }
  }
}
