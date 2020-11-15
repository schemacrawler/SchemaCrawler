/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.test.utility.TestDatabaseDriver;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;

public class DatabaseConnectionSourceTest {

  @Test
  public void databaseConnectionSource() throws SQLException, ClassNotFoundException {
    // Load test database driver
    Class.forName("schemacrawler.test.utility.TestDatabaseDriver");

    final DatabaseConnectionSource connectionSource =
        new DatabaseConnectionSource("jdbc:test-db:test");

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
  public void noDriver() throws SQLException, ClassNotFoundException {
    final DatabaseConnectionSource connectionSource =
        new DatabaseConnectionSource("jdbc:unknown-db:test");

    final Exception sqlException =
        assertThrows(SQLException.class, () -> connectionSource.getJdbcDriver());
    assertThat(
        sqlException.getMessage(),
        is(
            "Could not find a suitable JDBC driver for database connection URL, jdbc:unknown-db:test"));

    assertThat(
        connectionSource.toString(),
        is(
            "driver=<unknown>"
                + System.lineSeparator()
                + "url=jdbc:unknown-db:test"
                + System.lineSeparator()));

    final Exception connectionException =
        assertThrows(SchemaCrawlerRuntimeException.class, () -> connectionSource.get());
    connectionException.printStackTrace();
  }
}
