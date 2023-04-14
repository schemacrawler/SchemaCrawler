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
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.ConnectionInfo;

/** Connection information. */
final class ImmutableConnectionInfo implements ConnectionInfo {

  private static final long serialVersionUID = 6996782514772490150L;

  private final String databaseProductName;

  private final String databaseProductVersion;

  private final String connectionUrl;

  private final String userName;

  private final int jdbcMajorVersion;

  private final int jdbcMinorVersion;
  private final ImmutableDriverInfo driverInfo;

  ImmutableConnectionInfo(
      final String databaseProductName,
      final String databaseProductVersion,
      final String connectionUrl,
      final String userName,
      final int jdbcMajorVersion,
      final int jdbcMinorVersion,
      final ImmutableDriverInfo driverInfo) {

    this.databaseProductName =
        requireNotBlank(databaseProductName, "No database product name provided");
    this.databaseProductVersion =
        requireNotBlank(databaseProductVersion, "No database product version provided");

    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");

    this.userName = userName;

    this.jdbcMajorVersion = jdbcMajorVersion;
    this.jdbcMinorVersion = jdbcMinorVersion;

    this.driverInfo = requireNonNull(driverInfo, "No database driver information provided");
  }

  /** {@inheritDoc} */
  @Override
  public String getConnectionUrl() {
    return connectionUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getDatabaseProductName() {
    return databaseProductName;
  }

  /** {@inheritDoc} */
  @Override
  public String getDatabaseProductVersion() {
    return databaseProductVersion;
  }

  @Override
  public String getDriverClassName() {
    return driverInfo.getDriverClassName();
  }

  public ImmutableDriverInfo getDriverInfo() {
    return driverInfo;
  }

  @Override
  public int getDriverMajorVersion() {
    return driverInfo.getDriverMajorVersion();
  }

  @Override
  public int getDriverMinorVersion() {
    return driverInfo.getDriverMinorVersion();
  }

  @Override
  public String getDriverName() {
    return driverInfo.getDriverName();
  }

  @Override
  public String getDriverVersion() {
    return driverInfo.getDriverVersion();
  }

  /** {@inheritDoc} */
  @Override
  public int getJdbcMajorVersion() {
    return jdbcMajorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public int getJdbcMinorVersion() {
    return jdbcMinorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public String getUserName() {
    return userName;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format(
        "Connected to %n%s %s%nusing JDBC driver %n%s%nwith %n\"%s\"",
        databaseProductName, databaseProductVersion, driverInfo, connectionUrl);
  }
}
