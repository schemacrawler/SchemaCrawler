/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

  void setType(final ProcedureType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null procedure type");
    }
    procedureType = type;
  }

}
