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

package schemacrawler.schema;

import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.database.DatabaseUtility.readResultsVector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.string.StringFormat;

/**
 * Represents a collection of tables types for a database system, as returned by the database server
 * itself. A live database connection is required to obtain this information. The case of the table
 * type name is preserved, though look-ups are case-insensitive.
 */
public final class TableTypes implements Iterable<TableType> {

  private static final Logger LOGGER = Logger.getLogger(TableTypes.class.getName());

  /** Obtain a collection of tables types from provided list. */
  public static TableTypes from(final Collection<String> tableTypeStrings) {
    final Collection<TableType> tableTypes;
    if (tableTypeStrings == null) {
      return new TableTypes(null);
    }

    tableTypes = new HashSet<>();
    for (final String tableTypeString : tableTypeStrings) {
      if (!isBlank(tableTypeString)) {
        tableTypes.add(new TableType(tableTypeString));
      }
    }
    return new TableTypes(tableTypes);
  }

  /**
   * Obtain a collection of tables types for a database system, as returned by the database server
   * itself.
   */
  public static TableTypes from(final Connection connection) {
    requireNonNull(connection, "No connection provided");

    final Collection<TableType> tableTypes = new HashSet<>();
    try (final ResultSet tableTypesResults = connection.getMetaData().getTableTypes()) {
      final List<String> tableTypeStrings = readResultsVector(tableTypesResults);
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Supported table types from database driver: %s", tableTypeStrings));
      for (final String tableTypeString : tableTypeStrings) {
        if (!isBlank(tableTypeString)) {
          tableTypes.add(new TableType(tableTypeString));
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not obtain table types from connection", e);
    }
    return new TableTypes(tableTypes);
  }

  public static TableTypes from(final String... tableTypeStrings) {
    if (tableTypeStrings == null) {
      return new TableTypes(null);
    }
    return TableTypes.from(Arrays.asList(tableTypeStrings));
  }

  public static TableTypes from(final String tableTypesString) {
    if (tableTypesString == null) {
      return new TableTypes(null);
    }
    final String[] tableTypeStrings = tableTypesString.split(",");
    return TableTypes.from(tableTypeStrings);
  }

  public static TableTypes includeAll() {
    return from((Collection<String>) null);
  }

  public static TableTypes includeNone() {
    return from("");
  }

  private final List<TableType> tableTypes;

  private TableTypes(final Collection<TableType> tableTypesCollection) {
    if (tableTypesCollection == null) {
      tableTypes = null;
    } else {
      tableTypes = new ArrayList<>(tableTypesCollection);
      Collections.sort(tableTypes);
    }
  }

  public boolean isIncludeAll() {
    return tableTypes == null;
  }

  public boolean isIncludeNone() {
    return tableTypes.isEmpty();
  }

  @Override
  public Iterator<TableType> iterator() {
    if (isIncludeAll()) {
      throw new IllegalArgumentException("Include all table types, but none are known");
    }
    return tableTypes.iterator();
  }

  /**
   * Looks up a table type, from the provided string. The search (that is, value of the provided
   * string) is case-insensitive.
   *
   * @return Matched table type
   */
  public Optional<TableType> lookupTableType(final String tableTypeString) {
    if (isIncludeAll()) {
      return Optional.of(new TableType(tableTypeString));
    }

    for (final TableType tableType : tableTypes) {
      if (tableType.isEqualTo(tableTypeString)) {
        return Optional.of(tableType);
      }
    }
    return Optional.empty();
  }

  /**
   * Filters table types not known to the database system. Returns values in the same case as known
   * to the database system, even though the search (that is, values in the input collection) is
   * case-insensitive.
   *
   * @param tableTypesKeepList Table types to compare to, and use as a keep list.
   * @return Returns values in the same case as known to the database system.
   */
  public TableTypes subsetFrom(final TableTypes tableTypesKeepList) {
    requireNonNull(tableTypesKeepList, "No keep list of table types provided");
    if (tableTypesKeepList.isIncludeAll()) {
      return this;
    }
    if (tableTypesKeepList.isIncludeNone()) {
      return new TableTypes(null);
    }

    final List<TableType> filteredTableTypes = new ArrayList<>();
    for (final TableType tableType : tableTypesKeepList) {
      if (this.tableTypes.contains(tableType)) {
        // Add value in the same case as known to the database system
        final int index = this.tableTypes.indexOf(tableType);
        filteredTableTypes.add(this.tableTypes.get(index));
      }
    }
    filteredTableTypes.sort(naturalOrder());
    return new TableTypes(filteredTableTypes);
  }

  public String[] toArray() {
    if (isIncludeAll()) {
      return null;
    }
    return tableTypes.stream().map(TableType::getTableType).toArray(String[]::new);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (isIncludeAll()) {
      return "<all table types>";
    }
    return tableTypes.toString();
  }
}
