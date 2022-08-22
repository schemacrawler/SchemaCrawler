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
  private final String driverName;
  private final String driverClassName;
  private final String driverVersion;
  private final int driverMajorVersion;
  private final int driverMinorVersion;
  private final int jdbcMajorVersion;
  private final int jdbcMinorVersion;

  ImmutableConnectionInfo(
      final String databaseProductName,
      final String databaseProductVersion,
      final String connectionUrl,
      final String userName,
      final String driverClassName,
      final String driverName,
      final String driverVersion,
      final int driverMajorVersion,
      final int driverMinorVersion,
      final int jdbcMajorVersion,
      final int jdbcMinorVersion) {

    this.databaseProductName =
        requireNotBlank(databaseProductName, "No database product name provided");
    this.databaseProductVersion =
        requireNotBlank(databaseProductVersion, "No database product version provided");

    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");

    this.userName = userName;

    this.driverClassName =
        requireNonNull(driverClassName, "No database driver Java class name provided");
    this.driverName = requireNotBlank(driverName, "No database driver name provided");
    this.driverVersion = requireNotBlank(driverVersion, "No database driver version provided");
    this.driverMajorVersion = driverMajorVersion;
    this.driverMinorVersion = driverMinorVersion;

    this.jdbcMajorVersion = jdbcMajorVersion;
    this.jdbcMinorVersion = jdbcMinorVersion;
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

  /** {@inheritDoc} */
  @Override
  public String getDriverClassName() {
    return driverClassName;
  }

  /** {@inheritDoc} */
  @Override
  public int getDriverMajorVersion() {
    return driverMajorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public int getDriverMinorVersion() {
    return driverMinorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public String getDriverName() {
    return driverName;
  }

  /** {@inheritDoc} */
  @Override
  public String getDriverVersion() {
    return driverVersion;
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
        "Connected to %n%s %s %nusing JDBC driver %n<%s> %s %s%nwith %n\"%s\"",
        databaseProductName,
        databaseProductVersion,
        driverClassName,
        driverName,
        driverVersion,
        connectionUrl);
  }
}
