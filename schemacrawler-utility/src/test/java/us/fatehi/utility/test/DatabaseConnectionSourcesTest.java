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
package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.SingleUseUserCredentials;

public class DatabaseConnectionSourcesTest {

  @Test
  public void test() throws SQLException {

    final EmbeddedDatabase db =
        new EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setScriptEncoding("UTF-8")
            .ignoreFailedDrops(true)
            .addScript("testdb.sql")
            .build();

    final Connection wrappedConnection = db.getConnection();
    final DatabaseMetaData metaData = wrappedConnection.getMetaData();
    final String connectionUrl = metaData.getURL();
    final String userName = metaData.getUserName();
    final String password = "";

    final DatabaseConnectionSource databaseConnectionSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            connectionUrl, new SingleUseUserCredentials(userName, password));

    assertThat(databaseConnectionSource.get(), is(not(nullValue())));
  }
}
