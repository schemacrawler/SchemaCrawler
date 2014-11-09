/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
package schemacrawler.tools.options;


import static sf.util.Utility.isBlank;
import schemacrawler.schemacrawler.Options;

public final class DatabaseServerType
  implements Options
{

  private static final long serialVersionUID = 2160456864554076419L;

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

    this.databaseSystemName = databaseSystemName;
  }

  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
  }

  public String getDatabaseSystemName()
  {
    return databaseSystemName;
  }

  public boolean hasDatabaseSystemName()
  {
    return !isBlank(databaseSystemName);
  }

}
