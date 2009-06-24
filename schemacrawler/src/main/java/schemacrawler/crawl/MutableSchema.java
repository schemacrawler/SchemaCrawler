/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.Utility;

/**
 * Represents the database schema.
 * 
 * @author Sualeh Fatehi
 */
class MutableSchema
  extends AbstractDependantNamedObject
  implements Schema
{

  private static final long serialVersionUID = 3258128063743931187L;

  private final ColumnDataTypes columnDataTypes = new ColumnDataTypes();
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.alphabetical);
  private final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.alphabetical);

  MutableSchema(final AbstractNamedObject parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getColumnDataType(java.lang.String)
   */
  public MutableColumnDataType getColumnDataType(final String name)
  {
    return columnDataTypes.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataTypes()
   */
  public ColumnDataType[] getColumnDataTypes()
  {
    return columnDataTypes.values().toArray(new ColumnDataType[columnDataTypes
      .size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getFullName()
   */
  public String getFullName()
  {
    final StringBuilder buffer = new StringBuilder();
    final NamedObject catalog = getParent();
    if (catalog != null)
    {
      final String catalogName = catalog.getName();
      if (!Utility.isBlank(catalogName))
      {
        buffer.append(catalogName).append(".");
      }
    }
    if (getName() != null)
    {
      buffer.append(getName());
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedure(java.lang.String)
   */
  public MutableProcedure getProcedure(final String name)
  {
    return procedures.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  public Procedure[] getProcedures()
  {
    return procedures.values().toArray(new Procedure[procedures.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTable(java.lang.String)
   */
  public MutableTable getTable(final String name)
  {
    return tables.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTables()
   */
  public Table[] getTables()
  {
    return tables.values().toArray(new Table[tables.size()]);
  }

  void addColumnDataType(final MutableColumnDataType columnDataType)
  {
    if (columnDataType != null)
    {
      columnDataTypes.add(columnDataType);
    }
  }

  void addProcedure(final MutableProcedure procedure)
  {
    procedures.add(procedure);
  }

  void addTable(final MutableTable table)
  {
    tables.add(table);
  }

  MutableColumnDataType lookupByType(final int type)
  {
    MutableColumnDataType columnDataType = null;
    final MutableColumnDataType[] allColumnDataTypes = columnDataTypes.values()
      .toArray(new MutableColumnDataType[columnDataTypes.size()]);
    for (final MutableColumnDataType currentColumnDataType: allColumnDataTypes)
    {
      if (type == currentColumnDataType.getType())
      {
        columnDataType = currentColumnDataType;
        break;
      }
    }
    return columnDataType;
  }

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    return columnDataTypes.lookupColumnDataTypeByType(type);
  }

  MutableColumnDataType lookupColumnDataTypeByType(final String databaseSpecificTypeName)
  {
    return columnDataTypes.lookupColumnDataTypeByType(databaseSpecificTypeName);
  }

  void removeProcedure(final String procedureName)
  {
    procedures.remove(procedureName);
  }

  void removeTable(final String tableName)
  {
    tables.remove(tableName);
  }

}
