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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;

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
    assertThat("Could not find any schemas", catalog.getSchemas(), not(empty()));

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));

    final Path testOutputFile = IOUtility.createTempFilePath("sc_java_serialization", "ser");
    final JavaSerializedCatalog javaSerializedCatalogForSave = new JavaSerializedCatalog(catalog);
    javaSerializedCatalogForSave.save(Files.newOutputStream(testOutputFile));
    assertThat("Catalog was not serialized", Files.size(testOutputFile), greaterThan(0L));
    assertThat(fileHeaderOf(testOutputFile), is("ACED"));

    final JavaSerializedCatalog javaSerializedCatalogForLoad =
        new JavaSerializedCatalog(Files.newInputStream(testOutputFile));
    final Catalog catalogDeserialized = javaSerializedCatalogForLoad.getCatalog();

    final Schema schemaDeserialized = catalogDeserialized.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schemaDeserialized, notNullValue());
    assertThat(
        "Unexpected number of tables in the schema",
        catalogDeserialized.getTables(schemaDeserialized),
        hasSize(11));
  }
}
