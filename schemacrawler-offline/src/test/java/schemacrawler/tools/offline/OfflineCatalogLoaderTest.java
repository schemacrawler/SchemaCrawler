package schemacrawler.tools.offline;/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseDriver;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.options.Config;

public class OfflineCatalogLoaderTest
{

  @Test
  public void additionalConfiguration()
  {
    final CatalogLoader catalogLoader = new OfflineCatalogLoader();

    assertThat(catalogLoader.getAdditionalConfiguration(),
               is(not(nullValue())));
    assertThat(catalogLoader
                 .getAdditionalConfiguration()
                 .size(), is(0));

    final Config config = new Config();
    config.put("hello", "world");
    catalogLoader.setAdditionalConfiguration(config);

    assertThat(catalogLoader.getAdditionalConfiguration(),
               is(not(nullValue())));
    assertThat(catalogLoader
                 .getAdditionalConfiguration()
                 .containsKey("hello"), is(true));
  }

  @Test
  public void connection()
  {
    final CatalogLoader catalogLoader = new OfflineCatalogLoader();

    assertThat(catalogLoader.getConnection(), is(nullValue()));

    final Connection connection =
      new TestDatabaseDriver().connect("jdbc:test-db:test", null);
    catalogLoader.setConnection(connection);

    assertThat(catalogLoader.getConnection(), is(not(nullValue())));
  }

  @Test
  public void databaseSystemIdentifier()
  {
    assertThat(new OfflineCatalogLoader().getDatabaseSystemIdentifier(),
               is("offline"));
  }

  @Test
  public void schemaCrawlerOptions()
  {
    final CatalogLoader catalogLoader = new OfflineCatalogLoader();

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));

    final SchemaCrawlerOptions schemaCrawlerOptions =
      SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));
    assertThat(catalogLoader
                 .getSchemaCrawlerOptions()
                 .equals(schemaCrawlerOptions), is(true));
  }

  @Test
  public void schemaRetrievalOptions()
  {
    final CatalogLoader catalogLoader = new OfflineCatalogLoader();

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));
    assertThat(catalogLoader
                 .getSchemaRetrievalOptions()
                 .getDatabaseServerType()
                 .getDatabaseSystemIdentifier(), is("offline"));

    final SchemaRetrievalOptions schemaRetrievalOptions =
      SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    // Set is a no-op operation, so it should not have any effect
    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));
    assertThat(catalogLoader
                 .getSchemaRetrievalOptions()
                 .equals(schemaRetrievalOptions), is(false));
  }

}
