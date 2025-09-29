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
import static org.hamcrest.Matchers.notNullValue;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.nio.file.Files;
import java.nio.file.Path;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.UtilityMarker;

/**
 * Test utility for verifying Java serialization round-trip of a Catalog for any database
 * connection. Uses maximum schema info level and validates the schema structure before and after
 * serialization.
 */
@UtilityMarker
public final class CatalogSerializationTestUtility {

  private CatalogSerializationTestUtility() {
    // Utility class
  }

  /**
   * Performs a Java serialization round-trip of a Catalog obtained from the provided data source
   * and asserts that the catalog validates both before and after serialization.
   *
   * @param dataSource the database connection source
   * @throws Exception if any IO or crawling error occurs
   */
  public static void assertJavaSerializationRoundTrip(final Catalog catalog) throws Exception {

    validateSchema(catalog);

    final Path testOutputFile = IOUtility.createTempFilePath("sc_java_serialization", "ser");
    final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
    javaSerializedCatalogForSave.save(
        Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING));
    assertThat("Catalog was not serialized", isFileReadable(testOutputFile), is(true));

    // Deserialize generated serialized file, and assert load
    final JavaSerializedCatalog javaSerializedCatalogForLoad =
        new JavaSerializedCatalog(newInputStream(testOutputFile, READ));
    final Catalog catalogDeserialized = javaSerializedCatalogForLoad.getCatalog();
    validateSchema(catalogDeserialized);
  }

  private static void validateSchema(final Catalog catalog) {
    assertThat("Could not obtain catalog", catalog, notNullValue());

    assertThat("No data types in the schema", catalog.getColumnDataTypes().isEmpty(), is(false));
    assertThat("No tables in the schema", catalog.getTables().isEmpty(), is(false));
    assertThat("No routines in the schema", catalog.getRoutines().isEmpty(), is(false));
  }
}
