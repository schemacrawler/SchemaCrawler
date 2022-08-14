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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class SchemaRetrieverTest {

  @Test
  @DisplayName("Verify that schemas can be obtained from INFORMATION_SCHEMA")
  public void schemataView(final Connection connection) throws SQLException {
    // Mock database metadata, so we can check if it is being used over the INFORMATION_SCHEMA
    final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
    final Connection spyConnection = spy(connection);
    when(spyConnection.getMetaData()).thenReturn(databaseMetaData);
    when(databaseMetaData.getDatabaseProductName()).thenReturn("databaseProductName");
    when(databaseMetaData.getDatabaseProductVersion()).thenReturn("databaseProductVersion");
    when(databaseMetaData.getURL()).thenReturn("connectionUrl");
    when(databaseMetaData.getDriverName()).thenReturn("driverName");
    when(databaseMetaData.getDriverVersion()).thenReturn("driverVersion");

    final ConnectionInfo connectionInfo = ConnectionInfoBuilder.builder(connection).build();
    final MutableCatalog catalog = new MutableCatalog("test_catalog", connectionInfo);

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(InformationSchemaKey.SCHEMATA, "SELECT * FROM INFORMATION_SCHEMA.SCHEMATA")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(spyConnection);
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final SchemaRetriever schemaRetriever =
        new SchemaRetriever(retrieverConnection, catalog, options);
    schemaRetriever.retrieveSchemas(new IncludeAll());

    verify(databaseMetaData, times(0)).getSchemas();
    assertThat(
        schemaRetriever.getAllSchemas().values().stream()
            .map(SchemaReference::getFullName)
            .collect(toList()),
        is(
            asList(
                "PUBLIC.BOOKS",
                "PUBLIC.FOR_LINT",
                "PUBLIC.INFORMATION_SCHEMA",
                "PUBLIC.PUBLIC",
                "PUBLIC.\"PUBLISHER SALES\"",
                "PUBLIC.SYSTEM_LOBS")));
  }
}
