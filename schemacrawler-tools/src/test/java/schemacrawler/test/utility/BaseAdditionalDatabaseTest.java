/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.failTestSetup;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import schemacrawler.testdb.TestSchemaCreator;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public abstract class BaseAdditionalDatabaseTest {

  private DataSource dataSource;

  protected void closeDataSource() {
    try {
      if (dataSource instanceof Closeable closeable) {
        closeable.close();
      }
    } catch (final Exception e) {
      failTestSetup("Could not close data source", e);
    }
  }

  protected void createDatabase(final String scriptsResource) {
    try (final Connection connection = getConnection()) {
      final TestSchemaCreator schemaCreator = new TestSchemaCreator(connection, scriptsResource);
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

    final BasicDataSource ds = new BasicDataSource();
    ds.setUrl(connectionUrl);
    ds.setUsername(user);
    ds.setPassword(password);
    if (connectionProperties != null) {
      ds.setConnectionProperties(connectionProperties);
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
    final BasicDataSource basicDataSource = (BasicDataSource) dataSource;
    return DatabaseConnectionSources.fromDataSource(basicDataSource);
  }

  protected void runScript(final String databaseSqlResource) throws Exception {
    try (final Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource(databaseSqlResource, connection);
    }
  }
}
