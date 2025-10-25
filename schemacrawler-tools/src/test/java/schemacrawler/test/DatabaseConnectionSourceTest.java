/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
@WithTestDatabase
public class DatabaseConnectionSourceTest {

  @Test
  public void databaseConnectionSource() throws SQLException, ClassNotFoundException {

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            "jdbc:test-db:test", new MultiUseUserCredentials());

    final Connection connection = connectionSource.get();

    assertThat(connection, is(not(nullValue())));
    assertThrows(SQLFeatureNotSupportedException.class, () -> connection.getMetaData());
  }

  @Test
  public void hsqldbConnectionSource(final DatabaseConnectionInfo databaseConnectionInfo)
      throws SQLException, ClassNotFoundException {

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            databaseConnectionInfo.connectionUrl(), new MultiUseUserCredentials("sa", ""));

    final Connection connection = connectionSource.get();

    assertThat(connection, is(not(nullValue())));
  }
}
