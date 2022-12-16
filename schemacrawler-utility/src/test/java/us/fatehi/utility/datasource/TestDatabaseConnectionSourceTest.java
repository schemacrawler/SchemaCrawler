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
package us.fatehi.utility.datasource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@TestInstance(Lifecycle.PER_CLASS)
public class TestDatabaseConnectionSourceTest {

  private DatabaseConnectionSource databaseConnectionSource;
  private Connection wrappedConnection;

  @Disabled
  @Test
  public void connectionTests() throws Exception {
    final Connection connection = databaseConnectionSource.get();
    assertThat(connection, is(not(nullValue())));
    assertThat(connection.getClass().getName(), endsWith("JDBCConnection"));
    final Connection unwrappedConnection = connection.unwrap(Connection.class);
    assertThat(unwrappedConnection.getClass().getName(), endsWith("JDBCConnection"));
    assertThat(connection.isClosed(), is(false));
    assertThat(databaseConnectionSource.toString(), containsString("DataSourceConnectionSource"));

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

  @Test
  public void constructorTest() throws Exception {
    final DatabaseMetaData metaData = wrappedConnection.getMetaData();
    final String connectionUrl = metaData.getURL();
    final String userName = metaData.getUserName();
    final String password = "";
    final HashMap<String, String> connectionProperties = new HashMap<String, String>();
    connectionProperties.put("key", "value");
    databaseConnectionSource =
        new SingleDatabaseConnectionSource(
            connectionUrl,
            connectionProperties,
            new MultiUseUserCredentials(userName, password),
            connection -> {});

    final Connection connection = databaseConnectionSource.get();
    assertThat(connection, is(not(nullValue())));

    connection.close();
  }

  @BeforeEach
  public void createDatabase() throws Exception {

    final EmbeddedDatabase db =
        new EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setScriptEncoding("UTF-8")
            .ignoreFailedDrops(true)
            .addScript("testdb.sql")
            .build();

    wrappedConnection = db.getConnection();
    databaseConnectionSource = DatabaseConnectionSources.fromDataSource(db);
  }
}
