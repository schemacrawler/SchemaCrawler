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

package schemacrawler.tools.text.operation;


import schemacrawler.utility.Query;

/**
 * Database operations.
 */
public enum Operation
{

 /**
  * Count operation
  */
  count("Row Count", "SELECT COUNT(*) FROM ${table}"),
 /**
  * Dump operation
  */
  dump("Dump", "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}"),
 /**
  * Quick dump operation, where columns do not need to be retrieved
  * (minimum infolevel), but the order of rows may not be preserved from
  * run to run.
  */
  quickdump("Dump", "SELECT * FROM ${table}"),;

  private final String description;
  private final String queryString;

  private Operation(final String description, final String queryString)
  {
    this.description = description;
    this.queryString = queryString;
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
  public Query getQuery()
  {
    return new Query(name(), queryString);
  }

}
