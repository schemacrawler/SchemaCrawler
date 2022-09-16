/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.catalogloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseDriver;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;

public class SchemaCrawlerCatalogLoaderTest {

  @Test
  public void connection() throws SQLException {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getDataSource(), is(nullValue()));

    final Connection connection = new TestDatabaseDriver().connect("jdbc:test-db:test", null);
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);
    catalogLoader.setDataSource(dataSource);

    assertThat(catalogLoader.getDataSource(), is(not(nullValue())));

    connection.close();
  }

  @Test
  public void schemaCrawlerOptions() {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));
    assertThat(catalogLoader.getSchemaCrawlerOptions().equals(schemaCrawlerOptions), is(true));
  }

  @Test
  public void schemaRetrievalOptions() {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));

    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));
    assertThat(
        catalogLoader.getSchemaRetrievalOptions().equals(schemaRetrievalOptionsDefault), is(true));
  }
}
