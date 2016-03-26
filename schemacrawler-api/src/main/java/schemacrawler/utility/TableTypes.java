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
package schemacrawler.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.readResultsVector;
import static sf.util.Utility.filterOutBlank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.TableType;

/**
 * Represents a collection of tables types for a database system, as
 * returned by the database server itself. A live database connection is
 * required to obtain this information. The case of the table type name
 * is preserved, though look-ups are case-insensitive.
 */
public final class TableTypes
{

  private static final Logger LOGGER = Logger
    .getLogger(TableTypes.class.getName());

  private final Collection<TableType> tableTypes;

  /**
   * Obtain a collection of tables types for a database system, as
   * returned by the database server itself.
   */
  public TableTypes(final Connection connection)
  {
    requireNonNull(connection, "No connection provided");

    tableTypes = new HashSet<>();
    try (final ResultSet tableTypesResults = connection.getMetaData()
      .getTableTypes();)
    {
      readResultsVector(tableTypesResults).stream().filter(filterOutBlank)
        .forEach(tableTypeString -> tableTypes
          .add(new TableType(tableTypeString)));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not obtain table types from connection",
                 e);
    }
  }

  /**
   * Filters table types not known to the database system. Returns
   * values in the same case as known to the database system, even
   * though the search (that is, values in the input collection) is
   * case-insensitive.
   *
   * @param tableTypeStrings
   *        Can be null, which indicates return all table types, or an
   *        empty array, which indicates return no table types.
   * @return Returns values in the same case as known to the database
   *         system.
   */
  public String[] filterUnknown(final Collection<String> tableTypeStrings)
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
      final Optional<TableType> tableType = lookupTableType(tableTypeString);
      if (tableType.isPresent())
      {
        // Add value in the same case as known to the database system
        filteredTableTypes.add(tableType.get().getTableType());
      }
    }
    Collections.sort(filteredTableTypes);
    return filteredTableTypes.toArray(new String[filteredTableTypes.size()]);
  }

  /**
   * Looks up a table type, from the provided string. The search (that
   * is, value of the provided string) is case-insensitive.
   *
   * @return Matched table type
   */
  public Optional<TableType> lookupTableType(final String tableTypeString)
  {
    return tableTypes.stream()
      .filter(tableType -> tableType.isEqualTo(tableTypeString)).findAny();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return tableTypes.toString();
  }

}
