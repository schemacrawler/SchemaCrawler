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


import java.util.HashSet;
import java.util.Set;

import schemacrawler.schema.BaseColumn;

class ColumnDataTypes
  extends NamedObjectList<MutableColumnDataType>
{

  private static final long serialVersionUID = 6793135093651666453L;

  ColumnDataTypes()
  {
    super(NamedObjectSort.alphabetical);
  }

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    MutableColumnDataType columnDataType = null;
    for (final MutableColumnDataType currentColumnDataType: this)
    {
      if (type == currentColumnDataType.getType())
      {
        columnDataType = currentColumnDataType;
        break;
      }
    }
    return columnDataType;
  }

  MutableColumnDataType lookupColumnDataTypeByType(final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = null;
    for (final MutableColumnDataType currentColumnDataType: this)
    {
      if (currentColumnDataType.getDatabaseSpecificTypeName()
        .equals(databaseSpecificTypeName))
      {
        columnDataType = currentColumnDataType;
        break;
      }
    }
    return columnDataType;
  }

  Set<MutableColumnDataType> lookupColumnDataTypes(final String schemaName)
  {
    final Set<MutableColumnDataType> columnDataTypes = new HashSet<MutableColumnDataType>();
    for (final MutableColumnDataType currentColumnDataType: this)
    {
      String dataTypeSchemaName = currentColumnDataType.getSchemaName();
      if (dataTypeSchemaName == null? schemaName == null: dataTypeSchemaName
        .equals(schemaName))
      {
        columnDataTypes.add(currentColumnDataType);
      }
    }
    return columnDataTypes;
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
  MutableColumnDataType lookupOrCreateColumnDataType(final BaseColumn column,
                                                     final int jdbcDataType,
                                                     final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = lookupColumnDataTypeByType(databaseSpecificTypeName);
    if (columnDataType == null)
    {
      final String catalogName = column.getCatalogName();
      final String schemaName = column.getSchemaName();
      columnDataType = new MutableColumnDataType(catalogName,
                                                 schemaName,
                                                 databaseSpecificTypeName);
      columnDataType.setType(jdbcDataType);
      add(columnDataType);
    }
    return columnDataType;
  }

}
