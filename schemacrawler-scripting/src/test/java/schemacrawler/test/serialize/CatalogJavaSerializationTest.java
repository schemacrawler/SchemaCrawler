/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.serialize;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.DatabaseTestUtility.validateSchema;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.TestUtility.fileHeaderOf;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.nio.file.Files;
import java.nio.file.Path;
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
public class CatalogJavaSerializationTest {

  @Test
  public void catalogSerializationWithJava(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog =
        getCatalog(
            dataSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());
    validateSchema(catalog);

    final Path testOutputFile = IOUtility.createTempFilePath("sc_java_serialization", "ser");
    final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
    javaSerializedCatalogForSave.save(
        Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING));
    assertThat("Catalog was not serialized", isFileReadable(testOutputFile), is(true));
    assertThat(fileHeaderOf(testOutputFile), is("ACED"));

    // Deserialize generated JSON file, and assert load
    final JavaSerializedCatalog javaSerializedCatalogForLoad =
        new JavaSerializedCatalog(newInputStream(testOutputFile, READ));
    final Catalog catalogDeserialized = javaSerializedCatalogForLoad.getCatalog();
    validateSchema(catalogDeserialized);
  }
}
