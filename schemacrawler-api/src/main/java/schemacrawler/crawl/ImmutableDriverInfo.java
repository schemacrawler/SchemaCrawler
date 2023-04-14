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
import java.io.Serializable;

/** JDBC driver information. */
final class ImmutableDriverInfo implements Serializable {

  private static final long serialVersionUID = 4244451664320931213L;

  private final String driverName;
  private final String driverClassName;
  private final String driverVersion;
  private final int driverMajorVersion;
  private final int driverMinorVersion;

  ImmutableDriverInfo(
      final String driverClassName,
      final String driverName,
      final String driverVersion,
      final int driverMajorVersion,
      final int driverMinorVersion) {

    this.driverClassName =
        requireNonNull(driverClassName, "No database driver Java class name provided");
    this.driverName = requireNotBlank(driverName, "No database driver name provided");
    this.driverVersion = requireNotBlank(driverVersion, "No database driver version provided");
    this.driverMajorVersion = driverMajorVersion;
    this.driverMinorVersion = driverMinorVersion;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public int getDriverMajorVersion() {
    return driverMajorVersion;
  }

  public int getDriverMinorVersion() {
    return driverMinorVersion;
  }

  public String getDriverName() {
    return driverName;
  }

  public String getDriverVersion() {
    return driverVersion;
  }

  @Override
  public String toString() {
    return String.format(
        "Connected to %n%s %s %nusing JDBC driver %n<%s> %s %s%nwith %n\"%s\"",
        driverClassName, driverName, driverVersion);
  }
}
