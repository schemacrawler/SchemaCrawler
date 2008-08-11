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
