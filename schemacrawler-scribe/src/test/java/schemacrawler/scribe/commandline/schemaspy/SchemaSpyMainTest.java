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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
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
  public void dbHelpRunsWithoutConnectionArguments() {
    final int result = SchemaSpyMain.execute("-dbHelp");
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

  @Test
  public void resolvesArgsFromSchemaspyPropertiesInCurrentDirectory(@TempDir final Path tempDir)
      throws Exception {
    final Path propertiesFile = tempDir.resolve("schemaspy.properties");
    Files.writeString(
        propertiesFile,
        """
        t=pgsql
        db=books
        u=scott
        p=tiger
        host=db.example
        """);
    final String previousUserDir = System.getProperty("user.dir");
    try {
      System.setProperty("user.dir", tempDir.toString());
      final String[] effectiveArgs = SchemaSpyMain.resolveEffectiveArgs("-o", "out.zip");

      assertThat(Arrays.asList(effectiveArgs).contains("-t"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("pgsql"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-db"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("books"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-u"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("scott"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-host"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("db.example"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-o"), is(true));
    } finally {
      System.setProperty("user.dir", previousUserDir);
    }
  }

  @Test
  public void cliArgumentsOverrideSchemaspyProperties(@TempDir final Path tempDir)
      throws Exception {
    final Path propertiesFile = tempDir.resolve("schemaspy.properties");
    Files.writeString(
        propertiesFile,
        """
        db=booksFromFile
        u=userFromFile
        """);
    final String previousUserDir = System.getProperty("user.dir");
    try {
      System.setProperty("user.dir", tempDir.toString());
      final String[] effectiveArgs =
          SchemaSpyMain.resolveEffectiveArgs("-db", "booksFromCli", "-u", "userFromCli");

      assertThat(Arrays.asList(effectiveArgs).contains("booksFromFile"), is(false));
      assertThat(Arrays.asList(effectiveArgs).contains("userFromFile"), is(false));
      assertThat(Arrays.asList(effectiveArgs).contains("booksFromCli"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("userFromCli"), is(true));
    } finally {
      System.setProperty("user.dir", previousUserDir);
    }
  }

  @Test
  public void supportsSchemaspyPrefixedProperties(@TempDir final Path tempDir) throws Exception {
    final Path propertiesFile = tempDir.resolve("schemaspy.properties");
    Files.writeString(
        propertiesFile,
        """
        schemaspy.t=pgsql
        schemaspy.db=books
        schemaspy.u=scott
        schemaspy.host=db.example
        """);
    final String previousUserDir = System.getProperty("user.dir");
    try {
      System.setProperty("user.dir", tempDir.toString());
      final String[] effectiveArgs = SchemaSpyMain.resolveEffectiveArgs("-o", "out.zip");

      assertThat(Arrays.asList(effectiveArgs).contains("-t"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("pgsql"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-db"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("books"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-u"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("scott"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("-host"), is(true));
      assertThat(Arrays.asList(effectiveArgs).contains("db.example"), is(true));
    } finally {
      System.setProperty("user.dir", previousUserDir);
    }
  }

  @Test
  public void returnsOriginalArgsWhenNoPropertiesArePresent(@TempDir final Path tempDir) {
    final String previousUserDir = System.getProperty("user.dir");
    try {
      System.setProperty("user.dir", tempDir.toString());
      final String[] effectiveArgs = SchemaSpyMain.resolveEffectiveArgs("-t", "pgsql");
      assertArrayEquals(new String[] {"-t", "pgsql"}, effectiveArgs);
    } finally {
      System.setProperty("user.dir", previousUserDir);
    }
  }

  @Test
  public void mapsConnectionPropertiesArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs(
            "-t", "pgsql", "-db", "books", "-u", "scott", "-connprops", "key1=value1,key2=value2");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--jdbc-properties"));
    assertThat(equivalentCommand, containsString("key1=value1,key2=value2"));
  }

  @Test
  public void mapsCatalogArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-cat", "mycat");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--catalogs"));
    assertThat(equivalentCommand, containsString("mycat"));
  }

  @Test
  public void mapsSingleSchemaArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-s", "public");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--schemas"));
    assertThat(equivalentCommand, containsString("public"));
  }

  @Test
  public void mapsMultipleSchemasWithConversion() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-schemas", "schema1,schema2");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--schemas"));
    // Pattern uses \Q and \E for escaping: .*\.(schema1|schema2)
    assertThat(equivalentCommand, containsString("\\Qschema1\\E"));
    assertThat(equivalentCommand, containsString("\\Qschema2\\E"));
  }

  @Test
  public void mapsSchemaRegexArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-schemaSpec", ".*\\.TEST.*");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--schemas"));
    assertThat(equivalentCommand, containsString(".*\\.TEST.*"));
  }

  @Test
  public void schemaSpecTakesPriorityOverSchemasList() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs(
            "-t",
            "pgsql",
            "-db",
            "books",
            "-u",
            "scott",
            "-s",
            "single",
            "-schemas",
            "list1,list2",
            "-schemaSpec",
            ".*REGEX.*");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--schemas"));
    assertThat(equivalentCommand, containsString(".*REGEX.*"));
    assertThat(equivalentCommand, not(containsString("single")));
    assertThat(equivalentCommand, not(containsString("list1")));
  }

  @Test
  public void mapsIncludeTableArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-i", ".*\\.PUBLIC\\..*");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--tables"));
    assertThat(equivalentCommand, containsString(".*\\.PUBLIC\\..*"));
  }

  @Test
  public void mapsExcludeTableArgument() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-I", ".*\\.TEMP_.*");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--tables"));
    assertThat(
        equivalentCommand, containsString("(?!.*\\.TEMP_.*).*")); // negative lookahead pattern
  }

  @Test
  public void combinesIncludeAndExcludeTablePatterns() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command)
        .parseArgs(
            "-t",
            "pgsql",
            "-db",
            "books",
            "-u",
            "scott",
            "-i",
            ".*\\.PUBLIC\\..*",
            "-I",
            ".*TEMP.*");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--tables"));
    // Should contain pattern matching both include AND NOT exclude
    assertThat(equivalentCommand, containsString("(?="));
    assertThat(equivalentCommand, containsString("(?!"));
  }

  @Test
  public void enablesRowCountsByDefault() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command).parseArgs("-t", "pgsql", "-db", "books", "-u", "scott");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, containsString("--load-row-counts"));
    assertThat(equivalentCommand, containsString("true"));
  }

  @Test
  public void disablesRowCountsWhenNoRowsFlagSet() {
    final SchemaSpyMain command = new SchemaSpyMain();
    new CommandLine(command).parseArgs("-t", "pgsql", "-db", "books", "-u", "scott", "-norows");

    final String equivalentCommand = command.toEquivalentCommand();
    assertThat(equivalentCommand, not(containsString("--load-row-counts")));
  }
}
