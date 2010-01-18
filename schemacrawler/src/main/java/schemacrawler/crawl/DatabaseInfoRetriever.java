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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Schema;

final class DatabaseInfoRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseInfoRetriever.class.getName());

  private static final List<String> ignoreMethods = Arrays.asList(
    "getDatabaseProductName",
    "getDatabaseProductVersion",
    "getURL",
    "getUserName",
    "getDriverName",
    "getDriverVersion"
  );

  DatabaseInfoRetriever(final RetrieverConnection retrieverConnection,
                        final MutableDatabase database)
  {
    super(retrieverConnection, database);
  }

  /**
   * Provides additional information on the database.
   *
   * @param database Database information to add to
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalDatabaseInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final MutableDatabaseInfo dbInfo = database.getDatabaseInfo();

    final Collection<MutableDatabaseProperty> dbProperties = new ArrayList<MutableDatabaseProperty>();

    final Method[] methods = DatabaseMetaData.class.getMethods();
    for (final Method method : methods)
    {
      try
      {
        if (ignoreMethods.contains(method.getName()))
        {
          continue;
        }
        if (isDatabasePropertyMethod(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       "Retrieving database property using method: " + method);
          }
          Object value = method.invoke(dbMetaData);
          if (value != null && method.getName()
            .endsWith("s")
            && value instanceof String)
          {
            // Probably a comma-separated list
            value = Collections.unmodifiableList(Arrays.asList(((String) value)
              .split(",")));
          }
          // Add to the properties map
          dbProperties
            .add(new MutableDatabaseProperty(method.getName(), value));
        }
        else if (isDatabasePropertiesResultSetMethod(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       "Retrieving database property using method: " + method);
          }
          final ResultSet results = (ResultSet) method.invoke(dbMetaData);
          dbProperties
            .add(new MutableDatabaseProperty(method.getName(),
                                             readResultsVector(results)));
        }
        else if (isDatabasePropertyResultSetType(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       "Retrieving database property using method: " + method);
          }
          dbProperties
            .add(retrieveResultSetTypeProperty(dbMetaData,
                                               method,
                                               ResultSet.TYPE_FORWARD_ONLY,
                                               "TYPE_FORWARD_ONLY"));
          dbProperties
            .add(retrieveResultSetTypeProperty(dbMetaData,
                                               method,
                                               ResultSet.TYPE_SCROLL_INSENSITIVE,
                                               "TYPE_SCROLL_INSENSITIVE"));
          dbProperties
            .add(retrieveResultSetTypeProperty(dbMetaData,
                                               method,
                                               ResultSet.TYPE_SCROLL_SENSITIVE,
                                               "TYPE_SCROLL_SENSITIVE"));
        }
      }
      catch (final IllegalAccessException e)
      {
        LOGGER.log(Level.FINE, "Could not execute method, " + method, e);
      }
      catch (final InvocationTargetException e)
      {
        LOGGER.log(Level.FINE, "Could not execute method, " + method, e
          .getCause());
      }
    }

    dbInfo.addAll(dbProperties);

  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalJdbcDriverInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final String url = dbMetaData.getURL();

    final MutableJdbcDriverInfo driverInfo = database.getJdbcDriverInfo();
    if (driverInfo != null)
    {
      try
      {
        final Driver jdbcDriver = DriverManager.getDriver(url);
        final DriverPropertyInfo[] propertyInfo = jdbcDriver
          .getPropertyInfo(url, new Properties());
        for (final DriverPropertyInfo driverPropertyInfo : propertyInfo)
        {
          driverInfo
            .addJdbcDriverProperty(new MutableJdbcDriverProperty(driverPropertyInfo));
        }
      }
      catch (final SQLException e)
      {
        LOGGER
          .log(Level.WARNING, "Could not obtain JDBC driver information", e);
      }
    }

  }

  void retrieveAdditionalSchemaCrawlerInfo()
  {
    database.getSchemaCrawlerInfo()
      .setAdditionalSchemaCrawlerInfo();
  }

  /**
   * Provides information on the database.
   *
   * @param database Database
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveDatabaseInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();

    final MutableDatabaseInfo dbInfo = database.getDatabaseInfo();

    dbInfo.setUserName(dbMetaData.getUserName());
    dbInfo.setProductName(dbMetaData.getDatabaseProductName());
    dbInfo.setProductVersion(dbMetaData.getDatabaseProductVersion());
  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveJdbcDriverInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final String url = dbMetaData.getURL();

    final MutableJdbcDriverInfo driverInfo = database.getJdbcDriverInfo();
    if (driverInfo != null)
    {
      driverInfo.setDriverName(dbMetaData.getDriverName());
      driverInfo.setDriverVersion(dbMetaData.getDriverVersion());
      driverInfo.setConnectionUrl(url);
      final Driver jdbcDriver = DriverManager.getDriver(url);
      driverInfo.setJdbcDriverClassName(jdbcDriver.getClass().getName());
      driverInfo.setJdbcCompliant(jdbcDriver.jdbcCompliant());
    }

  }

  void retrieveSchemaCrawlerInfo()
  {
    database.getSchemaCrawlerInfo()
      .setSchemaCrawlerInfo();
  }

  /**
   * Retrieves column data type metadata.
   *
   * @param database Column data types
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveSystemColumnDataTypes()
    throws SQLException
  {
    final Schema systemSchema = new MutableSchema();

    final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getTypeInfo());
    try
    {
      while (results.next())
      {
        final String typeName = results.getString("TYPE_NAME");
        final int dataType = results.getInt("DATA_TYPE", 0);
        LOGGER.log(Level.FINER, String
          .format("Retrieving data type: %s (with type id %d)",
                  typeName,
                  dataType));
        final long precision = results.getLong("PRECISION", 0L);
        final String literalPrefix = results.getString("LITERAL_PREFIX");
        final String literalSuffix = results.getString("LITERAL_SUFFIX");
        final String createParameters = results.getString("CREATE_PARAMS");
        final boolean isNullable = results
          .getInt("NULLABLE", DatabaseMetaData.typeNullableUnknown) == DatabaseMetaData.typeNullable;
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

        final MutableColumnDataType columnDataType = new MutableColumnDataType(systemSchema,
                                                                               typeName);
        // Set the Java SQL type code, but no mapped Java class is
        // available, so use the defaults
        columnDataType.setType(dataType, null);
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

        database.addSystemColumnDataType(columnDataType);
      }
    }
    finally
    {
      results.close();
    }

  }

  void retrieveUserDefinedColumnDataTypes(final String catalogName,
                                          final String schemaName)
    throws SQLException
  {
    final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getUDTs(catalogName, schemaName, "%", null));
    try
    {
      while (results.next())
      {
        // final String catalogName = results.getString("TYPE_CAT");
        // final String schemaName = results.getString("TYPE_SCHEM");
        final String typeName = results.getString("TYPE_NAME");
        LOGGER.log(Level.FINER, "Retrieving data type: " + typeName);
        final short dataType = results.getShort("DATA_TYPE", (short) 0);
        final String className = results.getString("CLASS_NAME");
        final String remarks = results.getString("REMARKS");
        final short baseTypeValue = results.getShort("BASE_TYPE", (short) 0);

        final MutableSchema schema = lookupSchema(catalogName, schemaName);
        if (schema == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find schema, %s.%s",
                                               catalogName,
                                               schemaName));
          continue;
        }
        final ColumnDataType baseType = lookupColumnDataTypeByType(schema,
                                                                   baseTypeValue);
        final MutableColumnDataType columnDataType = new MutableColumnDataType(schema,
                                                                               typeName);
        columnDataType.setUserDefined(true);
        columnDataType.setType(dataType, className);
        columnDataType.setBaseType(baseType);
        columnDataType.setRemarks(remarks);

        columnDataType.addAttributes(results.getAttributes());

        schema.addColumnDataType(columnDataType);
      }
    }
    finally
    {
      results.close();
    }

  }

  /**
   * Checks if a method is a result set method.
   *
   * @param method Method
   *
   * @return Whether a method is a result set method
   */
  private static boolean isDatabasePropertiesResultSetMethod(final Method method)
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
   * @param method Method
   *
   * @return Whether method is a database property
   */
  private static boolean isDatabasePropertyMethod(final Method method)
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
   * @param method Method
   *
   * @return Whether a method is a database property result set type
   */
  private static boolean isDatabasePropertyResultSetType(final Method method)
  {
    final String[] databasePropertyResultSetTypes = new String[]{
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

  private static MutableDatabaseProperty retrieveResultSetTypeProperty(final DatabaseMetaData dbMetaData,
                                                                       final Method method,
                                                                       final int resultSetType,
                                                                       final String resultSetTypeName)
    throws IllegalAccessException, InvocationTargetException
  {
    final String name = method.getName() + "For" + resultSetTypeName
      + "ResultSets";
    Boolean propertyValue = null;
    propertyValue = (Boolean) method.invoke(dbMetaData, Integer.valueOf(resultSetType));
    return new MutableDatabaseProperty(name, propertyValue);
  }

}
