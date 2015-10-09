/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
