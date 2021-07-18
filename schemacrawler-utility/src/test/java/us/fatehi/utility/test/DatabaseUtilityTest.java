/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.DatabaseUtility;

public class DatabaseUtilityTest {

  @Test
  public void checkConnection() throws SQLException {

    final Connection connection = mock(Connection.class);

    assertThat(DatabaseUtility.checkConnection(connection), is(connection));

    when(connection.isClosed()).thenReturn(true);

    final SQLException exception1 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkConnection(null), is(nullValue())));
    assertThat(exception1.getMessage(), is("No database connection provided"));

    final SQLException exception2 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkConnection(connection), is(nullValue())));
    assertThat(exception2.getMessage(), is("Connection is closed"));
  }

  @Test
  public void checkResultSet() throws SQLException {

    final ResultSet results = mock(ResultSet.class);

    assertThat(DatabaseUtility.checkResultSet(results), is(results));

    when(results.isClosed()).thenReturn(true);

    final SQLException exception1 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkResultSet(null), is(nullValue())));
    assertThat(exception1.getMessage(), is("No result-set provided"));

    final SQLException exception2 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkResultSet(results), is(nullValue())));
    assertThat(exception2.getMessage(), is("Result-set is closed"));
  }

  @Test
  public void executeSql() throws SQLException {

    final Statement statement = mock(Statement.class);

    assertThat(DatabaseUtility.executeSql(null, "<some query>"), is(nullValue()));
    assertThat(DatabaseUtility.executeSql(statement, null), is(nullValue()));
  }

  @Test
  public void executeSql_throw() throws SQLException {

    final Statement statement = mock(Statement.class);
    doThrow(new SQLException("Exception using a mocked statement")).when(statement).execute(any());

    final SQLException exception =
        assertThrows(
            SQLException.class,
            () ->
                assertThat(DatabaseUtility.executeSql(statement, "<some query>"), is(nullValue())));
    assertThat(exception.getMessage(), is("Exception using a mocked statement"));
  }
}
