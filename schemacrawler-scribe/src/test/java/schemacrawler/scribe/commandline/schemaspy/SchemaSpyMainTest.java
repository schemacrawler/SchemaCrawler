/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SchemaSpyMainTest {

  @Test
  public void commandAssemblyMasksPasswordAndMapsKnownType() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs(
            "-t",
            "pgsql",
            "-db",
            "books",
            "-host",
            "dbhost",
            "-port",
            "5432",
            "-u",
            "scott",
            "-p",
            "tiger",
            "-o",
            "out.zip",
            "-loglevel",
            "warn");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--server postgresql"));
    assertThat(equivalentCommand, containsString("--host dbhost"));
    assertThat(equivalentCommand, containsString("--port 5432"));
    assertThat(equivalentCommand, containsString("--database books"));
    assertThat(equivalentCommand, containsString("--password ******"));
    assertThat(equivalentCommand, not(containsString("tiger")));
    assertThat(equivalentCommand, containsString("--log-level WARNING"));
  }

  @Test
  public void dbhelpRunsWithoutConnectionArguments() {
    final int result = SchemaSpyMain.execute("-dbhelp");
    assertThat(result, is(0));
  }

  @Test
  public void executeCreatesScribeOutput(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final String connectionUrl;
    try (Connection connection = connectionSource.get()) {
      connectionUrl = connection.getMetaData().getURL();
    }
    final Path outputFile = tempDir.resolve("schemaspy-output.zip");

    final int result =
        SchemaSpyMain.execute(
            "-t",
            "hsqldb",
            "-db",
            connectionUrl,
            "-u",
            "sa",
            "-o",
            outputFile.toString(),
            "-loglevel",
            "off");

    assertThat(result, is(0));
    assertTrue(Files.exists(outputFile));
    try (ZipFile zipFile = new ZipFile(outputFile.toFile())) {
      assertThat(zipFile.getEntry("index.md") != null, is(true));
    }
  }

  @Test
  public void executeReturnsErrorCodeWhenRequiredOptionsMissing() {
    final int result = SchemaSpyMain.execute("-t", "pgsql");
    assertThat(result, is(2));
  }

  @Test
  public void logLevelMappingPrefersDebugFlag() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command).parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-debug");
    assertThat(command.toJulLevel(), is(Level.FINE));
  }

  @Test
  public void unknownTypeFailsWithSupportedTypeList() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command).parseArgs("-t", "unknown-type", "-db", "books", "-u", "scott");
    final ExecutionRuntimeException exception =
        assertThrows(ExecutionRuntimeException.class, command::toEquivalentCommand);
    assertThat(exception.getMessage(), containsString("Supported database types"));
  }
}
