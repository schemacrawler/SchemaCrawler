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

package us.fatehi.utility.datasource;

import static us.fatehi.utility.database.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

@Deprecated
final class WrappedDatabaseConnectionSource extends AbstractDatabaseConnectionSource {

  private final Connection connection;

  public WrappedDatabaseConnectionSource(
      final Connection connection, final Consumer<Connection> connectionInitializer) {
    try {
      this.connection = checkConnection(connection);
      setConnectionInitializer(connectionInitializer);
      this.connectionInitializer.accept(connection);
    } catch (final SQLException e) {
      throw new RuntimeException("Could not wrap provided database connection", e);
    }
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }

  @Override
  protected void finalize() throws Throwable {
    // Assert that all connections are closed
    if (!connection.isClosed()) {
      throw new RuntimeException("Connection pool is not closed");
    }
    super.finalize();
  }
}
