/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.crawl;


import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionColumn;
import schemacrawler.schema.FunctionColumnType;

/**
 * Represents a column in a database function. Created from metadata
 * returned by a JDBC call.
 *
 * @author Sualeh Fatehi
 */
final class MutableFunctionColumn
  extends MutableRoutineColumn<Function>
  implements FunctionColumn
{

  private static final long serialVersionUID = 3546361725629772857L;

  private FunctionColumnType functionColumnType;

  MutableFunctionColumn(final Function parent, final String name)
  {
    super(new FunctionReference(parent), name);
  }

  /**
   * {@inheritDoc}
   *
   * @see FunctionColumn#getColumnType()
   */
  @Override
  public FunctionColumnType getColumnType()
  {
    return functionColumnType;
  }

  void setFunctionColumnType(final FunctionColumnType functionColumnType)
  {
    this.functionColumnType = functionColumnType;
  }

}
