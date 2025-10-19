/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static us.fatehi.test.utility.TestUtility.failTestSetup;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import schemacrawler.testdb.TestSchemaCreator;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public abstract class BaseAdditionalDatabaseTest {

  private DataSource dataSource;

  protected void closeDataSource() {
    try {
      if (dataSource instanceof final Closeable closeable) {
        closeable.close();
      }
    } catch (final Exception e) {
      failTestSetup("Could not close data source", e);
    }
  }

  protected void createDatabase(final String scriptsResource) {
    try (final Connection connection = getConnection()) {
      final TestSchemaCreator schemaCreator =
          new TestSchemaCreator(connection, scriptsResource, false);
      schemaCreator.run();
    } catch (final SQLException e) {
      failTestSetup("Could not create database", e);
    }
  }

  protected void createDataSource(
      final String connectionUrl, final String user, final String password) {
    createDataSource(connectionUrl, user, password, null);
  }

  protected void createDataSource(
      final String connectionUrl,
      final String user,
      final String password,
      final String connectionProperties) {

    dataSource = createDataSourceObject(connectionUrl, user, password, connectionProperties);
  }

  protected DataSource createDataSourceObject(
      final String connectionUrl,
      final String user,
      final String password,
      final String connectionProperties) {

    final HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(connectionUrl);
    ds.setUsername(user);
    ds.setPassword(password);

    if (connectionProperties != null && !connectionProperties.isBlank()) {
      for (final String entry : connectionProperties.split(";")) {
        final String[] kv = entry.split("=", 2);
        if (kv.length == 2) {
          ds.addDataSourceProperty(kv[0].trim(), kv[1].trim());
        }
      }
    }

    return ds;
  }

  protected final Connection getConnection() {
    try {
      return dataSource.getConnection();
    } catch (final SQLException e) {
      fail("Could not get database connection", e);
      return null; // Appeasing the compiler - this line will never be executed.
    }
  }

  protected final DatabaseConnectionSource getDataSource() {
    return DatabaseConnectionSources.fromDataSource(dataSource);
  }

  protected void runScript(final String databaseSqlResource) throws Exception {
    try (final Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource(databaseSqlResource, connection);
    }
  }
}
