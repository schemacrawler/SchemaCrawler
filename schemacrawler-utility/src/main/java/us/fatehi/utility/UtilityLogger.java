/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.join;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  public void logSQLWarnings(final ResultSet resultSet) {
    if ((resultSet == null) || !logger.isLoggable(Level.INFO)) {
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
    if ((statement == null) || !logger.isLoggable(Level.INFO)) {
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

    final SortedMap<String, String> systemProperties = new TreeMap<>();
    for (final Entry<Object, Object> propertyValue : System.getProperties().entrySet()) {
      final String name = (String) propertyValue.getKey();
      if ((name.startsWith("java.") || name.startsWith("os.")) && !name.endsWith(".path")) {
        systemProperties.put(name, (String) propertyValue.getValue());
      }
    }

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
