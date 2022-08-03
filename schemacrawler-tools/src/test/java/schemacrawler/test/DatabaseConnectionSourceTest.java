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
package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.TestDatabaseDriver;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSources;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;

@DisableLogging
@WithTestDatabase
public class DatabaseConnectionSourceTest {

  @Test
  public void databaseConnectionSource() throws SQLException, ClassNotFoundException {
    // Load test database driver
    Class.forName("schemacrawler.test.utility.TestDatabaseDriver");

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource("jdbc:test-db:test");

    assertThat(
        connectionSource.toString(),
        is(
            "driver=schemacrawler.test.utility.TestDatabaseDriver"
                + System.lineSeparator()
                + "url=jdbc:test-db:test"
                + System.lineSeparator()));
    assertThat(connectionSource.getUserCredentials(), is(not(nullValue())));
    assertThat(
        connectionSource.getJdbcDriver().getClass().getSimpleName(), is("TestDatabaseDriver"));

    final Connection connection = connectionSource.get();

    assertThat(connection, is(not(nullValue())));
    assertThrows(SQLFeatureNotSupportedException.class, () -> connection.getMetaData());
    assertThat(connectionSource.getJdbcDriver().getClass(), is(TestDatabaseDriver.class));
  }

  @Test
  public void hsqldbConnectionSource(final DatabaseConnectionInfo databaseConnectionInfo)
      throws SQLException, ClassNotFoundException {

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            databaseConnectionInfo.getConnectionUrl());

    assertThat(connectionSource.toString(), startsWith("driver=org.hsqldb.jdbc.JDBCDriver"));
    assertThat(connectionSource.getUserCredentials(), is(not(nullValue())));
    assertThat(connectionSource.getJdbcDriver().getClass().getSimpleName(), is("JDBCDriver"));

    connectionSource.setUserCredentials(new SingleUseUserCredentials("sa", ""));

    final Connection connection = connectionSource.get();

    assertThat(connection, is(not(nullValue())));
  }

  @Test
  public void noDriver() throws SQLException, ClassNotFoundException {
    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource("jdbc:unknown-db:test");

    final Exception sqlException =
        assertThrows(SQLException.class, () -> connectionSource.getJdbcDriver());
    assertThat(
        sqlException.getMessage(),
        is(
            "Could not find a suitable JDBC driver for database connection URL <jdbc:unknown-db:test>: No suitable driver"));

    assertThat(
        connectionSource.toString(),
        is(
            "driver=<unknown>"
                + System.lineSeparator()
                + "url=jdbc:unknown-db:test"
                + System.lineSeparator()));

    final Exception connectionException =
        assertThrows(InternalRuntimeException.class, () -> connectionSource.get());
    connectionException.printStackTrace();
  }
}
