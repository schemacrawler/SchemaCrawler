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
import static schemacrawler.test.ExecutableTestUtility.executableExecution;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.TestUtility.failTestSetup;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.utility.SerializedCatalogUtility;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@TestInstance(Lifecycle.PER_CLASS)
@WithTestDatabase
public class OfflineTest extends BaseAdditionalDatabaseTest {

  private Path serializedCatalogFile;

  @BeforeEach
  public void setupOfflineSnapshot(final DatabaseConnectionSource connectionSource)
      throws Exception {
    serializeCatalog(connectionSource);
    createDatabase();
  }

  @Test
  public void testOfflineWithConnection() throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeSchemas(Pattern.compile(".*"));
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.maximum);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevelBuilder(schemaInfoLevelBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(textOptionsBuilder.toConfig());

    final String expectedResource = "testOfflineWithConnection.txt";
    assertThat(
        outputOf(executableExecution(getConnectionSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  private void createDatabase() throws Exception {
    final DatabaseConnector connector =
        DatabaseConnectorRegistry.getRegistry().getDatabaseConnector("offline");
    final String offlineDatabaseFile = serializedCatalogFile.toFile().toString();
    final DatabaseConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions(
            "offline", null, null, offlineDatabaseFile, Map.of());
    createConnectionSource(
        connector.newDatabaseConnectionSource(connectionOptions, new MultiUseUserCredentials()));
  }

  private void serializeCatalog(final DatabaseConnectionSource connectionSource) {
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
              connectionSource,
              SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
              schemaCrawlerOptions,
              ConfigUtility.newConfig());
      validateCatalog(catalog);

      serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler", "ser");
      final OutputStream outputStream =
          new GZIPOutputStream(
              Files.newOutputStream(serializedCatalogFile, WRITE, CREATE, TRUNCATE_EXISTING));
      SerializedCatalogUtility.saveCatalog(catalog, outputStream);
      assertThat("Database was not serialized", isFileReadable(serializedCatalogFile), is(true));
    } catch (final IOException e) {
      failTestSetup("Could not serialize catalog", e);
    }
  }

  private void validateCatalog(final Catalog catalog) {
    assertThat("Could not obtain catalog", catalog, notNullValue());
    assertThat("Could not find any schemas", catalog.getSchemas(), not(empty()));

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));
  }
}
