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

  @DisplayName("1: Controlled + on classpath + no exception = use plugin")
  @Test
  public void test1_controlled_onClasspath_noException(
      final DatabaseConnectionSource connectionSource) throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("2: Controlled + on classpath + exception matches = use plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void test2_controlled_onClasspath_exceptionMatches(
      final DatabaseConnectionSource connectionSource) throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("3: Controlled + on classpath + exception doesn't match = use plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "newdb")
  public void test3_controlled_onClasspath_exceptionDoesNotMatch(
      final DatabaseConnectionSource connectionSource) throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("4: Controlled + not on classpath + no exception = throw exception")
  @Test
  public void test4_controlled_noClasspath_noException() throws Exception {

    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final InternalRuntimeException exception =
        assertThrows(
            InternalRuntimeException.class,
            () -> DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource));
    assertThat(exception.getMessage(), containsString("<oracle>"));
  }

  @DisplayName("5: Controlled + not on classpath + exception matches = use 'unknown' plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void test5_controlled_noClasspath_exceptionMatches() throws Exception {

    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
  }

  @DisplayName("6: Controlled + not on classpath + exception doesn't match = throw exception")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "newdb")
  public void test6_controlled_noClasspath_exceptionDoesNotMatch() throws Exception {

    // Mock an Oracle connection - plugin is not found
    final DatabaseConnectionSource connectionSource = mockOracleConnectionSource();

    final InternalRuntimeException exception =
        assertThrows(
            InternalRuntimeException.class,
            () -> DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource));
    assertThat(exception.getMessage(), containsString("<oracle>"));
  }

  @DisplayName("8: Uncontrolled + not on classpath + any exception = use 'unknown' plugin")
  @Test
  public void test8_uncontrolled_noClasspath_noException() throws Exception {

    final DatabaseConnectionSource connectionSource = mockUncontrolledConnectionSource();

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

  private DatabaseConnectionSource mockUncontrolledConnectionSource() throws SQLException {
    return mockConnectionSourceForUrl("jdbc:newdb:foo", "Mock NewDB connection");
  }
}
