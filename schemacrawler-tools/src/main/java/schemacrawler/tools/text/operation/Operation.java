/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.text.MessageFormat;

/**
 * Database operations.
 */
public enum Operation
{

  /**
   * Count operation
   */
  count("Row Count", "SELECT COUNT(*) FROM ${table}",
    "{0,choice,0#empty|0<{0,number,integer} rows}"),
  /**
   * Dump operation
   */
  dump("Dump", "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}", ""), ;

  private final String description;
  private final String queryString;
  private final String countMessageFormat;

  private Operation(final String description,
                    final String queryString,
                    final String countMessageFormat)
  {
    this.description = description;
    this.queryString = queryString;
    this.countMessageFormat = countMessageFormat;
  }

  /**
   * Message format for the counts.
   * 
   * @param number
   *        Number value in the message
   * @return Message format for the counts
   */
  String getCountMessage(final Number number)
  {
    return MessageFormat.format(countMessageFormat, number);
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
