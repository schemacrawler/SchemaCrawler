/*
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
package schemacrawler.crawl;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@Disabled("Not supported by HyperSQL")
public class IndexRetrieverMetadataAllTest
{

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve indexes and primary keys from metadata for all tables")
  public void indexesAndPrimaryKeysFromDataDictionary(final Connection connection)
    throws Exception
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withIndexRetrievalStrategy(MetadataRetrievalStrategy.metadata_all)
      .withPrimaryKeyRetrievalStrategy(MetadataRetrievalStrategy.metadata_all);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
    indexRetriever.retrieveIndexes(catalog.getAllTables());
    indexRetriever.retrievePrimaryKeys(catalog.getAllTables());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      if (!Arrays
        .asList("Global Counts", "AUTHORSLIST")
        .contains(table.getName()))
      {
        assertThat("Did not find indexes for " + table.getFullName(), table.getIndexes(), is(not(empty())));
      }
      if (!Arrays
        .asList("Global Counts", "AUTHORSLIST", "BOOKAUTHORS", "PUBLICATIONWRITERS", "SALES", "SALESDATA")
        .contains(table.getName()))
      {
        assertThat("Did not find primary key for " + table.getFullName(), table.getPrimaryKey(), is(not(nullValue())));
      }
    }
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum())
      .toOptions();
    catalog = (MutableCatalog) getCatalog(connection,
                                          SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                                          schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      assertThat(table.getIndexes(), is(empty()));
      assertThat(table.getPrimaryKey(), is(nullValue()));
    }
  }

}
