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


import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * Represents the database schema.
 * 
 * @author Sualeh Fatehi
 */
class MutableSchema
  extends AbstractNamedObject
  implements Schema
{

  private static final long serialVersionUID = 3258128063743931187L;

  private final Catalog catalog;
  private final ColumnDataTypes columnDataTypes = new ColumnDataTypes();
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.alphabetical);
  private final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.alphabetical);

  MutableSchema(final Catalog catalog, final String name)
  {
    super(name);
    this.catalog = catalog;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    final MutableSchema other = (MutableSchema) obj;
    if (catalog == null)
    {
      if (other.catalog != null)
      {
        return false;
      }
    }
    else if (!catalog.equals(other.catalog))
    {
      return false;
    }
    if (getName() == null)
    {
      if (other.getName() != null)
      {
        return false;
      }
    }
    else if (!getName().equals(other.getName()))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getCatalog()
   */
  public Catalog getCatalog()
  {
    return catalog;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getColumnDataType(java.lang.String)
   */
  public ColumnDataType getColumnDataType(final String name)
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
    final String catalogName = catalog.getName();
    if (catalog != null && catalogName != null && catalogName.length() > 0)
    {
      buffer.append(catalogName).append(".");
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
  public Procedure getProcedure(final String name)
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
  public Table getTable(final String name)
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

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (catalog == null? 0: catalog.hashCode());
    result = prime * result + super.hashCode();
    return result;
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

  ColumnDataType lookupColumnDataTypeByType(final int type)
  {
    ColumnDataType columnDataType = columnDataTypes
      .lookupColumnDataTypeByType(type);
    if (columnDataType == null)
    {
      columnDataType = ((MutableDatabase) getCatalog().getDatabase())
        .getSystemColumnDataTypesList().lookupColumnDataTypeByType(type);
    }
    return columnDataType;
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name, if it does not exist.
   * 
   * @param jdbcDataType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   */
  ColumnDataType lookupOrCreateColumnDataType(final int jdbcDataType,
                                              final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = columnDataTypes
      .lookupColumnDataTypeByType(databaseSpecificTypeName);
    if (columnDataType == null)
    {
      columnDataType = ((MutableDatabase) getCatalog().getDatabase())
        .getSystemColumnDataTypesList()
        .lookupColumnDataTypeByType(databaseSpecificTypeName);
    }
    // Create new data type, if needed
    if (columnDataType == null)
    {
      if (columnDataType == null)
      {
        columnDataType = new MutableColumnDataType(this,
                                                   databaseSpecificTypeName);
        columnDataType.setType(jdbcDataType);
        columnDataTypes.add(columnDataType);
      }
    }
    return columnDataType;
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
