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
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TableExtRetrieverTest
{

  @Test
  @DisplayName("Verify that index column definitions can be obtained from INFORMATION_SCHEMA")
  public void indexColumnInfo(final Connection connection)
    throws Exception
  {

    final String definition = "TEST INDEX COLUMN DEFINITION";

    final MutableCatalog catalog = getBaseCatalog(connection);

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViewsBuilder()
      .withSql(InformationSchemaKey.EXT_INDEX_COLUMNS,
               String.format("SELECT TABLE_CAT AS INDEX_CATALOG, TABLE_SCHEM AS INDEX_SCHEMA, "
                             + "TABLE_NAME, INDEX_NAME, COLUMN_NAME, "
                             + "1 AS IS_GENERATED, '%s' AS INDEX_COLUMN_DEFINITION "
                             + "FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO", definition));
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever = new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveIndexColumnInformation();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      for (final Index index : table.getIndexes())
      {
        final List<IndexColumn> columns = index.getColumns();
        assertThat(columns, is(not(empty())));
        for (final IndexColumn column : columns)
        {
          assertThat(column.getDefinition(), is(definition));
        }

      }
    }
  }

  private MutableCatalog getBaseCatalog(final Connection connection)
    throws SchemaCrawlerException
  {
    final MutableCatalog catalog = (MutableCatalog) getCatalog(connection,
                                                               SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                                                               SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      for (final Index index : table.getIndexes())
      {
        final List<IndexColumn> columns = index.getColumns();
        assertThat(columns, is(not(empty())));
        for (final IndexColumn column : columns)
        {
          assertThat(column.isGenerated(), is(false));
          assertThat(column.getDefinition(), is(""));
        }

      }
    }

    return catalog;
  }

}
