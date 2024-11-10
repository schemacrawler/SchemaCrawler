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

package schemacrawler.integration.test;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.TestUtility.failTestSetup;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
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
public class LoadSnapshotTest {

  private Path serializedCatalogFile;

  @Test
  public void loadSnapshot() throws Exception {
    final JavaSerializedCatalog serializedCatalog =
        new JavaSerializedCatalog(newInputStream(serializedCatalogFile, READ));
    final Catalog catalog = serializedCatalog.getCatalog();

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));
  }

  @BeforeEach
  public void serializeCatalog(final DatabaseConnectionSource dataSource) {

    try {

      final SchemaCrawlerOptions schemaCrawlerOptions =
          DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

      final Catalog catalog =
          getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
      assertThat("Could not obtain catalog", catalog, notNullValue());
      assertThat("Could not find any schemas", catalog.getSchemas(), not(empty()));

      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
      assertThat("Could not obtain schema", schema, notNullValue());
      assertThat(
          "Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));

      serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler", "ser");

      final JavaSerializedCatalog serializedCatalog = new JavaSerializedCatalog(catalog);
      serializedCatalog.save(
          Files.newOutputStream(serializedCatalogFile, WRITE, CREATE, TRUNCATE_EXISTING));
      assertThat("Database was not serialized", size(serializedCatalogFile), greaterThan(0L));
    } catch (final IOException e) {
      failTestSetup("Could not serialize catalog", e);
    }
  }
}
