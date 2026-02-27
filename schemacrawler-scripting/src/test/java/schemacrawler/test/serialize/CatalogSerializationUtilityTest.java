/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.serialize;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.DatabaseTestUtility.validateSchema;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.formatter.serialize.CatalogSerializationUtility;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class CatalogSerializationUtilityTest {

  @Test
  public void catalogSerialization(final DatabaseConnectionSource connectionSource)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog =
        getCatalog(
            connectionSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());
    validateSchema(catalog);

    final Path testOutputFile = IOUtility.createTempFilePath("sc_serialization", "ser.gz");
    try (final OutputStream out =
            Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING);
        final GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
      final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
      javaSerializedCatalogForSave.save(gzipOut);
    }

    // Deserialize using CatalogSerializationUtility
    final Catalog catalogDeserialized =
        CatalogSerializationUtility.deserializeCatalog(testOutputFile);
    assertThat(catalogDeserialized, is(notNullValue()));
    validateSchema(catalogDeserialized);
  }

  @Test
  public void deserializeNullPath() {
    assertThrows(
        NullPointerException.class, () -> CatalogSerializationUtility.deserializeCatalog(null));
  }

  @Test
  public void privateConstructor() throws Exception {
    final Constructor<CatalogSerializationUtility> constructor =
        CatalogSerializationUtility.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    assertThat(constructor.newInstance(), is(notNullValue()));
  }
}
