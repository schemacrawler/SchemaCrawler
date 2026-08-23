/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.utility.DatabaseConnectorUtility;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.DatabaseServerType;

@WithTestDatabase
public class MatchSchemaRetrievalOptionsTest {

  @DisplayName("No exception for known connector + plugin not found = throw an exception")
  @Test
  public void a_noException_noPlugin() throws Exception {

    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final InternalRuntimeException exception =
        assertThrows(
            InternalRuntimeException.class,
            () -> DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource));
    assertThat(exception.getMessage(), containsString("<oracle>"));
  }

  @DisplayName("No exception for known connector + plugin found = use plugin")
  @Test
  public void b_noException_withPlugin(final DatabaseConnectionSource connectionSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("Exception does not match URL + plugin found = use plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "newdb")
  public void c_exceptionDoesNotMatch_withPlugin(final DatabaseConnectionSource connectionSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("Exception matches URL + plugin found = use plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void d_exceptionMatchesURL_withPlugin(final DatabaseConnectionSource connectionSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("Exception matches URL + plugin not found = use 'unknown' plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void e_exceptionMatchesURL_withoutPlugin() throws Exception {

    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
  }

  @DisplayName("Exception does not match URL + plugin not found = throw an exception")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "newdb")
  public void f_exceptionDoesNotMatch_withputPlugin() throws Exception {

    // Mock an Oracle connection - plugin is not found
    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final InternalRuntimeException exception =
        assertThrows(
            InternalRuntimeException.class,
            () -> DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource));
    assertThat(exception.getMessage(), containsString("<oracle>"));
  }

  @DisplayName("Unknown connector + plugin not required = use 'unknown' plugin")
  @Test
  public void g_noException_unknownUrl() throws Exception {

    final DatabaseConnectionSource connectionSource =
        mockConnectionSourceForUrl("jdbc:newdb:foo", "Mock NewDB connection");

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
  }

  private DatabaseConnectionSource mockConnectionSourceForUrl(
      final String connectionUrl, final String toString) throws SQLException {
    final DatabaseMetaData databaseMetaData = TestObjectUtility.mockDatabaseMetaData();
    when(databaseMetaData.getURL()).thenReturn(connectionUrl);
    when(databaseMetaData.toString()).thenReturn(toString);
    final Connection connection = TestObjectUtility.mockConnection();
    when(connection.getMetaData()).thenReturn(databaseMetaData);
    when(connection.toString()).thenReturn(toString);
    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.fromConnection(connection);
    return connectionSource;
  }

  private DatabaseConnectionSource mockOracleConnectionSource() throws SQLException {
    final String fakeOracleUrl = "jdbc:oracle:foo";
    final DatabaseConnectionSource connectionSource =
        mockConnectionSourceForUrl(fakeOracleUrl, "Mock Oracle connection");
    return connectionSource;
  }
}
