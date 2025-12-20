/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.utility.database.ConnectionInfoBuilder;
import us.fatehi.utility.database.DatabaseInformation;
import us.fatehi.utility.database.JdbcDriverInformation;

@TestInstance(Lifecycle.PER_CLASS)
public class ConnectionInfoBuilderTest {

  private Connection connection;

  @BeforeAll
  public void createDatabase() throws Exception {
    final DataSource db = DataSourceTestUtility.newEmbeddedDatabase("/testdb.sql");
    connection = db.getConnection();
  }

  @Test
  public void connectionInfoBuilder() throws SQLException {

    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);
    final DatabaseInformation databaseInfo = connectionInfoBuilder.buildDatabaseInformation();
    final JdbcDriverInformation jdbcDriverInfo = connectionInfoBuilder.buildJdbcDriverInformation();

    assertThat(jdbcDriverInfo.getConnectionUrl(), matchesPattern("jdbc:hsqldb:mem:.*"));

    assertThat(databaseInfo.getDatabaseProductName(), is("HSQL Database Engine"));
    assertThat(databaseInfo.getDatabaseProductVersion(), is("2.7.4"));

    assertThat(jdbcDriverInfo.getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
    assertThat(jdbcDriverInfo.getDriverMajorVersion(), is(2));
    assertThat(jdbcDriverInfo.getDriverMinorVersion(), is(7));
    assertThat(jdbcDriverInfo.getDriverName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getDriverVersion(), is("2.7.4"));

    assertThat(jdbcDriverInfo.getJdbcMajorVersion(), is(4));
    assertThat(jdbcDriverInfo.getJdbcMinorVersion(), is(2));
    assertThat(databaseInfo.getUserName(), is("SA"));
  }

  @Test
  public void connectionInfoBuilderException() throws SQLException {

    final DatabaseMetaData dbMetaData2 = spy(connection.getMetaData());
    // See issue #931
    when(dbMetaData2.getUserName()).thenThrow(new SQLException("Cannot get user name"));

    final Connection connection2 = mock();
    when(connection2.getMetaData()).thenReturn(dbMetaData2);

    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection2);
    final DatabaseInformation databaseInfo = connectionInfoBuilder.buildDatabaseInformation();
    final JdbcDriverInformation jdbcDriverInfo = connectionInfoBuilder.buildJdbcDriverInformation();

    assertThat(jdbcDriverInfo.getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
    assertThat(jdbcDriverInfo.getDriverMajorVersion(), is(2));
    assertThat(jdbcDriverInfo.getDriverMinorVersion(), is(7));
    assertThat(jdbcDriverInfo.getDriverName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getDriverVersion(), is("2.7.4"));

    assertThat(databaseInfo.getUserName(), is(""));
  }
}
