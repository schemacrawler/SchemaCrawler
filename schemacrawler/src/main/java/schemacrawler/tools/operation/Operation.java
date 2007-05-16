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

package schemacrawler.tools.operation;


/**
 * Database operations.
 */
public enum Operation
{

  /** Count operation */
  count("Row Count", "SELECT COUNT(*) FROM ${table}",
    "{0,choice,0#empty|0<{0,number,integer} rows}"),
  /** Drop operation */
  drop("Drop Table", "DROP ${tabletype} ${table}", "dropped"),
  /** Truncate operation */
  truncate("Truncate Table", "DELETE FROM ${table}",
    "truncated; {0,choice,0#was already empty|0<had {0,number,integer} rows}"),
  /** Dump operation */
  dump("Dump", "SELECT ${columns} FROM ${table} ORDER BY ${columns}", ""),
  /** Query-over operation */
  queryover("", "Query Over Table", "{0,choice,0#-|0<{0,number,integer}}");

  private final String description;
  private final String query;
  private final String countMessageFormat;

  private Operation(final String description,
                    final String query,
                    final String countMessageFormat)
  {
    this.description = description;
    this.query = query;
    this.countMessageFormat = countMessageFormat;
  }

  /**
   * Message format for the counts.
   * 
   * @return Message format for the counts
   */
  public String getCountMessageFormat()
  {
    return countMessageFormat;
  }

  /**
   * Operation description.
   * 
   * @return Operation description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Query.
   * 
   * @return Query
   */
  public String getQuery()
  {
    return query;
  }

  /**
   * If this operation is a select operation.
   * 
   * @return If this operation is a select operation
   */
  public boolean isSelectOperation()
  {
    return this == queryover || this == dump || this == count;
  }

}
