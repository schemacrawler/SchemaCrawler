/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.testdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.util.Objects.requireNonNull;

public class TestSchemaCreator implements Runnable {

  private static void executeScriptLine(
      final String scriptResourceLine, final Connection connection) {

    requireNonNull(scriptResourceLine, "No script resource line provided");
    requireNonNull(connection, "No database connection provided");

    final String scriptResource;
    final String delimiter;

    final String[] split = scriptResourceLine.split(",");
    if (split.length == 1) {
      scriptResource = scriptResourceLine.trim();
      if (scriptResource.isEmpty()) {
        delimiter = "#";
      } else {
        delimiter = ";";
      }
    } else if (split.length == 2) {
      delimiter = split[0].trim();
      scriptResource = split[1].trim();
    } else {
      throw new RuntimeException("Too many fields in \"%s\"".formatted(scriptResourceLine));
    }

    final boolean skip = "#".equals(delimiter);
    if (skip) {
      return;
    }

    try (final BufferedReader scriptReader = newClasspathReader(scriptResource)) {
      executeSqlScript(connection, scriptReader, delimiter);
    } catch (final IOException | SQLException e) {
      throw new RuntimeException("Could not read \"%s\"".formatted(scriptResource), e);
    }
  }

  private static void executeSqlScript(
      final Connection connection, final BufferedReader reader, final String delimiter)
      throws IOException, SQLException {
    final StringBuilder scriptBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      scriptBuilder.append(line).append("\n");
    }

    final String fixedDelimiter;
    if ("@".equals(delimiter)) {
      fixedDelimiter = "^@";
    } else {
      fixedDelimiter = delimiter;
    }

    final String[] statements = scriptBuilder.toString().split(fixedDelimiter);
    for (final String stmt : statements) {
      final String sql = stmt.strip();
      if (!sql.isBlank()) {
        try (final Statement statement = connection.createStatement()) {
          statement.execute(sql);
        }
      }
    }
  }

  private static BufferedReader newClasspathReader(final String classpathResource) {
    if (classpathResource == null || classpathResource.isBlank()) {
      throw new IllegalArgumentException("Classpath resource not provided");
    }

    final String resource;
    if (classpathResource.startsWith("/")) {
      resource = classpathResource.substring(1);
    } else {
      resource = classpathResource;
    }

    final InputStream inputStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    final BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    return reader;
  }

  private final Connection connection;
  private final String scriptsResource;

  public TestSchemaCreator(
      final Connection connection, final String scriptsResource, final boolean debug) {
    this.connection = requireNonNull(connection, "No database connection provided");
    this.scriptsResource = requireNonNull(scriptsResource, "No script resource provided");
  }

  @Override
  public void run() {
    try (final BufferedReader reader = newClasspathReader(scriptsResource)) {
      reader.lines().forEach(line -> executeScriptLine(line, connection));
    } catch (final IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
