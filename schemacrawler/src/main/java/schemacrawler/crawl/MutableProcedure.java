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

package schemacrawler.crawl;


import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureType;
import schemacrawler.schema.RoutineBodyType;

/**
 * Represents a database procedure. Created from metadata returned by a
 * JDBC call.
 * 
 * @author Sualeh Fatehi
 * @version 0.1
 */
final class MutableProcedure
  extends AbstractDatabaseObject
  implements Procedure
{

  private static final long serialVersionUID = 3906925686089134130L;

  private ProcedureType procedureType;
  private final NamedObjectList<MutableProcedureColumn> columns = new NamedObjectList<MutableProcedureColumn>(NamedObjectSort.natural);
  private RoutineBodyType routineBodyType;
  private String definition;

  MutableProcedure(final String catalogName,
                   final String schemaName,
                   final String name)
  {
    super(catalogName, schemaName, name);
    // Default values
    procedureType = ProcedureType.unknown;
    routineBodyType = RoutineBodyType.unknown;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Procedure#getColumn(java.lang.String)
   */
  public ProcedureColumn getColumn(final String name)
  {
    return columns.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getColumns()
   */
  public ProcedureColumn[] getColumns()
  {
    return columns.getAll().toArray(new ProcedureColumn[columns.size()]);
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

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getRoutineBodyType()
   */
  public RoutineBodyType getRoutineBodyType()
  {
    return routineBodyType;
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
   * Adds a column.
   * 
   * @param column
   *        Column
   */
  void addColumn(final MutableProcedureColumn column)
  {
    columns.add(column);
  }

  void setColumnComparator(final NamedObjectSort comparator)
  {
    columns.setSortOrder(comparator);
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

  void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

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

}
