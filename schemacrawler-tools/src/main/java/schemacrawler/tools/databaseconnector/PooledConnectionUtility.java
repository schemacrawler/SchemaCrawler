/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.databaseconnector;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

public class PooledConnectionUtility {

  private static class PooledConnectionInvocationHandler implements InvocationHandler {

    private final Connection connection;
    private final DatabaseConnectionSource databaseConnectionSource;

    PooledConnectionInvocationHandler(
        final Connection connection, final DatabaseConnectionSource databaseConnectionSource) {
      this.connection = requireNonNull(connection, "No database connnection provided");
      this.databaseConnectionSource =
          requireNonNull(databaseConnectionSource, "No database connection source provided");
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Exception {
      final String methodName = method.getName();
      switch (methodName) {
        case "close":
          databaseConnectionSource.releaseConnection(connection);
          return null;
        case "isWrapperFor":
          final Class<?> clazz = (Class<?>) args[0];
          return clazz.isAssignableFrom(connection.getClass());
        case "unwrap":
          return connection;
        case "toString":
          return String.format("Pooled connection <%s> - <%s>", connection.getClass(), connection);
        default:
          try {
            return method.invoke(connection, args);
          } catch (IllegalAccessException
              | IllegalArgumentException
              | InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof Exception) {
              throw (Exception) cause;
            }
            throw new ExecutionRuntimeException(
                String.format("Could not delegate method <%s>", method), e);
          }
      }
    }
  }

  public static Connection newPooledConnection(
      final Connection connection, final DatabaseConnectionSource databaseConnectionSource) {

    return (Connection)
        newProxyInstance(
            PooledConnectionUtility.class.getClassLoader(),
            new Class[] {Connection.class},
            new PooledConnectionInvocationHandler(connection, databaseConnectionSource));
  }

  private PooledConnectionUtility() {
    // Prevent instantiation
  }
}
