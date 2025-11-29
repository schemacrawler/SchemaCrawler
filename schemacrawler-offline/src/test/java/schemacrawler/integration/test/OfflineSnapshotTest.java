/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineDatabaseConnectionSource;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.TestUtility.failTestSetup;
import static us.fatehi.test.utility.TestUtility.flattenCommandlineArgs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.OfflineDatabaseConnector;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OfflineSnapshotTest {

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";

  private Path serializedCatalogFile;

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLine() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", Boolean.FALSE.toString());
      argsMap.put("--info-level", "maximum");
      argsMap.put("--routines", ".*");
      argsMap.put("--command", "details");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = "details.txt";
    assertThat(
        outputOf(testout),
        hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT + expectedResource)));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLineWithFilters() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", "true");
      argsMap.put("--info-level", "maximum");
      argsMap.put("--command", "details");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--routines", "");
      argsMap.put("--tables", ".*SALES");
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(
        outputOf(testout),
        hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT + "offlineWithFilters.txt")));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotCommandLineWithSchemaFilters() throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "offline");
      argsMap.put("--database", serializedCatalogFile.toString());

      argsMap.put("--no-info", "true");
      argsMap.put("--info-level", "maximum");
      argsMap.put("--routines", ".*");
      argsMap.put("--command", "list");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--schemas", "PUBLIC.BOOKS");
      argsMap.put("--output-file", out.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg : argsMap.entrySet()) {
        argsList.add("-%s=%s".formatted(arg.getKey(), arg.getValue()));
      }

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(
        outputOf(testout),
        hasSameContentAs(
            classpathResource(OFFLINE_EXECUTABLE_OUTPUT + "offlineWithSchemaFilters.txt")));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void offlineSnapshotExecutable() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    schemaTextOptionsBuilder.noInfo(false);

    final DatabaseConnectionSource dataSource =
        newOfflineDatabaseConnectionSource(serializedCatalogFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
    executable.setDataSource(dataSource);

    final String expectedResource = "details.txt";
    executeExecutable(executable, OFFLINE_EXECUTABLE_OUTPUT + expectedResource);
  }

  @BeforeEach
  public void serializeCatalog(final DatabaseConnectionSource dataSource) {
    try {
      final LimitOptionsBuilder limitOptionsBuilder =
          LimitOptionsBuilder.builder().includeAllRoutines();
      final LoadOptionsBuilder loadOptionsBuilder =
          LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      final SchemaCrawlerOptions schemaCrawlerOptions =
          SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
              .withLimitOptions(limitOptionsBuilder.toOptions())
              .withLoadOptions(loadOptionsBuilder.toOptions());

      final Catalog catalog =
          getCatalog(
              dataSource,
              SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
              schemaCrawlerOptions,
              ConfigUtility.newConfig());
      assertThat("Could not obtain catalog", catalog, notNullValue());
      assertThat("Could not find any schemas", catalog.getSchemas(), not(empty()));

      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
      assertThat("Could not obtain schema", schema, notNullValue());
      assertThat(
          "Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));

      serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler", "ser");
      final JavaSerializedCatalog serializedCatalog = new JavaSerializedCatalog(catalog);
      final OutputStream outputStream =
          new GZIPOutputStream(
              Files.newOutputStream(serializedCatalogFile, WRITE, CREATE, TRUNCATE_EXISTING));
      serializedCatalog.save(outputStream);
      assertThat("Database was not serialized", isFileReadable(serializedCatalogFile), is(true));
    } catch (final IOException e) {
      failTestSetup("Could not serialize catalog", e);
    }
  }

  private void executeExecutable(
      final SchemaCrawlerExecutable executable, final String referenceFileName) throws Exception {
    final DatabaseConnectionSource dataSource =
        newOfflineDatabaseConnectionSource(serializedCatalogFile);
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE);

    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(referenceFileName)));
  }
}
