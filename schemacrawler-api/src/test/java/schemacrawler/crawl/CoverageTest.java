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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class CoverageTest {

  @Test
  public void connectionInfoBuilder(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);
    final DatabaseInfo databaseInfo = connectionInfoBuilder.buildDatabaseInfo();
    final JdbcDriverInfo jdbcDriverInfo = connectionInfoBuilder.buildJdbcDriverInfo();

    assertThat(
        jdbcDriverInfo.getConnectionUrl(),
        matchesPattern("jdbc:hsqldb:hsql://\\d*\\.\\d*\\.\\d*\\.\\d*:\\d*/schemacrawler\\d*"));

    assertThat(databaseInfo.getDatabaseProductName(), is("HSQL Database Engine"));
    assertThat(databaseInfo.getDatabaseProductVersion(), is("2.7.3"));

    assertThat(jdbcDriverInfo.getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
    assertThat(jdbcDriverInfo.getDriverMajorVersion(), is(2));
    assertThat(jdbcDriverInfo.getDriverMinorVersion(), is(7));
    assertThat(jdbcDriverInfo.getDriverName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getDriverVersion(), is("2.7.3"));

    assertThat(jdbcDriverInfo.getJdbcMajorVersion(), is(4));
    assertThat(jdbcDriverInfo.getJdbcMinorVersion(), is(2));
    assertThat(databaseInfo.getUserName(), is("SA"));
  }

  @Test
  public void connectionInfoBuilderException(final Connection connection) throws SQLException {

    final DatabaseMetaData dbMetaData2 = spy(connection.getMetaData());
    // See issue #931
    when(dbMetaData2.getUserName()).thenThrow(new SQLException("Cannot get user name"));

    final Connection connection2 = mock();
    when(connection2.getMetaData()).thenReturn(dbMetaData2);

    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection2);
    final DatabaseInfo databaseInfo = connectionInfoBuilder.buildDatabaseInfo();
    final JdbcDriverInfo jdbcDriverInfo = connectionInfoBuilder.buildJdbcDriverInfo();

    assertThat(jdbcDriverInfo.getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
    assertThat(jdbcDriverInfo.getDriverMajorVersion(), is(2));
    assertThat(jdbcDriverInfo.getDriverMinorVersion(), is(7));
    assertThat(jdbcDriverInfo.getDriverName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getDriverVersion(), is("2.7.3"));

    assertThat(databaseInfo.getUserName(), is(""));
  }

  @Test
  public void namedObjectList() {
    final NamedObjectList<NamedObject> list = new NamedObjectList<>();
    list.add(
        new AbstractNamedObject("name1") {

          private static final long serialVersionUID = -514565049545540452L;
        });
    list.add(
        new AbstractNamedObject("name2") {

          private static final long serialVersionUID = 6176088733525976950L;
        });
    assertThat(list.size(), equalTo(2));
    assertThat(list.toString(), equalTo("[\"name1\", \"name2\"]"));
  }

  @Test
  public void namedObjectListNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          final NamedObjectList<NamedObject> list = new NamedObjectList<>();
          list.add(null);
        });
  }

  @Test
  public void retrieverConnection() throws SQLException {
    assertThrows(NullPointerException.class, () -> new RetrieverConnection(null, null));
  }

  @Test
  public void retrieverConnectionClosed(final DatabaseConnectionSource dataSource) {
    assertThrows(
        NullPointerException.class,
        () -> {
          final Connection connection = dataSource.get();
          connection.close();
          new RetrieverConnection(dataSource, null);
        });
  }
}
