/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.join;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class UtilityLogger {

  private final Logger logger;

  public UtilityLogger(final Logger logger) {
    this.logger = requireNonNull(logger, "No logger provided");
  }

  public void logFatalStackTrace(final Throwable t) {
    if (t == null || !logger.isLoggable(Level.SEVERE)) {
      return;
    }

    logger.log(Level.SEVERE, t.getMessage(), t);
  }

  public void logPossiblyUnsupportedSQLFeature(
      final Supplier<String> message, final SQLException e) {
    // HYC00 = Optional feature not implemented
    // HY000 = General error
    // (HY000 is thrown by the Teradata JDBC driver for unsupported
    // functions)
    if ("HYC00".equalsIgnoreCase(e.getSQLState())
        || "HY000".equalsIgnoreCase(e.getSQLState())
        || "0A000".equalsIgnoreCase(e.getSQLState())
        || e instanceof SQLFeatureNotSupportedException) {
      logSQLFeatureNotSupported(message, e);
    } else {
      logger.log(Level.WARNING, e, message);
    }
  }

  public void logSafeArguments(final String[] args) {
    if (args == null || !logger.isLoggable(Level.INFO)) {
      return;
    }

    final String passwordRedacted = "<password provided>";
    final StringJoiner argsList = new StringJoiner(System.lineSeparator());
    for (final Iterator<String> iterator = Arrays.asList(args).iterator(); iterator.hasNext(); ) {
      final String arg = iterator.next();
      if (arg == null) {
        continue;
      }
      if (arg.matches("--password.*=.*")) {
        argsList.add(passwordRedacted);
      } else if (arg.startsWith("--password")) {
        argsList.add(passwordRedacted);
        if (iterator.hasNext()) {
          // Skip over the password
          iterator.next();
        }
      } else {
        argsList.add(arg);
      }
    }

    logger.log(Level.INFO, "Command line: %n%s".formatted(argsList.toString()));
  }

  public void logSQLFeatureNotSupported(final Supplier<String> message, final Throwable e) {
    logger.log(Level.WARNING, message);
    logger.log(Level.FINE, e, message);
  }

  public void logSQLWarnings(final ResultSet resultSet) {
    if (resultSet == null || !logger.isLoggable(Level.INFO)) {
      return;
    }

    try {
      logSQLWarnings(resultSet.getWarnings());
      resultSet.clearWarnings();
    } catch (final SQLException e) {
      // NOTE: Do not show exception while logging warnings
      logger.log(Level.FINE, "Could not log SQL warnings for result set");
    }
  }

  public void logSQLWarnings(final Statement statement) {
    if (statement == null || !logger.isLoggable(Level.INFO)) {
      return;
    }

    try {
      logSQLWarnings(statement.getWarnings());
      statement.clearWarnings();
    } catch (final SQLException e) {
      // NOTE: Do not show exception while logging warnings
      logger.log(Level.FINE, "Could not log SQL warnings for statement");
    }
  }

  public void logSystemClasspath() {
    if (!logger.isLoggable(Level.CONFIG)) {
      return;
    }

    logger.log(
        Level.CONFIG,
        "Classpath: %n%s".formatted(printPath(System.getProperty("java.class.path"))));
    logger.log(
        Level.CONFIG,
        "LD_LIBRARY_PATH: %n%s".formatted(printPath(System.getenv("LD_LIBRARY_PATH"))));
  }

  public void logSystemProperties() {
    if (!logger.isLoggable(Level.CONFIG)) {
      return;
    }

    final Map<String, Object> systemProperties =
        PropertiesUtility.systemProperties().entrySet().stream()
            .filter(
                entry -> {
                  final String key = entry.getKey();
                  return (key.startsWith("java.") || key.startsWith("os."))
                      && !key.endsWith(".path");
                })
            .collect(
                Collectors.toMap(Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));

    logger.log(
        Level.CONFIG,
        "System properties: %n%s".formatted(join(systemProperties, System.lineSeparator())));
  }

  private void logSQLWarnings(final SQLWarning sqlWarning) {
    SQLWarning currentSqlWarning = sqlWarning;
    while (currentSqlWarning != null) {
      final String message =
          "%s%nError code: %d, SQL state: %s"
              .formatted(
                  currentSqlWarning.getMessage(),
                  currentSqlWarning.getErrorCode(),
                  currentSqlWarning.getSQLState());
      logger.log(Level.INFO, message, currentSqlWarning);
      currentSqlWarning = currentSqlWarning.getNextWarning();
    }
  }

  private String printPath(final String path) {
    if (path == null) {
      return "";
    }
    return String.join(System.lineSeparator(), path.split(File.pathSeparator));
  }
}
