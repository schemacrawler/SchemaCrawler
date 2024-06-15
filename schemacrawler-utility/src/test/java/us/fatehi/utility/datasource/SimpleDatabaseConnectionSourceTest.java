/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.datasource;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import us.fatehi.test.utility.DataSourceTestUtility;

@TestInstance(Lifecycle.PER_CLASS)
public class SimpleDatabaseConnectionSourceTest {

  private DatabaseConnectionSource databaseConnectionSource;

  @Test
  public void badConstructorArgs() throws Exception {

    assertThrows(
        RuntimeException.class,
        () ->
            new SimpleDatabaseConnectionSource(
                "<bad-url>", null, new MultiUseUserCredentials("user", "!"), connection -> {}));
  }

  @Test
  public void connectionTests() throws Exception {

    final Connection connection = databaseConnectionSource.get();
    assertThat(connection, is(not(nullValue())));
    assertThat(connection.getClass().getName(), not(endsWith("JDBCConnection")));
    final Connection unwrappedConnection = connection.unwrap(Connection.class);
    assertThat(unwrappedConnection.getClass().getName(), endsWith("JDBCConnection"));
    assertThat(connection.isClosed(), is(false));

    connection.close();
    assertThat(connection.isClosed(), is(true));
    assertThat(unwrappedConnection.isClosed(), is(false));

    databaseConnectionSource.releaseConnection(connection);
    assertThat(connection.isClosed(), is(true));
    assertThat(unwrappedConnection.isClosed(), is(false));

    databaseConnectionSource.close();
    assertThat(connection.isClosed(), is(true));
    assertThat(unwrappedConnection.isClosed(), is(true));
  }

  @BeforeEach
  public void createDatabase() throws Exception {

    final DataSource db = DataSourceTestUtility.newEmbeddedDatabase("/testdb.sql");
    final Connection wrappedConnection = db.getConnection();
    final DatabaseMetaData metaData = wrappedConnection.getMetaData();
    final String connectionUrl = metaData.getURL();
    final String userName = metaData.getUserName();
    final String password = "";
    databaseConnectionSource =
        new SimpleDatabaseConnectionSource(
            connectionUrl,
            new HashMap<>(),
            new MultiUseUserCredentials(userName, password),
            connection -> {});
  }
}
