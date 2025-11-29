/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
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

  /**
   * Performs a Java serialization round-trip of a Catalog obtained from the provided data source
   * and asserts that the catalog validates both before and after serialization.
   *
   * @param dataSource the database connection source
   * @throws Exception if any IO or crawling error occurs
   */
  public static void assertJavaSerializationRoundTrip(final Catalog catalog) throws Exception {

    assertThat("No catalog provided", catalog, is(not(nullValue())));

    // Get number of objects in the catalog
    final int numColumnDataTypes = catalog.getColumnDataTypes().size();
    final int numTables = catalog.getTables().size();
    final int numRoutines = catalog.getRoutines().size();

    // Serialize catalog to a temporary file
    final Path testOutputFile = IOUtility.createTempFilePath("sc_java_serialization", "ser");
    final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
    javaSerializedCatalogForSave.save(
        Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING));
    assertThat("Catalog was not serialized", isFileReadable(testOutputFile), is(true));

    // Deserialize generated serialized file, and assert load
    final JavaSerializedCatalog javaSerializedCatalogForLoad =
        new JavaSerializedCatalog(newInputStream(testOutputFile, READ));
    final Catalog catalogDeserialized = javaSerializedCatalogForLoad.getCatalog();

    // Assert that the deserialized catalog has the same number of objects
    assertThat(
        "Different number of data types in deserialized catalog",
        catalogDeserialized.getColumnDataTypes().size(),
        is(numColumnDataTypes));
    assertThat(
        "Different number of tables in deserialized catalog",
        catalogDeserialized.getTables().size(),
        is(numTables));
    assertThat(
        "Different number of routines in deserialized catalog",
        catalogDeserialized.getRoutines().size(),
        is(numRoutines));
  }

  private CatalogSerializationTestUtility() {
    // Utility class
  }
}
