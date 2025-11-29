/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PooledConnectionUtility {

  private static class PooledConnectionInvocationHandler implements InvocationHandler {

    private final Connection connection;
    private final DatabaseConnectionSource databaseConnectionSource;
    private boolean isClosed;

    PooledConnectionInvocationHandler(
        final Connection connection, final DatabaseConnectionSource databaseConnectionSource) {
      requireNonNull(connection, "No database connnection provided");
      if (connection instanceof DatabaseConnectionSourceConnection) {
        try {
          this.connection = connection.unwrap(Connection.class);
        } catch (final SQLException e) {
          throw new UnsupportedOperationException("Could not unwrap proxy connection");
        }
      } else {
        this.connection = connection;
      }
      this.databaseConnectionSource =
          requireNonNull(databaseConnectionSource, "No database connection source provided");
      isClosed = false;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Exception {
      final String methodName = method.getName();
      if (!List.of("isClosed", "unwrap").contains(methodName) && isClosed) {
        throw new SQLException("Cannot call <%s> since connection is closed".formatted(method));
      }
      switch (methodName) {
        case "close":
          databaseConnectionSource.releaseConnection(connection);
          isClosed = true;
          return null;
        case "isClosed":
          return isClosed;
        case "isWrapperFor":
          final Class<?> clazz = (Class<?>) args[0];
          return clazz.isAssignableFrom(connection.getClass());
        case "unwrap":
          return connection;
        case "toString":
          return "Pooled connection <%s@%d> for <%s>"
              .formatted(proxy.getClass().getName(), proxy.hashCode(), connection);
        default:
          try {
            return method.invoke(connection, args);
          } catch (IllegalAccessException
              | IllegalArgumentException
              | InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof Exception exception) {
              throw exception;
            }
            throw new SQLException("Could not delegate method <%s>".formatted(method), e);
          }
      }
    }
  }

  public static Connection newPooledConnection(
      final Connection connection, final DatabaseConnectionSource databaseConnectionSource) {

    return (Connection)
        newProxyInstance(
            PooledConnectionUtility.class.getClassLoader(),
            new Class[] {Connection.class, DatabaseConnectionSourceConnection.class},
            new PooledConnectionInvocationHandler(connection, databaseConnectionSource));
  }

  private PooledConnectionUtility() {
    // Prevent instantiation
  }
}
