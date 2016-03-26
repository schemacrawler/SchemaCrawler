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

package schemacrawler.schema;


import java.sql.DatabaseMetaData;

/**
 * An enumeration wrapper around JDBC function types.
 */
public enum FunctionReturnType
  implements RoutineReturnType
{

 /**
  * Result unknown.
  */
 unknown(DatabaseMetaData.functionResultUnknown, "result unknown"),
 /**
  * Does not return a table.
  */
 noTable(DatabaseMetaData.functionNoTable, "does not return a table"),
 /**
  * Returns a table.
  */
 returnsTable(DatabaseMetaData.functionReturnsTable, "returns table");

  private final int id;
  private final String text;

  private FunctionReturnType(final int id, final String text)
  {
    this.id = id;
    this.text = text;
  }

  /**
   * Gets the id.
   *
   * @return id
   */
  @Override
  public int getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return text;
  }

}
