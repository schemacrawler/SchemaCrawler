/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.Property;

@WithTestDatabase
@ResolveTestContext
public class DatabaseInfoRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve additional database info")
  public void additionalDatabaseInfo(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    assertThat(
        "Should not have database properties",
        catalog.getDatabaseInfo().getProperties(),
        is(empty()));

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, SchemaRetrievalOptionsBuilder.builder().toOptions());

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveAdditionalDatabaseInfo();

    // Verify that database properties were retrieved
    final Collection<Property> databaseProperties = catalog.getDatabaseInfo().getProperties();
    assertThat("Should have database properties", databaseProperties, is(not(empty())));

    // Verify that some common database properties are present
    boolean foundSupportedProperty = false;
    for (final Property property : databaseProperties) {
      if (property.getName().startsWith("supports")) {
        foundSupportedProperty = true;
        break;
      }
    }
    assertThat("Should find at least one 'supports' property", foundSupportedProperty, is(true));

    // Verify that result set type properties were retrieved
    boolean foundResultSetTypeProperty = false;
    for (final Property property : databaseProperties) {
      if (property.getName().contains("ResultSets")) {
        foundResultSetTypeProperty = true;
        break;
      }
    }
    assertThat(
        "Should find at least one result set type property", foundResultSetTypeProperty, is(true));
  }

  @Test
  @DisplayName("Retrieve additional JDBC driver info")
  public void additionalJdbcDriverInfo(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    assertThat(catalog.getJdbcDriverInfo(), is(notNullValue()));
    assertThat(
        "Should not have JDBC driver properties",
        catalog.getJdbcDriverInfo().getDriverProperties(),
        is(empty()));

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, SchemaRetrievalOptionsBuilder.builder().toOptions());

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveAdditionalJdbcDriverInfo();

    // Verify that JDBC driver properties were retrieved
    final JdbcDriverInfo jdbcDriverInfo = catalog.getJdbcDriverInfo();
    assertThat(jdbcDriverInfo, is(notNullValue()));

    // HSQLDB driver should have some properties
    final Collection<JdbcDriverProperty> driverProperties = jdbcDriverInfo.getDriverProperties();
    assertThat("Should have JDBC driver properties", driverProperties, is(not(empty())));

    // Verify driver name and version
    assertThat(jdbcDriverInfo.getDriverName(), containsString("HSQL"));
    assertThat(jdbcDriverInfo.getDriverVersion(), is(notNullValue()));
  }

  @Test
  @DisplayName("Database info")
  public void databaseInfo(final TestContext testContext, final Connection connection)
      throws Exception {

    assertThat(
        catalog.getDatabaseInfo().toString(),
        is("-- database: HSQL Database Engine 2.7.4" + System.lineSeparator()));
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

  @Test
  @DisplayName("Test error handling in retrieveAdditionalDatabaseInfo")
  public void errorHandlingInRetrieveAdditionalDatabaseInfo(final TestContext testContext)
      throws Exception {

    // Create a retriever connection with a mocked connection source to simulate errors
    final Connection mockConnection = TestObjectUtility.mockConnection();
    final DatabaseMetaData mockMetaData = mockConnection.getMetaData();
    when(mockMetaData.isReadOnly()).thenThrow(SQLException.class);
    when(mockMetaData.getSchemas()).thenThrow(AbstractMethodError.class);
    when(mockMetaData.getCatalogs()).thenThrow(SQLException.class);
    final DatabaseConnectionSource dataSource =
        new ConnectionDatabaseConnectionSource(mockConnection);

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, SchemaRetrievalOptionsBuilder.builder().toOptions());

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    // The method should handle exceptions gracefully and not throw
    assertDoesNotThrow(() -> databaseInfoRetriever.retrieveAdditionalDatabaseInfo());
  }

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);
    final MutableDatabaseInfo databaseInfo =
        (MutableDatabaseInfo) connectionInfoBuilder.buildDatabaseInfo();
    final MutableJdbcDriverInfo jdbcDriverInfo =
        (MutableJdbcDriverInfo) connectionInfoBuilder.buildJdbcDriverInfo();

    catalog = new MutableCatalog("database_info_test", databaseInfo, jdbcDriverInfo);
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getDatabaseInfo().getServerInfo(), is(empty()));
  }

  @Test
  @DisplayName("Test result set type properties retrieval")
  public void resultSetTypePropertiesRetrieval(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    assertThat(catalog.getDatabaseInfo().getProperties(), is(empty()));

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, SchemaRetrievalOptionsBuilder.builder().toOptions());

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveAdditionalDatabaseInfo();

    // Verify that result set type properties were retrieved
    final Collection<Property> databaseProperties = catalog.getDatabaseInfo().getProperties();

    // Check for specific result set type properties
    boolean foundDeletesAreDetected = false;
    boolean foundInsertsAreDetected = false;
    boolean foundUpdatesAreDetected = false;
    boolean foundSupportsResultSetType = false;

    for (final Property property : databaseProperties) {
      final String propertyName = property.getName();
      if (propertyName.contains("deletesAreDetected")) {
        foundDeletesAreDetected = true;
      } else if (propertyName.contains("insertsAreDetected")) {
        foundInsertsAreDetected = true;
      } else if (propertyName.contains("updatesAreDetected")) {
        foundUpdatesAreDetected = true;
      } else if (propertyName.contains("supportsResultSetType")) {
        foundSupportsResultSetType = true;
      }
    }

    assertThat("Should find deletesAreDetected properties", foundDeletesAreDetected, is(true));
    assertThat("Should find insertsAreDetected properties", foundInsertsAreDetected, is(true));
    assertThat("Should find updatesAreDetected properties", foundUpdatesAreDetected, is(true));
    assertThat(
        "Should find supportsResultSetType properties", foundSupportsResultSetType, is(true));
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
