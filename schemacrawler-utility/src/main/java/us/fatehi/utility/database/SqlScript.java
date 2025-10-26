/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.database;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class SqlScript implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(SqlScript.class.getName());

  private static final boolean debug =
      Boolean.parseBoolean(
          System.getProperty(SqlScript.class.getCanonicalName() + ".debug", "false"));

  public static void executeScriptFromResource(
      final String scriptResource, final Connection connection) {
    executeScriptFromResource(scriptResource, ";", connection);
  }

  public static void executeScriptFromResource(
      final String scriptResource, final String delimiter, final Connection connection) {

    requireNotBlank(scriptResource, "No script resource line provided");
    requireNonNull(connection, "No database connection provided");

    try (final Reader scriptReader =
        new ClasspathInputResource(scriptResource).openNewInputReader(UTF_8)) {
      final SqlScript sqlScript = new SqlScript(scriptReader, delimiter, connection);
      sqlScript.run();
    } catch (final Exception e) {
      throw new SQLRuntimeException("Could not read \"%s\"".formatted(scriptResource), e);
    }
  }

  private final Reader scriptReader;
  private final String delimiter;
  private final Connection connection;

  public SqlScript(final Reader scriptReader, final String delimiter, final Connection connection) {
    this.scriptReader = requireNonNull(scriptReader, "No script resource line provided");
    this.delimiter = requireNonNull(delimiter, "No delimiter provided");
    this.connection = requireNonNull(connection, "No database connection provided");
  }

  @Override
  public void run() {

    final boolean skip = "#".equals(delimiter);

    if (debug) {
      final String lineLogMessage =
          "%s %s"
              .formatted(scriptReader, skip ? "-- skip" : "-- execute, delimiting by " + delimiter);
      LOGGER.log(Level.INFO, lineLogMessage);
      System.out.println(lineLogMessage);
    }

    if (skip) {
      return;
    }

    String sql = null;
    try (final Statement statement = connection.createStatement()
    // NOTE: Do not close reader or connection, since we did not open them
    ) {
      final List<String> sqlList = readSql(new BufferedReader(scriptReader));
      for (final Iterator<String> iterator = sqlList.iterator(); iterator.hasNext(); ) {
        sql = iterator.next();
        try {
          if (Pattern.matches("\\s+", sql)) {
            continue;
          }
          if (debug) {
            LOGGER.log(Level.INFO, "\n" + sql);
          }

          executeSql(sql, statement);

          if (!connection.getAutoCommit()) {
            connection.commit();
          }

        } catch (final SQLWarning e) {
          final int errorCode = e.getErrorCode();
          if (errorCode == 5701 || errorCode == 5703 || errorCode == 1280) {
            // SQL Server information message
            continue;
          }
          final Throwable throwable = getCause(e);
          throw new SQLRuntimeException(throwable);
        }
      }
    } catch (final Exception e) {
      final Throwable throwable = getCause(e);
      System.err.println(throwable.getMessage());
      System.err.println(sql);
      LOGGER.log(Level.WARNING, throwable.getMessage(), throwable);
      throw new SQLRuntimeException(e);
    }
  }

  private void executeSql(final String sql, final Statement statement) throws SQLException {
    final boolean hasResults = statement.execute(sql);
    if (hasResults) {
      throw new SQLWarning("Results not expected from SQL%n%s%n".formatted(sql));
    }

    final SQLWarning warnings = statement.getWarnings();
    statement.clearWarnings();
    if (warnings != null) {
      LOGGER.log(Level.WARNING, warnings.getMessage(), warnings);
    }
  }

  private Throwable getCause(final Throwable e) {
    Throwable cause;
    Throwable result = e;

    while (null != (cause = result.getCause()) && result != cause) {
      result = cause;
    }
    return result;
  }

  private List<String> readSql(final BufferedReader lineReader) throws IOException {
    final List<String> list = new ArrayList<>();
    String line;
    StringBuilder sql = new StringBuilder();
    while ((line = lineReader.readLine()) != null) {
      final String trimmedLine = line.trim();
      final boolean isComment = trimmedLine.startsWith("--") || trimmedLine.startsWith("//");
      if (!isComment && trimmedLine.endsWith(delimiter)) {
        sql.append(line, 0, line.lastIndexOf(delimiter));
        list.add(sql.toString());
        sql = new StringBuilder();
      } else {
        sql.append(line);
        sql.append("\n");
      }
    }
    // Check if the last line is not delimited
    if (sql.length() > 0) {
      list.add(sql.toString());
    }

    return list;
  }
}
