/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.matchSchemaRetrievalOptions;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class MatchSchemaRetrievalOptionsTest {

  @DisplayName("Exception does not match URL + plugin found = use plugin")
  @Test
  public void matchSchemaRetrievalOptions0A(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("Exception does not match URL correctly + plugin found = use plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "newdb")
  public void matchSchemaRetrievalOptions0B(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("hsqldb"));
  }

  @DisplayName("Exception matches URL + plugin found = use \"unknown\" plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void matchSchemaRetrievalOptions1(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
  }

  @DisplayName("Exception does not match URL + plugin not found = throw an exception")
  @Test
  public void matchSchemaRetrievalOptions2() throws Exception {

    // Mock an Oracle connection - plugin is not found
    final String fakeOracleUrl = "jdbc:oracle:foo";
    final DatabaseConnectionSource dataSource =
        mockConnectionForUrl(fakeOracleUrl, "Mock Oracle connection");

    final InternalRuntimeException exception =
        assertThrows(InternalRuntimeException.class, () -> matchSchemaRetrievalOptions(dataSource));
    assertThat(exception.getMessage(), containsString(fakeOracleUrl));
  }

  @DisplayName("Exception matches URL + plugin not found = use \"unknown\" plugin")
  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void matchSchemaRetrievalOptions3() throws Exception {

    // Mock an Oracle connection - plugin is not found
    final String fakeOracleUrl = "jdbc:oracle:foo";
    final DatabaseConnectionSource dataSource =
        mockConnectionForUrl(fakeOracleUrl, "Mock Oracle connection");

    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    final DatabaseServerType databaseServerType = schemaRetrievalOptions.getDatabaseServerType();
    assertThat(databaseServerType.isUnknownDatabaseSystem(), is(true));
  }

  private DatabaseConnectionSource mockConnectionForUrl(
      final String fakeOracleUrl, final String toString) throws SQLException {
    final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
    when(databaseMetaData.getURL()).thenReturn(fakeOracleUrl);
    when(databaseMetaData.toString()).thenReturn(toString);
    final Connection connection = mock(Connection.class);
    when(connection.getMetaData()).thenReturn(databaseMetaData);
    when(connection.toString()).thenReturn(toString);
    final DatabaseConnectionSource dataSource = mock(DatabaseConnectionSource.class);
    when(dataSource.get()).thenReturn(connection);
    return dataSource;
  }
}
