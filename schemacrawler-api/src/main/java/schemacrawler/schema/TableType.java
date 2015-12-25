/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import java.io.Serializable;

/**
 * Represents a type of table in the database. Examples could be a base
 * table, a view, a global temporary table, and so on. The table type
 * name is case-sensitive, as it is known to the database system.
 * However, string comparisons are case-insensitive.
 */
public final class TableType
  implements Serializable, Comparable<TableType>
{

  private static final long serialVersionUID = -8172248482959041873L;

  public static final TableType UNKNOWN = new TableType();

  private final String tableType;

  /**
   * Constructor for table type. Table type is case-sensitive.
   */
  public TableType(final String tableTypeString)
  {
    if (isBlank(tableTypeString))
    {
      throw new IllegalArgumentException("No table type provided");
    }
    tableType = tableTypeString.trim();
  }

  private TableType()
  {
    tableType = null;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final TableType other)
  {
    if (other == null)
    {
      return 1;
    }
    final String thisToString = toString();
    final int compareTo = thisToString.compareTo(other.toString());
    if (compareTo != 0 && "TABLE".equalsIgnoreCase(thisToString))
    {
      return -1;
    }
    else
    {
      return compareTo;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
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
    if (!(obj instanceof TableType))
    {
      return false;
    }
    final TableType other = (TableType) obj;
    if (tableType == null)
    {
      if (other.tableType != null)
      {
        return false;
      }
    }
    else if (!tableType.equalsIgnoreCase(other.tableType))
    {
      return false;
    }
    return true;
  }

  /**
   * The table type, with the case preserved.
   *
   * @return The table type
   */
  public String getTableType()
  {
    return tableType;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (tableType == null? 0: tableType.hashCode());
    return result;
  }

  /**
   * Checks if a string is equal to this table type. This is a
   * case-insensitive check.
   *
   * @return True if the string is the same as this table type
   */
  public boolean isEqualTo(final String testTableType)
  {
    if (isBlank(testTableType))
    {
      return false;
    }
    return tableType.equalsIgnoreCase(testTableType.trim());
  }

  /**
   * Checks if the table type is a view of any kind.
   */
  public boolean isView()
  {
    return tableType != null && tableType.toUpperCase().contains("VIEW");
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return tableType == null? "": tableType.toLowerCase();
  }

}
