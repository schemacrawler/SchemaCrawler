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
package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_COLUMN_PRIVILEGES;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_PRIVILEGES;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.none;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablePrivilegesRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TablePrivilegeRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve column privileges without metadata retrieval strategy")
  public void columnPrivilegesBadMetadataRetrievalStrategy(
      final DatabaseConnectionSource dataSource) throws Exception {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(TABLE_COLUMN_PRIVILEGES, "<<bad sql>>")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .withInformationSchemaViews(informationSchemaViews)
        .with(tableColumnPrivilegesRetrievalStrategy, none);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    tablePrivilegeRetriever.retrieveTableColumnPrivileges();

    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();
    final Column column = table.lookupColumn("FIRSTNAME").get();

    assertThat(column.getPrivileges(), is(empty()));
  }

  @Test
  @DisplayName("Retrieve column privileges without query")
  public void columnPrivilegesFromDataDictionaryWithoutQuery(
      final DatabaseConnectionSource dataSource) throws Exception {

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.with(tableColumnPrivilegesRetrievalStrategy, data_dictionary_all);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    assertThrows(
        ExecutionRuntimeException.class,
        () -> tablePrivilegeRetriever.retrieveTableColumnPrivileges());
  }

  @Test
  @DisplayName("Retrieve column privileges from metadata")
  public void columnPrivilegesFromMetadata(final DatabaseConnectionSource dataSource)
      throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    tablePrivilegeRetriever.retrieveTableColumnPrivileges();

    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();
    final Column column = table.lookupColumn("FIRSTNAME").get();
    assertThat(
        "HyperSQL does not support retrieving column privileges from metadata",
        column.getPrivileges(),
        is(empty()));
  }

  @BeforeAll
  public void loadBaseCatalog(final DatabaseConnectionSource dataSource) throws Exception {
    try (final Connection connection = dataSource.get(); ) {
      catalog =
          (MutableCatalog)
              getCatalog(
                  connection,
                  schemaRetrievalOptionsDefault,
                  SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

      final Collection<Table> tables = catalog.getTables();
      assertThat(tables, hasSize(19));
      for (final Table table : tables) {
        for (final Index index : table.getIndexes()) {
          final List<IndexColumn> columns = index.getColumns();
          assertThat(columns, is(not(empty())));
          for (final IndexColumn column : columns) {
            assertThat(column.isGenerated(), is(false));
            assertThat(column.getDefinition(), is(""));
          }
        }
      }
    }
  }

  @Test
  @DisplayName("Retrieve table privileges without metadata retrieval strategy")
  public void tablePrivilegesBadMetadataRetrievalStrategy(final DatabaseConnectionSource dataSource)
      throws Exception {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(TABLE_PRIVILEGES, "<<bad sql>>")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .withInformationSchemaViews(informationSchemaViews)
        .with(tablePrivilegesRetrievalStrategy, none);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();
    assertThat(table.getPrivileges(), is(empty()));

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    tablePrivilegeRetriever.retrieveTablePrivileges();

    assertThat(table.getPrivileges(), is(empty()));
  }

  @Test
  @DisplayName("Retrieve table privileges from data dictionary")
  public void tablePrivilegesFromDataDictionary(final DatabaseConnectionSource dataSource)
      throws Exception {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                TABLE_PRIVILEGES,
                "SELECT "
                    + "TABLE_CATALOG AS TABLE_CAT, TABLE_SCHEMA AS TABLE_SCHEM, TABLE_NAME, "
                    + "GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE "
                    + "FROM INFORMATION_SCHEMA.TABLE_PRIVILEGES")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .withInformationSchemaViews(informationSchemaViews)
        .with(tablePrivilegesRetrievalStrategy, data_dictionary_all);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();
    assertThat(table.getPrivileges(), is(empty()));

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    tablePrivilegeRetriever.retrieveTablePrivileges();

    assertThat(table.getPrivileges(), hasSize(6));
  }

  @Test
  @DisplayName("Retrieve table privileges without query")
  public void tablePrivilegesFromDataDictionaryWithoutQuery(
      final DatabaseConnectionSource dataSource) throws Exception {

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.with(tablePrivilegesRetrievalStrategy, data_dictionary_all);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TablePrivilegeRetriever tablePrivilegeRetriever =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    assertThrows(
        ExecutionRuntimeException.class, () -> tablePrivilegeRetriever.retrieveTablePrivileges());
  }
}
