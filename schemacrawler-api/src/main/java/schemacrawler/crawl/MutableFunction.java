/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionColumn;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

/**
 * Represents a database function. Created from metadata returned by a
 * JDBC call.
 * 
 * @author Sualeh Fatehi
 */
final class MutableFunction
  extends MutableRoutine
  implements Function
{

  private static final long serialVersionUID = 3906925686089134130L;

  private FunctionReturnType returnType;
  private final NamedObjectList<MutableFunctionColumn> columns = new NamedObjectList<MutableFunctionColumn>();
  private RoutineBodyType routineBodyType;

  MutableFunction(final Schema schema, final String name)
  {
    super(schema, name);
    // Default values
    returnType = FunctionReturnType.unknown;
    routineBodyType = RoutineBodyType.unknown;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Function#getColumn(java.lang.String)
   */
  @Override
  public MutableFunctionColumn getColumn(final String name)
  {
    return columns.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Function#getColumns()
   */
  @Override
  public List<FunctionColumn> getColumns()
  {
    return new ArrayList<FunctionColumn>(columns.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see Function#getReturnType()
   */
  @Override
  public FunctionReturnType getReturnType()
  {
    return returnType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Function#getRoutineBodyType()
   */
  @Override
  public RoutineBodyType getRoutineBodyType()
  {
    return routineBodyType;
  }

  @Override
  public RoutineType getRoutineType()
  {
    return RoutineType.function;
  }

  void addColumn(final MutableFunctionColumn column)
  {
    columns.add(column);
  }

  void setReturnType(final FunctionReturnType returnType)
  {
    if (returnType == null)
    {
      throw new IllegalArgumentException("Null function return type");
    }
    this.returnType = returnType;
  }

  @Override
  void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

}
