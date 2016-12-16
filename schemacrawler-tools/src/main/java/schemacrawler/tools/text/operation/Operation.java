/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
