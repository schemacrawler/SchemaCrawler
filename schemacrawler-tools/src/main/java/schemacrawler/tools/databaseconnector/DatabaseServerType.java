/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.databaseconnector;


import static sf.util.Utility.isBlank;

import java.io.Serializable;

public final class DatabaseServerType
  implements Serializable
{

  private static final long serialVersionUID = 2160456864554076419L;

  public static final DatabaseServerType UNKNOWN = new DatabaseServerType();

  private final String databaseSystemIdentifier;
  private final String databaseSystemName;

  public DatabaseServerType(final String databaseSystemIdentifier,
                            final String databaseSystemName)
  {
    if (isBlank(databaseSystemIdentifier))
    {
      throw new IllegalArgumentException("No database system identifier provided");
    }
    this.databaseSystemIdentifier = databaseSystemIdentifier;

    if (isBlank(databaseSystemName))
    {
      throw new IllegalArgumentException("No database system name provided");
    }
    this.databaseSystemName = databaseSystemName;

  }

  private DatabaseServerType()
  {
    databaseSystemIdentifier = null;
    databaseSystemName = null;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final DatabaseServerType other = (DatabaseServerType) obj;
    if (databaseSystemIdentifier == null)
    {
      if (other.databaseSystemIdentifier != null)
      {
        return false;
      }
    }
    else if (!databaseSystemIdentifier.equals(other.databaseSystemIdentifier))
    {
      return false;
    }
    return true;
  }

  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
  }

  public String getDatabaseSystemName()
  {
    return databaseSystemName;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (databaseSystemIdentifier == null? 0: databaseSystemIdentifier
               .hashCode());
    return result;
  }

  public boolean isUnknownDatabaseSystem()
  {
    return isBlank(databaseSystemIdentifier);
  }

  @Override
  public String toString()
  {
    if (isUnknownDatabaseSystem())
    {
      return "";
    }
    else
    {
      return String.format("%s - %s",
                           databaseSystemIdentifier,
                           databaseSystemName);
    }
  }

}
