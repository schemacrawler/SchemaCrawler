package schemacrawler.crawl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ColumnDataTypes
{

  private final Map<String, Set<MutableColumnDataType>> columnDataTypes = new HashMap<String, Set<MutableColumnDataType>>();

  void addColumnDataType(String schemaName,
                         final MutableColumnDataType systemColumnDataType)
  {
    Set<MutableColumnDataType> columnDataTypesList = columnDataTypes
      .get(schemaName);
    if (columnDataTypesList == null)
    {
      columnDataTypesList = new HashSet<MutableColumnDataType>();
      columnDataTypes.put(schemaName, columnDataTypesList);
    }
    columnDataTypesList.add(systemColumnDataType);
  }

  Set<MutableColumnDataType> lookupColumnDataTypes(String schemaName)
  {
    return columnDataTypes.get(schemaName);
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name.
   * 
   * @param jdbcDataType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   */
  void lookupAndSetDataType(final AbstractColumn column,
                            final int jdbcDataType,
                            final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = lookupColumnDataTypeByType(databaseSpecificTypeName);
    if (columnDataType == null)
    {
      String catalogName = column.getCatalogName();
      String schemaName = column.getSchemaName();
      columnDataType = new MutableColumnDataType(catalogName,
                                                 schemaName,
                                                 databaseSpecificTypeName);
      columnDataType.setType(jdbcDataType);
      addColumnDataType(schemaName, columnDataType);
    }
    column.setType(columnDataType);
  }

  MutableColumnDataType lookupColumnDataTypeByType(final String databaseSpecificTypeName)
  {
    final Set<MutableColumnDataType> allColumnDataTypes = getAllColumnDataTypes();

    MutableColumnDataType columnDataType = null;
    for (final MutableColumnDataType currentColumnDataType: allColumnDataTypes)
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

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    final Set<MutableColumnDataType> allColumnDataTypes = getAllColumnDataTypes();

    MutableColumnDataType columnDataType = null;
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

  Set<MutableColumnDataType> getAllColumnDataTypes()
  {
    final Set<MutableColumnDataType> allColumnDataTypesList = new HashSet<MutableColumnDataType>();
    for (final Set<MutableColumnDataType> columnDataTypesList: columnDataTypes
      .values())
    {
      allColumnDataTypesList.addAll(columnDataTypesList);
    }
    return allColumnDataTypesList;
  }

}
