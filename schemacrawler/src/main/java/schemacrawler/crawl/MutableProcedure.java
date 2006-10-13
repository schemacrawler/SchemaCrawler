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

package schemacrawler.crawl;


import java.util.List;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureType;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.util.NaturalSortComparator;
import schemacrawler.util.SerializableComparator;

/**
 * Represents a database procedure. Created from metadata returned by a
 * JDBC call.
 * 
 * @author sfatehi
 * @version 0.1
 */
final class MutableProcedure
  extends AbstractDatabaseObject
  implements Procedure
{

  private static final long serialVersionUID = 3906925686089134130L;

  private ProcedureType procedureType;
  private final NamedObjectList columns = new NamedObjectList(
      new NaturalSortComparator());
  private RoutineBodyType routineBodyType;
  private String definition;

  /**
   * Sets the procedure type.
   * 
   * @param type
   *        Procedure type.
   */
  void setType(final ProcedureType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null procedure type");
    }
    procedureType = type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getType()
   */
  public ProcedureType getType()
  {
    return procedureType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getColumns()
   */
  public ProcedureColumn[] getColumns()
  {
    final List allColumns = columns.getAll();
    return (ProcedureColumn[]) allColumns
        .toArray(new ProcedureColumn[allColumns.size()]);
  }

  /**
   * Adds a column.
   * 
   * @param column
   *        Column
   */
  void addColumn(final ProcedureColumn column)
  {
    columns.add(column);
  }

  void setColumnComparator(final SerializableComparator comparator)
  {
    columns.setComparator(comparator);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getRoutineBodyType()
   */
  public RoutineBodyType getRoutineBodyType()
  {
    return routineBodyType;
  }

  void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getDefinition()
   */
  public String getDefinition()
  {
    return definition;
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

}
