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

package schemacrawler.crawl;


import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureType;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.Schema;

/**
 * Represents a database procedure. Created from metadata returned by a
 * JDBC call.
 * 
 * @author Sualeh Fatehi
 */
final class MutableProcedure
  extends AbstractDatabaseObject
  implements Procedure
{

  private static final long serialVersionUID = 3906925686089134130L;

  private ProcedureType procedureType;
  private final NamedObjectList<MutableProcedureColumn> columns = new NamedObjectList<MutableProcedureColumn>();
  private RoutineBodyType routineBodyType;
  private final StringBuilder definition;

  MutableProcedure(final Schema schema,
                   final String name,
                   final String quoteCharacter)
  {
    super(schema, name, quoteCharacter);
    // Default values
    procedureType = ProcedureType.unknown;
    routineBodyType = RoutineBodyType.unknown;
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Procedure#getColumn(java.lang.String)
   */
  public MutableProcedureColumn getColumn(final String name)
  {
    return columns.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getColumns()
   */
  public ProcedureColumn[] getColumns()
  {
    return columns.values().toArray(new ProcedureColumn[columns.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getDefinition()
   */
  public String getDefinition()
  {
    return definition.toString();
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

  void addColumn(final MutableProcedureColumn column)
  {
    columns.add(column);
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  void setColumnComparator(final NamedObjectSort comparator)
  {
    columns.setSortOrder(comparator);
  }

  void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

  void setType(final ProcedureType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null procedure type");
    }
    procedureType = type;
  }

}
