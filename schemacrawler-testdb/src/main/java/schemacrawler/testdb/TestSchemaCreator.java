/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.testdb;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class TestSchemaCreator implements Runnable {

  public static void executeScriptLine(
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
      throw new SQLRuntimeException(String.format("Too many fields in \"%s\"", scriptResourceLine));
    }

    final boolean skip = "#".equals(delimiter);
    if (skip) {
      return;
    }

    try (final BufferedReader scriptReader =
        new ClasspathInputResource(scriptResource).openNewInputReader(UTF_8)) {
      new SqlScript(scriptReader, delimiter, connection).run();
    } catch (final IOException e) {
      throw new SQLRuntimeException(String.format("Could not read \"%s\"", scriptResource), e);
    }
  }

  private final Connection connection;

  private final String scriptsResource;

  public TestSchemaCreator(final Connection connection, final String scriptsResource) {
    this.connection = requireNonNull(connection, "No database connection provided");
    this.scriptsResource = requireNonNull(scriptsResource, "No script resource provided");
  }

  @Override
  public void run() {
    try (final BufferedReader reader =
        new ClasspathInputResource(scriptsResource).openNewInputReader(UTF_8)) {
      reader.lines().forEach(line -> executeScriptLine(line, connection));
    } catch (final IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
