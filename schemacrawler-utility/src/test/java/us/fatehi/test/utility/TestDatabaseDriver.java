/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static java.lang.reflect.Proxy.newProxyInstance;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class TestDatabaseDriver implements Driver {

  private static final String JDBC_URL_PREFIX = "jdbc:test-db:";

  static {
    try {
      DriverManager.registerDriver(new TestDatabaseDriver());
    } catch (final SQLException e) {
      e.printStackTrace();
    }
  }

  private static TestConnection newConnection(final String url, final Properties info) {
    return (TestConnection)
        newProxyInstance(
            TestDatabaseDriver.class.getClassLoader(),
            new Class[] {TestConnection.class},
            (proxy, method, args) -> {
              final String methodName = method.getName();
              switch (methodName) {
                case "close":
                case "setAutoCommit":
                  // Do nothing
                  return null;
                case "isWrapperFor":
                  return false;
                case "isValid":
                  return true;
                case "getDatabaseProductName":
                case "getDriverName":
                case "toString":
                  return "TestDatabaseDriver";
                case "getDatabaseProductVersion":
                case "getDriverVersion":
                  return "0.0";
                case "getConnectionProperties":
                  return info;
                case "getUrl":
                  return url;
                default:
                  throw new SQLFeatureNotSupportedException(methodName);
              }
            });
  }

  public TestDatabaseDriver() {
    forceInstantiationFailureIfConfigured();
  }

  @Override
  public boolean acceptsURL(final String url) {
    return !isBlank(url) && url.startsWith(JDBC_URL_PREFIX);
  }

  @Override
  public Connection connect(final String url, final Properties info) {
    if (acceptsURL(url)) {
      return newConnection(url, info);
    }
    return null;
  }

  @Override
  public int getMajorVersion() {
    return 0;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(final String url, final java.util.Properties info)
      throws SQLException {
    return new DriverPropertyInfo[] {new DriverPropertyInfo("key", "")};
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  private void forceInstantiationFailureIfConfigured() {
    final String propertyValue =
        System.getProperty(this.getClass().getName() + ".force-instantiation-failure");
    if (propertyValue != null) {
      throw new RuntimeException("Forced instantiation error");
    }
  }
}
