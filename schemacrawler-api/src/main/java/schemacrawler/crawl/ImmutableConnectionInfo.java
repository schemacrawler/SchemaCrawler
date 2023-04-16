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

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;

/** Connection information. */
final class ImmutableConnectionInfo implements ConnectionInfo {

  private static final long serialVersionUID = 6996782514772490150L;

  private final DatabaseInfo databaseInfo;
  private final JdbcDriverInfo jdbcDriverInfo;

  ImmutableConnectionInfo(final DatabaseInfo databaseInfo, final JdbcDriverInfo jdbcDriverInfo) {
    this.databaseInfo = requireNonNull(databaseInfo, "No database information provided");
    this.jdbcDriverInfo = requireNonNull(jdbcDriverInfo, "No database driver information provided");
  }

  @Override
  public DatabaseInfo getDatabaseInfo() {
    return databaseInfo;
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo() {
    return jdbcDriverInfo;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format(
        "Connected to %n%s%nusing JDBC driver %n%s%n", databaseInfo, jdbcDriverInfo);
  }
}
