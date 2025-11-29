/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
