/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.schema;


import java.util.ArrayList;
import java.util.List;

/**
 * An enumeration wrapper around JDBC table types.
 */
public enum TableType
{

  /** Unknown */
  UNKNOWN("UNKNOWN"),
  /** Table */
  TABLE("TABLE"),
  /** View */
  VIEW("VIEW"),
  /** System table */
  SYSTEM_TABLE("SYSTEM_TABLE"),
  /** Global temporary */
  GLOBAL_TEMPORARY("GLOBAL_TEMPORARY"),
  /** Local temporary */
  LOCAL_TEMPORARY("LOCAL_TEMPORARY"),
  /** Alias */
  ALIAS("ALIAS"),
  /** Synonym */
  SYNONYM("SYNONYM");

  /**
   * Converts an array of table types to an array of their corresponding
   * string values.
   * 
   * @param tableTypes
   *        Array of table types
   * @return Array of string table types
   */
  public static String[] toStringArray(final TableType[] tableTypes)
  {

    if (tableTypes == null)
    {
      return new String[0];
    }

    final List tableTypeStrings = new ArrayList(tableTypes.length);
    for (final TableType tableType: tableTypes)
    {
      if (tableType != null)
      {
        tableTypeStrings.add(tableType.toString());
      }
    }
    return (String[]) tableTypeStrings.toArray(new String[tableTypeStrings
      .size()]);
  }

  /**
   * Converts an array of string table types to an array of their
   * corresponding enumeration values.
   * 
   * @param tableTypeStrings
   *        Array of string table types
   * @return Array of table types
   */
  public static TableType[] valueOf(final String[] tableTypeStrings)
  {

    if (tableTypeStrings == null || tableTypeStrings.length == 0)
    {
      return new TableType[0];
    }

    final List tableTypes = new ArrayList(tableTypeStrings.length);
    for (final String tableTypeString: tableTypeStrings)
    {
      tableTypes.add(valueOf(tableTypeString));
    }
    return (TableType[]) tableTypes.toArray(new TableType[tableTypes.size()]);
  }

  private final String name;

  private TableType(final String tableType)
  {
    name = tableType;
  }

  /**
   * Returns whether this table type represents a table.
   * 
   * @return Whether this table type represents a table
   */
  public boolean isTable()
  {
    return name.equals(TABLE.name);
  }

  /**
   * Returns whether this table type represents a view.
   * 
   * @return Whether this table type represents a view
   */
  public boolean isView()
  {
    return name.equals(VIEW.name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return name;
  }
}
