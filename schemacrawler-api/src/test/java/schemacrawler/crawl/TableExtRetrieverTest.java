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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
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
public class TableExtRetrieverTest
{

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve table definitions from INFORMATION_SCHEMA")
  public void tableDefinitions(final Connection connection)
    throws Exception
  {

    final String definition = "TEST Table definition";

    final InformationSchemaViews informationSchemaViews = InformationSchemaViewsBuilder
      .builder()
      .withSql(InformationSchemaKey.EXT_TABLES,
               String.format("SELECT DISTINCT TABLE_CAT AS TABLE_CATALOG, TABLE_SCHEM AS TABLE_SCHEMA, "
                             + "TABLE_NAME, '%s' AS TABLE_DEFINITION " + "FROM INFORMATION_SCHEMA.SYSTEM_TABLES",
                             definition))
      .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever = new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveTableDefinitions();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      assertThat(table.getDefinition(), is(definition));
    }
  }

  @Test
  @DisplayName("Retrieve primary key definitions from INFORMATION_SCHEMA")
  public void primaryKeyInfo(final Connection connection)
    throws Exception
  {
    final String definition = "TEST Primary Key definition";

    final InformationSchemaViews informationSchemaViews = InformationSchemaViewsBuilder
      .builder()
      .withSql(InformationSchemaKey.EXT_PRIMARY_KEYS,
               String.format("SELECT DISTINCT TABLE_CAT AS PRIMARY_KEY_CATALOG, TABLE_SCHEM AS PRIMARY_KEY_SCHEMA, "
                             + "TABLE_NAME AS PRIMARY_KEY_TABLE_NAME, PK_NAME AS PRIMARY_KEY_NAME, "
                             + "'%s' AS PRIMARY_KEY_DEFINITION " + "FROM INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS",
                             definition))
      .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever = new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrievePrimaryKeyDefinitions(catalog.getAllTables());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      if (!Arrays
        .asList("Global Counts", "AUTHORSLIST", "BOOKAUTHORS", "PUBLICATIONWRITERS", "SALES", "SALESDATA")
        .contains(table.getName()))
      {
        assertThat(table
                     .getPrimaryKey()
                     .getDefinition(), is(definition));
      }
    }
  }

  @Test
  @DisplayName("Retrieve index definitions from INFORMATION_SCHEMA")
  public void indexInfo(final Connection connection)
    throws Exception
  {

    final String remarks = "TEST Index remarks";
    final String definition = "TEST Index definition";

    final InformationSchemaViews informationSchemaViews = InformationSchemaViewsBuilder
      .builder()
      .withSql(InformationSchemaKey.EXT_INDEXES,
               String.format("SELECT DISTINCT TABLE_CAT AS INDEX_CATALOG, TABLE_SCHEM AS INDEX_SCHEMA, "
                             + "TABLE_NAME, INDEX_NAME, '%s' AS REMARKS, '%s' AS INDEX_DEFINITION "
                             + "FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO", remarks, definition))
      .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever = new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveIndexInformation();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      for (final Index index : table.getIndexes())
      {
        assertThat(index.getRemarks(), is(remarks));
        assertThat(index.getDefinition(), is(definition));
      }
    }
  }

  @Test
  @DisplayName("Retrieve index column definitions from INFORMATION_SCHEMA")
  public void indexColumnInfo(final Connection connection)
    throws Exception
  {

    final String definition = "TEST INDEX COLUMN DEFINITION";

    final InformationSchemaViews informationSchemaViews = InformationSchemaViewsBuilder
      .builder()
      .withSql(InformationSchemaKey.EXT_INDEX_COLUMNS,
               String.format("SELECT TABLE_CAT AS INDEX_CATALOG, TABLE_SCHEM AS INDEX_SCHEMA, "
                             + "TABLE_NAME, INDEX_NAME, COLUMN_NAME, "
                             + "1 AS IS_GENERATED, '%s' AS INDEX_COLUMN_DEFINITION "
                             + "FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO", definition))
      .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViews(informationSchemaViews);
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

  @BeforeAll
  public void loadBaseCatalog(final Connection connection)
    throws SchemaCrawlerException
  {
    catalog = (MutableCatalog) getCatalog(connection,
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
  }

}
