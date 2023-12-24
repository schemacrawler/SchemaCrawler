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

package schemacrawler.tools.offline.jdbc;

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import java.util.function.Consumer;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

final class OfflineDatabaseConnectionSource implements DatabaseConnectionSource {

  private final Connection connection;

  public OfflineDatabaseConnectionSource(final OfflineConnection connection) {
    this.connection = requireNonNull(connection, "No offline connection provided");
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    return connection;
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }

  @Override
  public void setFirstConnectionInitializer(final Consumer<Connection> connectionInitializer) {
    // No-op
  }
}
