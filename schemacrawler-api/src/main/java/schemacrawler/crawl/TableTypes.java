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
package schemacrawler.crawl;


import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class TableTypes
{

  private static final Logger LOGGER = Logger.getLogger(TableTypes.class
                                                        .getName());

  private final Collection<String> tableTypes;

  TableTypes(final Connection connection)
  {
    tableTypes = new HashSet<>();

    if (connection != null)
    {
      try
      {
        final ResultSet tableTypesResults = connection.getMetaData()
            .getTableTypes();
        final List<String> tableTypesList = RetrieverUtility
            .readResultsVector(tableTypesResults);
        tableTypes.addAll(tableTypesList);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING,
                   "Could not obtain table types from connection",
                   e);
      }
    }
  }

  @Override
  public String toString()
  {
    return tableTypes.toString();
  }

  /**
   * Converts an array of table types to an array of their corresponding
   * string values.
   *
   * @param tableTypes
   *        Array of table types
   * @return Array of string table types
   */
  String[] filterUnknown(final Collection<String> tableTypes)
  {
    if (tableTypes == null)
    {
      return null;
    }
    if (tableTypes.isEmpty())
    {
      return new String[0];
    }

    final List<String> filteredTableTypes = new ArrayList<>();
    for (final String tableType: tableTypes)
    {
      if (isKnownTableType(tableType))
      {
        filteredTableTypes.add(tableType);
      }
    }
    Collections.sort(filteredTableTypes);
    return filteredTableTypes.toArray(new String[filteredTableTypes.size()]);
  }

  boolean isKnownTableType(final String testTableType)
  {
    if (isBlank(testTableType))
    {
      return false;
    }
    for (final String tableType: tableTypes)
    {
      if (tableType.equalsIgnoreCase(testTableType))
      {
        return true;
      }
    }
    return false;
  }

}
