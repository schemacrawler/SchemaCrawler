/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
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
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
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
            databaseConnectionInfo.getConnectionUrl(), new MultiUseUserCredentials("sa", ""));

    final Connection connection = connectionSource.get();

    assertThat(connection, is(not(nullValue())));
  }
}
