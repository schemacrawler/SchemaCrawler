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

package schemacrawler.schemacrawler;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serializable;

/**
 * Class that represents an id for SchemaCrawler plugin that allows for crawl customizations for a
 * particular database. The "server" id is used on the SchemaCrawler command-line. It also allows
 * for customizations for the behavior of a particular database driver.
 */
public final class DatabaseServerType implements Serializable, Comparable<DatabaseServerType> {

  private static final long serialVersionUID = 2160456864554076419L;

  public static final DatabaseServerType UNKNOWN = new DatabaseServerType();

  private final String databaseSystemIdentifier;
  private final String databaseSystemName;

  public DatabaseServerType(
      final String databaseSystemIdentifier, final String databaseSystemName) {
    this.databaseSystemIdentifier =
        requireNotBlank(databaseSystemIdentifier, "No database system identifier provided");
    this.databaseSystemName =
        requireNotBlank(databaseSystemName, "No database system name provided");
  }

  private DatabaseServerType() {
    databaseSystemIdentifier = null;
    databaseSystemName = null;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final DatabaseServerType other) {
    if (this == other) {
      return 0;
    } else if (other == null) {
      return -1;
    }

    final boolean thisUnknown = databaseSystemIdentifier == null;
    final boolean otherUnknown = other.databaseSystemIdentifier == null;
    if (otherUnknown && !thisUnknown) {
      return 1;
    } else if (!otherUnknown && thisUnknown) {
      return -1;
    } else {
      return databaseSystemIdentifier.compareTo(other.databaseSystemIdentifier);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DatabaseServerType other = (DatabaseServerType) obj;
    if (databaseSystemIdentifier == null) {
      return other.databaseSystemIdentifier == null;
    } else {
      return databaseSystemIdentifier.equals(other.databaseSystemIdentifier);
    }
  }

  public String getDatabaseSystemIdentifier() {
    return databaseSystemIdentifier;
  }

  public String getDatabaseSystemName() {
    return databaseSystemName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + (databaseSystemIdentifier == null ? 0 : databaseSystemIdentifier.hashCode());
    return result;
  }

  public boolean isUnknownDatabaseSystem() {
    return isBlank(databaseSystemIdentifier);
  }

  @Override
  public String toString() {
    if (isUnknownDatabaseSystem()) {
      return "";
    } else {
      return String.format("%s - %s", databaseSystemIdentifier, databaseSystemName);
    }
  }
}
