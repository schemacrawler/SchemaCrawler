/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.offline.jdbc;

import static java.lang.reflect.Proxy.newProxyInstance;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class OfflineConnectionUtility {

  private static class OfflineConnectionInvocationHandler implements InvocationHandler {

    private final Path offlineDatabasePath;
    private boolean isClosed;

    public OfflineConnectionInvocationHandler(final Path offlineDatabasePath) {
      this.offlineDatabasePath = offlineDatabasePath;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws SQLFeatureNotSupportedException {
      final String methodName = method.getName();
      switch (methodName) {
        case "close":
          isClosed = true;
          return null;
        case "setAutoCommit":
          // Do nothing
          return null;
        case "getOfflineDatabasePath":
          return offlineDatabasePath;
        case "isWrapperFor":
          if (args[0] == null) {
            return false;
          }
          final Class<?> clazz = (Class<?>) args[0];
          return clazz.isAssignableFrom(Connection.class);
        case "unwrap":
          return proxy;
        case "isValid":
        case "isClosed":
          return isClosed;
        case "getTypeMap":
          return Collections.emptyMap();
        case "hashCode":
          return offlineDatabasePath.hashCode();
        case "toString":
          return String.format(
              "schemacrawler.tools.offline.jdbc.OfflineConnection@%s",
              offlineDatabasePath.hashCode());
        case "equals":
          if (args != null && args.length > 0 && args[0] instanceof OfflineConnection) {
            final OfflineConnection otherOfflineConnection = (OfflineConnection) args[0];
            return otherOfflineConnection.hashCode() == offlineDatabasePath.hashCode();
          }
        // Fall through
        default:
          throw new SQLFeatureNotSupportedException(
              String.format(
                  "Offline catalog snapshot connection does not support method <%s>", methodName),
              "HYC00");
      }
    }
  }

  public static DatabaseConnectionSource newOfflineDatabaseConnectionSource(
      final Path offlineDatabasePath) {
    requireNonNull(offlineDatabasePath, "No offline catalog snapshot path provided");

    final Path absoluteOfflineDatabasePath = offlineDatabasePath.toAbsolutePath();
    final OfflineConnection offlineConnection =
        (OfflineConnection)
            newProxyInstance(
                OfflineConnectionUtility.class.getClassLoader(),
                new Class[] {OfflineConnection.class},
                new OfflineConnectionInvocationHandler(absoluteOfflineDatabasePath));
    return new OfflineDatabaseConnectionSource(offlineConnection);
  }

  private OfflineConnectionUtility() {
    // Prevent instantiation
  }
}
