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
package schemacrawler.crawl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import schemacrawler.schema.TableType;

class TableTypes
{

  private static final Logger LOGGER = Logger.getLogger(TableTypes.class
    .getName());

  private final Collection<TableType> tableTypes;

  TableTypes(final Connection connection)
  {
    tableTypes = new HashSet<>();

    if (connection != null)
    {
      try (final ResultSet tableTypesResults = connection.getMetaData()
        .getTableTypes();)
      {
        final List<String> tableTypesList = RetrieverUtility
          .readResultsVector(tableTypesResults);
        tableTypes.addAll(tableTypesList.stream()
          .map(tableType -> new TableType(tableType))
          .collect(Collectors.toSet()));
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
   * @param tableTypeStrings
   *        Array of table types
   * @return Array of string table types
   */
  String[] filterUnknown(final Collection<String> tableTypeStrings)
  {
    if (tableTypeStrings == null)
    {
      return null;
    }
    if (tableTypeStrings.isEmpty())
    {
      return new String[0];
    }

    final List<String> filteredTableTypes = new ArrayList<>();
    for (final String tableTypeString: tableTypeStrings)
    {
      final TableType tableType = lookupTableType(tableTypeString);
      if (!tableType.equals(TableType.UNKNOWN))
      {
        filteredTableTypes.add(tableType.getTableType());
      }
    }
    Collections.sort(filteredTableTypes);
    return filteredTableTypes.toArray(new String[filteredTableTypes.size()]);
  }

  /**
   * Looks up a table type, from the provided string. Returns unknown if
   * no match is found.
   *
   * @return Matched table type, or unknown
   */
  TableType lookupTableType(final String tableTypeString)
  {
    return tableTypes.stream()
      .filter(tableType -> tableType.isEqualTo(tableTypeString)).findAny()
      .orElse(TableType.UNKNOWN);
  }

}
