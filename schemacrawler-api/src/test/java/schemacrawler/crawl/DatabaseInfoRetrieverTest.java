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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseInfoRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Database info")
  public void databaseInfo(final TestContext testContext, final Connection connection)
      throws Exception {

    assertThat(
        catalog.getDatabaseInfo().toString(),
        is("-- database: HSQL Database Engine 2.7.0" + System.lineSeparator()));
  }

  @Test
  @DisplayName("Retrieve database users")
  public void databaseUsers(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(catalog.getDatabaseUsers(), is(empty()));

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.DATABASE_USERS,
                "SELECT USER_NAME AS USERNAME, "
                    + "ADMIN, INITIAL_SCHEMA, AUTHENTICATION, PASSWORD_DIGEST "
                    + "FROM INFORMATION_SCHEMA.SYSTEM_USERS")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder().withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveDatabaseUsers();

    final List<DatabaseUser> databaseUsers = new ArrayList<>(catalog.getDatabaseUsers());
    assertThat(databaseUsers, hasSize(2));
    assertThat(
        databaseUsers.stream().map(DatabaseUser::getName).collect(Collectors.toList()),
        hasItems("OTHERUSER", "SA"));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().size())
            .collect(Collectors.toList()),
        hasItems(3, 3));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().keySet())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()),
        hasItems("AUTHENTICATION", "PASSWORD_DIGEST", "ADMIN"));
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    final ConnectionInfo connectionInfo = ConnectionInfoBuilder.builder(connection).build();
    catalog = new MutableCatalog("database_info_test", connectionInfo);
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getDatabaseInfo().getServerInfo(), is(empty()));
  }

  @Test
  @DisplayName("Retrieve server info")
  public void serverInfo(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    assertThat(catalog.getDatabaseInfo().getServerInfo(), is(empty()));

    final String name = "TEST Server Info Property - Name";
    final String description = "TEST Server Info Property - Description";
    final String value = "TEST Server Info Property - Value";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.SERVER_INFORMATION,
                String.format(
                    "SELECT '%s' AS NAME, '%s' AS DESCRIPTION, '%s' AS VALUE "
                        + "FROM INFORMATION_SCHEMA.SYSTEM_TYPEINFO",
                    name, description, value))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder().withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveServerInfo();

    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo().getServerInfo());
    assertThat(serverInfo, hasSize(1));
    final Property serverInfoProperty = serverInfo.get(0);
    assertThat(serverInfoProperty, is(new ImmutableServerInfoProperty(name, value, description)));
    assertThat(serverInfoProperty.getDescription(), is(description));
  }
}
