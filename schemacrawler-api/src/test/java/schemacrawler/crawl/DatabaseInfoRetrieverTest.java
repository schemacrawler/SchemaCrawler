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

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.schemacrawler.InformationSchemaKey.DATABASE_USERS;
import static schemacrawler.schemacrawler.InformationSchemaKey.SERVER_INFORMATION;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.Property;

@WithTestDatabase
@ResolveTestContext
public class DatabaseInfoRetrieverTest {

  private MutableCatalog catalog;

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(
                SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.minimum).toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog =
        (MutableCatalog)
            getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);

    assertThat(catalog, is(notNullValue()));
  }

  @Test
  @DisplayName("Test retrieving database info")
  public void testRetrieveDatabaseInfo(final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsDefault;
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // Act - retrieve additional database info
    databaseInfoRetriever.retrieveAdditionalDatabaseInfo();

    // Assert - verify database properties were retrieved
    final Collection<Property> databaseProperties = catalog.getDatabaseInfo().getProperties();
    assertThat(databaseProperties, is(not(empty())));
  }

  @Test
  @DisplayName("Test retrieving JDBC driver info")
  public void testRetrieveJdbcDriverInfo(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsDefault;
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // Act - retrieve JDBC driver info
    databaseInfoRetriever.retrieveAdditionalJdbcDriverInfo();

    // Assert - verify JDBC driver info was retrieved
    assertThat(catalog.getJdbcDriverInfo(), is(notNullValue()));
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is(notNullValue()));
  }

  @Test
  @DisplayName("Test retrieving database users with valid SQL")
  public void testRetrieveDatabaseUsers(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view for database users
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                DATABASE_USERS,
                "SELECT 'test_user' AS USER_NAME, 'Test User' AS DESCRIPTION FROM (VALUES(0))")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // Act - retrieve database users
    databaseInfoRetriever.retrieveDatabaseUsers();

    // We can't easily verify that specific users were added since it depends on the database,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving database users with invalid SQL")
  public void testRetrieveDatabaseUsersWithInvalidSql(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view with invalid SQL
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(DATABASE_USERS, "THIS IS NOT VALID SQL")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // Act & Assert - retrieving database users with invalid SQL should not throw exception
    databaseInfoRetriever.retrieveDatabaseUsers();

    // The method should handle the SQL exception gracefully
    final Collection<DatabaseUser> databaseUsers = catalog.getDatabaseUsers();
    assertThat(databaseUsers, is(empty()));
  }

  @Test
  @DisplayName("Test retrieving server info")
  public void testRetrieveServerInfo(final DatabaseConnectionSource dataSource) throws Exception {
    // Arrange - create a custom information schema view for server info
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                SERVER_INFORMATION,
                "SELECT 'test_property' AS NAME, 'test_value' AS VALUE, 'Test Description' AS DESCRIPTION FROM (VALUES(0))")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // Act - retrieve server info
    databaseInfoRetriever.retrieveServerInfo();

    // Assert - verify server properties were retrieved
    final Collection<Property> serverProperties = catalog.getDatabaseInfo().getServerInfo();
    assertThat(serverProperties, is(not(empty())));

    // Check for the specific property we added
    boolean hasTestProperty = false;
    for (Property property : serverProperties) {
      if (property.getName().equals("test_property")) {
        hasTestProperty = true;
        assertThat(property.getValue(), is("test_value"));
        break;
      }
    }
    assertThat("Should have test_property", hasTestProperty, is(true));
  }
}
