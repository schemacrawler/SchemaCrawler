/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ColumnDataType;

/**
 * Retriever of database metadata to get the details about the database
 * system.
 * 
 * @author Sualeh Fatehi
 */
final class DatabaseInfoRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseInfoRetriever.class.getName());

  DatabaseInfoRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Derives the property name from the method name.
   * 
   * @param method
   *        Method
   * @return Method name
   */
  private String derivePropertyName(final Method method)
  {
    final String get = "get";
    String name = method.getName();
    if (name.startsWith(get))
    {
      name = name.substring(get.length());
    }
    // Capitalize the first letter
    name = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    return name;
  }

  /**
   * Checks if a method is a result set method.
   * 
   * @param method
   * @return Whether a method is a result set method
   */
  private boolean isDatabasePropertiesResultSetMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean isPropertiesResultSetMethod = returnType
      .equals(ResultSet.class)
                                                && method.getParameterTypes().length == 0;
    return isPropertiesResultSetMethod;
  }

  /**
   * Checks if a method is a database property.
   * 
   * @param method
   * @return Whether method is a database property
   */
  private boolean isDatabasePropertyMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean notPropertyMethod = returnType.equals(ResultSet.class)
                                      || returnType.equals(Connection.class)
                                      || method.getParameterTypes().length > 0;
    return !notPropertyMethod;
  }

  /**
   * Checks if a method is a database property result set type.
   * 
   * @param method
   * @return Whether a method is a database property result set type
   */
  private boolean isDatabasePropertyResultSetType(final Method method)
  {
    final String[] databasePropertyResultSetTypes = new String[] {
        "deletesAreDetected",
        "insertsAreDetected",
        "updatesAreDetected",
        "othersDeletesAreVisible",
        "othersInsertsAreVisible",
        "othersUpdatesAreVisible",
        "ownDeletesAreVisible",
        "ownInsertsAreVisible",
        "ownUpdatesAreVisible",
        "supportsResultSetType"
    };
    final boolean isDatabasePropertyResultSetType = Arrays
      .binarySearch(databasePropertyResultSetTypes, method.getName()) >= 0;
    return isDatabasePropertyResultSetType;
  }

  /**
   * Reads a single column result set as a list.
   * 
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   */
  private List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<String>();
    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        values.add(value);
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

  /**
   * Provides additional information on the database.
   * 
   * @param dbInfo
   *        Database information to add to
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveAdditionalDatabaseInfo(final MutableDatabaseInfo dbInfo)
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getRetrieverConnection().getMetaData();
    final Method[] methods = DatabaseMetaData.class.getMethods();
    for (final Method method: methods)
    {
      try
      {
        if (isDatabasePropertyMethod(method))
        {
          final String name = derivePropertyName(method);
          Object value = method.invoke(dbMetaData, new Object[0]);
          if (value != null && name.endsWith("s") && value instanceof String)
          {
            // Probably a comma-separated list
            value = Collections.unmodifiableList(Arrays.asList(((String) value)
              .split(",")));
          }
          // Add to the properties map
          dbInfo.putProperty(name, value);
        }
        else if (isDatabasePropertiesResultSetMethod(method))
        {
          final String name = derivePropertyName(method);
          final ResultSet results = (ResultSet) method.invoke(dbMetaData,
                                                              new Object[0]);
          dbInfo.putProperty(name, readResultsVector(results));
        }
        else if (isDatabasePropertyResultSetType(method))
        {
          retrieveResultSetTypeProperty(dbMetaData,
                                        dbInfo,
                                        method,
                                        ResultSet.TYPE_FORWARD_ONLY,
                                        "TypeForwardOnly");
          retrieveResultSetTypeProperty(dbMetaData,
                                        dbInfo,
                                        method,
                                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        "TypeScrollInsensitive");
          retrieveResultSetTypeProperty(dbMetaData,
                                        dbInfo,
                                        method,
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        "TypeScrollSensitive");
        }
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.FINE, "Could not execute method, " + method, e);
      }
    }
  }

  /**
   * Provides information on the database.
   * 
   * @return Database information
   * @throws SQLException
   *         On a SQL exception
   */
  MutableDatabaseInfo retrieveDatabaseInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getRetrieverConnection().getMetaData();

    final MutableDatabaseInfo dbInfo = new MutableDatabaseInfo();
    dbInfo.setProductName(dbMetaData.getDatabaseProductName());
    dbInfo.setProductVersion(dbMetaData.getDatabaseProductVersion());
    return dbInfo;
  }

  private void retrieveResultSetTypeProperty(final DatabaseMetaData dbMetaData,
                                             final MutableDatabaseInfo dbInfo,
                                             final Method method,
                                             final int resultSetType,
                                             final String resultSetTypeName)
    throws IllegalAccessException, InvocationTargetException
  {
    final String name = derivePropertyName(method) + "ResultSet"
                        + resultSetTypeName;
    Boolean propertyValue = null;
    propertyValue = (Boolean) method.invoke(dbMetaData, new Object[] {
      Integer.valueOf(resultSetType)
    });
    dbInfo.putProperty(name, propertyValue);
  }

  /**
   * Retrieves column data type metadata.
   * 
   * @param columnDataTypes
   *        Column data types
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveSystemColumnDataTypes(final ColumnDataTypes columnDataTypes)
    throws SQLException
  {
    final String catalogName = getRetrieverConnection().getCatalogName();
    final String schemaName = null;
    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getTypeInfo());
    try
    {
      while (results.next())
      {
        final String typeName = results.getString("TYPE_NAME");
        final int type = results.getInt("DATA_TYPE", 0);
        LOGGER.log(Level.FINEST, "Retrieving data type: " + typeName
                                 + ", with type id: " + type);
        final long precision = results.getLong("PRECISION", 0L);
        final String literalPrefix = results.getString("LITERAL_PREFIX");
        final String literalSuffix = results.getString("LITERAL_SUFFIX");
        final String createParameters = results.getString("CREATE_PARAMS");
        final boolean isNullable = results
          .getInt(NULLABLE, DatabaseMetaData.typeNullableUnknown) == DatabaseMetaData.typeNullable;
        final boolean isCaseSensitive = results.getBoolean("CASE_SENSITIVE");
        final int searchable = results.getInt("SEARCHABLE", -1);
        final boolean isUnsigned = results.getBoolean("UNSIGNED_ATTRIBUTE");
        final boolean isFixedPrecisionScale = results
          .getBoolean("FIXED_PREC_SCALE");
        final boolean isAutoIncremented = results.getBoolean("AUTO_INCREMENT");
        final String localTypeName = results.getString("LOCAL_TYPE_NAME");
        final int minimumScale = results.getInt("MINIMUM_SCALE", 0);
        final int maximumScale = results.getInt("MAXIMUM_SCALE", 0);
        final int numPrecisionRadix = results.getInt("NUM_PREC_RADIX", 0);

        final MutableColumnDataType columnDataType = new MutableColumnDataType(catalogName,
                                                                               schemaName,
                                                                               typeName);
        columnDataType.setType(type);
        columnDataType.setPrecision(precision);
        columnDataType.setLiteralPrefix(literalPrefix);
        columnDataType.setLiteralSuffix(literalSuffix);
        columnDataType.setCreateParameters(createParameters);
        columnDataType.setNullable(isNullable);
        columnDataType.setCaseSensitive(isCaseSensitive);
        columnDataType.setSearchable(searchable);
        columnDataType.setUnsigned(isUnsigned);
        columnDataType.setFixedPrecisionScale(isFixedPrecisionScale);
        columnDataType.setAutoIncrementable(isAutoIncremented);
        columnDataType.setLocalTypeName(localTypeName);
        columnDataType.setMinimumScale(minimumScale);
        columnDataType.setMaximumScale(maximumScale);
        columnDataType.setNumPrecisionRadix(numPrecisionRadix);

        columnDataType.addAttributes(results.getAttributes());

        columnDataTypes.add(columnDataType);
      }
    }
    finally
    {
      results.close();
    }

  }

  /**
   * Retrieves user defined column data type metadata.
   * 
   * @param dbInfo
   *        Database info
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveUserDefinedColumnDataTypes(final ColumnDataTypes columnDataTypes)
    throws SQLException
  {
    final String catalogName = getRetrieverConnection().getCatalogName();
    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getUDTs(getRetrieverConnection().getCatalogName(),
                             getRetrieverConnection().getSchemaPattern(),
                             "%",
                             null));
    try
    {
      while (results.next())
      {
        // final String catalogName = results.getString("TYPE_CAT");
        final String schemaName = results.getString("TYPE_SCHEM");
        final String typeName = results.getString("TYPE_NAME");
        LOGGER.log(Level.FINEST, "Retrieving data type: " + typeName);
        final int type = results.getInt("DATA_TYPE", 0);
        final String className = results.getString("CLASS_NAME");
        final String remarks = results.getString("REMARKS");
        final int baseTypeValue = results.getInt("BASE_TYPE", 0);
        final ColumnDataType baseType = columnDataTypes
          .lookupColumnDataTypeByType(baseTypeValue);
        final MutableColumnDataType columnDataType = new MutableColumnDataType(catalogName,
                                                                               schemaName,
                                                                               typeName);
        columnDataType.setUserDefined(true);
        columnDataType.setType(type);
        columnDataType.setTypeClassName(className);
        columnDataType.setBaseType(baseType);
        columnDataType.setRemarks(remarks);

        columnDataType.addAttributes(results.getAttributes());

        columnDataTypes.add(columnDataType);
      }
    }
    finally
    {
      results.close();
    }

  }
}
