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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaKey;
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
public class ForeignKeyRetrieverDefinitionsTest
{

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve foreign key definitions")
  public void fkDefinitions(final Connection connection)
    throws Exception
  {

    final String definition = "TEST Foreign Key definition";

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViewsBuilder()
      .withSql(InformationSchemaKey.EXT_FOREIGN_KEYS,
               String.format("SELECT DISTINCT PKTABLE_CAT AS FOREIGN_KEY_CATALOG, PKTABLE_SCHEM AS FOREIGN_KEY_SCHEMA, "
                             + "PKTABLE_NAME AS FOREIGN_KEY_TABLE_NAME, FK_NAME AS FOREIGN_KEY_NAME, "
                             + "'%s' AS FOREIGN_KEY_DEFINITION "
                             + "FROM INFORMATION_SCHEMA.SYSTEM_CROSSREFERENCE", definition));
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final ForeignKeyRetriever foreignKeyRetriever = new ForeignKeyRetriever(retrieverConnection, catalog, options);
    foreignKeyRetriever.retrieveForeignKeyDefinitions(catalog.getAllTables());

    for (final Table table : catalog.getTables())
    {
      for (final ForeignKey foreignKey : table.getForeignKeys())
      {
        assertThat(foreignKey.getDefinition(), is(definition));
      }
    }
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder
                             .builder()
                             .withInfoLevel(InfoLevel.standard)
                             .setRetrieveForeignKeyDefinitions(false)
                             .toOptions())
      .toOptions();
    catalog = (MutableCatalog) getCatalog(connection,
                                          SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                                          schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables)
    {
      assertThat(table.getColumns(), is(not(empty())));
      table.getForeignKeys();
      for (final ForeignKey foreignKey : table.getForeignKeys())
      {
        assertThat(foreignKey.hasDefinition(), is(false));
      }
    }
  }

}
