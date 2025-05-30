/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test.serialize;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.utility.IOUtility.isFileReadable;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.options.Config;
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
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    assertThat("Could not obtain catalog", catalog, notNullValue());
    validateSchema(catalog);

    final Path testOutputFile = IOUtility.createTempFilePath("sc_java_serialization", "ser");
    final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
    javaSerializedCatalogForSave.save(
        Files.newOutputStream(testOutputFile, WRITE, CREATE, TRUNCATE_EXISTING));
    assertThat("Catalog was not serialized", isFileReadable(testOutputFile), is(true));
    assertThat(fileHeaderOf(testOutputFile), is("ACED"));

    final JavaSerializedCatalog javaSerializedCatalogForLoad =
        new JavaSerializedCatalog(newInputStream(testOutputFile, READ));
    final Catalog catalogDeserialized = javaSerializedCatalogForLoad.getCatalog();
    assertThat("Could not obtain catalog", catalogDeserialized, notNullValue());
    validateSchema(catalogDeserialized);
  }

  private void validateSchema(final Catalog catalog) {
    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat(
        "Unexpected number of tables in the schema", catalog.getColumnDataTypes(), hasSize(32));
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));
    assertThat(
        "Unexpected number of routines in the schema", catalog.getRoutines(schema), hasSize(4));
    assertThat(
        "Unexpected number of synonyms in the schema", catalog.getSynonyms(schema), hasSize(0));
    assertThat(
        "Unexpected number of sequences in the schema", catalog.getSequences(schema), hasSize(0));
  }
}
