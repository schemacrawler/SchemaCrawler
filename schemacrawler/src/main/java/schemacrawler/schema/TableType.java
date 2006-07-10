/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

/**
 * An enumeration wrapper around JDBC table types.
 */
public final class TableType
  implements EnumType
{

  private static final TableType UNKNOWN = new TableType("UNKNOWN");
  private static final TableType TABLE = new TableType("TABLE");
  private static final TableType VIEW = new TableType("VIEW");
  private static final TableType SYSTEM_TABLE = new TableType("SYSTEM_TABLE");
  private static final TableType GLOBAL_TEMPORARY = new TableType(
      "GLOBAL_TEMPORARY");
  private static final TableType LOCAL_TEMPORARY = new TableType(
      "LOCAL_TEMPORARY");
  private static final TableType ALIAS = new TableType("ALIAS");
  private static final TableType SYNONYM = new TableType("SYNONYM");

  private static final long serialVersionUID = 3546925783735220534L;

  private static final TableType[] ALL = {
    UNKNOWN,
    TABLE,
    VIEW,
    SYSTEM_TABLE,
    GLOBAL_TEMPORARY,
    LOCAL_TEMPORARY,
    ALIAS,
    SYNONYM,
  };

  private final int id;
  private final String name;

  private TableType(final String tableType)
  {
    ordinal = nextOrdinal++;
    id = ordinal;
    name = tableType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.EnumType#getId()
   */
  public int getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.EnumType#getName()
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns whether this table type represents a view.
   * 
   * @return Whether this table type represents a view
   */
  public boolean isView()
  {
    return id == VIEW.getId();
  }

  /**
   * Returns whether this table type represents a table.
   * 
   * @return Whether this table type represents a table
   */
  public boolean isTable()
  {
    return id == TABLE.getId();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return name;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param tableTypeString
   *          String value of table type
   * @return Enumeration value
   */
  public static TableType valueOf(final String tableTypeString)
  {

    TableType tableType = ALL[0];

    for (int i = 0; i < ALL.length; i++)
    {
      if (ALL[i].toString().equalsIgnoreCase(tableTypeString))
      {
        tableType = ALL[i];
        break;
      }
    }

    return tableType;

  }

  /**
   * Converts an array of string table types to an array of their corresponding
   * enumeration values.
   * 
   * @param tableTypeStrings
   *          Array of string table types
   * @return Array of table types
   */
  public static TableType[] valueOf(final String[] tableTypeStrings)
  {

    if (tableTypeStrings == null)
    {
      return new TableType[0];
    }

    final List tableTypes = new ArrayList(tableTypeStrings.length);
    for (int i = 0; i < tableTypeStrings.length; i++)
    {
      final String tableTypeString = tableTypeStrings[i];
      tableTypes.add(valueOf(tableTypeString));
    }

    return (TableType[]) tableTypes.toArray(new TableType[tableTypes.size()]);
  }

  /**
   * Converts an array of table types to an array of their corresponding string
   * values.
   * 
   * @param tableTypes
   *          Array of table types
   * @return Array of string table types
   */
  public static String[] toStringArray(final TableType[] tableTypes)
  {

    if (tableTypes == null)
    {
      return new String[0];
    }

    final List tableTypeStrings = new ArrayList(tableTypes.length);
    for (int i = 0; i < tableTypes.length; i++)
    {
      final TableType tableType = tableTypes[i];
      if (tableType != null)
      {
        tableTypeStrings.add(tableType.toString());
      }
    }

    return (String[]) tableTypeStrings.toArray(new String[tableTypeStrings
      .size()]);
  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final TableType[] VALUES = ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }
}
