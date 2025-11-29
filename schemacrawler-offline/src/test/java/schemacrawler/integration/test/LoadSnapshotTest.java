/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.DatabaseTestUtility.validateSchema;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.TestUtility.failTestSetup;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class LoadSnapshotTest {

  private Path serializedCatalogFile;

  @Test
  public void loadSnapshot() throws Exception {
    final JavaSerializedCatalog serializedCatalog =
        new JavaSerializedCatalog(newInputStream(serializedCatalogFile, READ));
    final Catalog catalog = serializedCatalog.getCatalog();

    validateSchema(catalog);
  }

  @BeforeEach
  public void serializeCatalog(final DatabaseConnectionSource dataSource) {

    try {

      final SchemaCrawlerOptions schemaCrawlerOptions =
          DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

      final Catalog catalog =
          getCatalog(
              dataSource,
              schemaRetrievalOptionsDefault,
              schemaCrawlerOptions,
              ConfigUtility.newConfig());
      assertThat("Could not obtain catalog", catalog, notNullValue());
      assertThat("Could not find any schemas", catalog.getSchemas(), not(empty()));

      validateSchema(catalog);

      serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler", "ser");

      final JavaSerializedCatalog serializedCatalog = new JavaSerializedCatalog(catalog);
      serializedCatalog.save(
          Files.newOutputStream(serializedCatalogFile, WRITE, CREATE, TRUNCATE_EXISTING));
      assertThat("Database was not serialized", isFileReadable(serializedCatalogFile), is(true));
    } catch (final IOException e) {
      failTestSetup("Could not serialize catalog", e);
    }
  }
}
