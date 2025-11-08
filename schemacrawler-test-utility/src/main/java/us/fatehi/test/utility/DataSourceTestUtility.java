/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static org.junit.jupiter.api.Assertions.fail;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.SQLExceptionOverride;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.jdbc.JDBCDataSource;

public final class DataSourceTestUtility {

  public static final int JDBC_DRIVER_COUNT = 15;

  public static DataSource createDataSource(
      final String connectionUrl,
      final String user,
      final String password,
      final Map<String, String> connectionProperties) {

    final class SQLExceptionHandler implements SQLExceptionOverride {

      @java.lang.Override
      public Override adjudicate(final SQLException e) {
        // HYC00 = Optional feature not implemented
        // HY000 = General error
        // (HY000 is thrown by the Teradata JDBC driver for unsupported
        // functions)
        if ("HYC00".equalsIgnoreCase(e.getSQLState())
            || "HY000".equalsIgnoreCase(e.getSQLState())
            || "0A000".equalsIgnoreCase(e.getSQLState())
            || e instanceof SQLFeatureNotSupportedException) {
          return Override.DO_NOT_EVICT;
        }
        return Override.CONTINUE_EVICT;
      }
    }

    final HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(connectionUrl);
    ds.setUsername(user);
    ds.setPassword(password);
    ds.setExceptionOverride(new SQLExceptionHandler());

    if (connectionProperties != null) {
      connectionProperties.entrySet().stream()
          .forEach(entry -> ds.addDataSourceProperty(entry.getKey(), entry.getValue()));
    }

    return ds;
  }

  public static DataSource newEmbeddedDatabase(final String script) {
    try {
      // Create data source
      final String randomDatabaseName = RandomStringUtils.secure().nextAlphabetic(7);
      final JDBCDataSource hsqlDataSource = new JDBCDataSource();
      hsqlDataSource.setDatabase("jdbc:hsqldb:mem:" + randomDatabaseName);
      // Read script
      final String sqlScript = IOUtils.resourceToString(script, StandardCharsets.UTF_8);
      final String[] statements = sqlScript.split(";");
      String sqlStatement = null;
      try (final Connection conn = hsqlDataSource.getConnection();
          final Statement stmt = conn.createStatement(); ) {
        for (final String sql : statements) {
          if (sql == null) {
            continue;
          }
          sqlStatement = sql.strip();
          if (!sqlStatement.isBlank()) {
            stmt.executeUpdate(sqlStatement);
          }
        }
      } catch (final SQLException e) {
        throw new SQLException("Could not execute SQL%n%s".formatted(sqlStatement), e);
      }
      return hsqlDataSource;
    } catch (final IOException | SQLException e) {
      return fail("Could not create a data source", e);
    }
  }

  private DataSourceTestUtility() {
    // Prevent instantiation
  }
}
