/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.SearchableType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.utility.Query;
import sf.util.DatabaseUtility;
import sf.util.StringFormat;

final class DatabaseInfoRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseInfoRetriever.class.getName());

  private static final List<String> ignoreMethods = Arrays
    .asList("getDatabaseProductName",
            "getDatabaseProductVersion",
            "getURL",
            "getUserName",
            "getDriverName",
            "getDriverVersion");

  /**
   * Checks if a method is a result set method.
   *
   * @param method
   *        Method
   * @return Whether a method is a result set method
   */
  private static boolean isDatabasePropertiesResultSetMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean isPropertiesResultSetMethod = returnType
      .equals(ResultSet.class) && method.getParameterTypes().length == 0;
    return isPropertiesResultSetMethod;
  }

  /**
   * Checks if a method is a database property.
   *
   * @param method
   *        Method
   * @return Whether method is a database property
   */
  private static boolean isDatabasePropertyListMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean isDatabasePropertyListMethod = returnType
      .equals(String.class) && method.getName()
        .endsWith("s") && method.getParameterTypes().length == 0;
    return isDatabasePropertyListMethod;
  }

  /**
   * Checks if a method is a database property.
   *
   * @param method
   *        Method
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
   * @param method
   *        Method
   * @return Whether a method is a database property result set type
   */
  private static boolean isDatabasePropertyResultSetType(final Method method)
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
                                                                   "supportsResultSetType" };
    final boolean isDatabasePropertyResultSetType = Arrays
      .binarySearch(databasePropertyResultSetTypes, method.getName()) >= 0;
    return isDatabasePropertyResultSetType;
  }

  private static ImmutableDatabaseProperty retrieveResultSetTypeProperty(final DatabaseMetaData dbMetaData,
                                                                         final Method method,
                                                                         final int resultSetType,
                                                                         final String resultSetTypeName)
    throws IllegalAccessException, InvocationTargetException
  {
    final String name = method.getName() + "For" + resultSetTypeName
                        + "ResultSets";
    final Boolean propertyValue = (Boolean) method
      .invoke(dbMetaData, Integer.valueOf(resultSetType));
    return new ImmutableDatabaseProperty(name, propertyValue);
  }

  DatabaseInfoRetriever(final RetrieverConnection retrieverConnection,
                        final MutableCatalog catalog,
                        final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Provides additional information on the database.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveAdditionalDatabaseInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final MutableDatabaseInfo dbInfo = catalog.getDatabaseInfo();

    final Collection<ImmutableDatabaseProperty> dbProperties = new ArrayList<>();

    final Method[] methods = DatabaseMetaData.class.getMethods();
    for (final Method method: methods)
    {
      try
      {
        if (ignoreMethods.contains(method.getName()))
        {
          continue;
        }
        if (isDatabasePropertyListMethod(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       new StringFormat("Retrieving database property using method, %s",
                                        method));
          }
          final String value = (String) method.invoke(dbMetaData);
          final String[] list = value == null? new String[0]: value.split(",");
          dbProperties.add(new ImmutableDatabaseProperty(method.getName(),
                                                         Arrays.asList(list)));
        }
        else if (isDatabasePropertyMethod(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       new StringFormat("Retrieving database property using method, %s",
                                        method));
          }
          final Object value = method.invoke(dbMetaData);
          dbProperties
            .add(new ImmutableDatabaseProperty(method.getName(), value));
        }
        else if (isDatabasePropertiesResultSetMethod(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       new StringFormat("Retrieving database property using method, %s",
                                        method));
          }
          final ResultSet results = (ResultSet) method.invoke(dbMetaData);
          final List<String> resultsList = DatabaseUtility
            .readResultsVector(results);
          dbProperties.add(new ImmutableDatabaseProperty(method.getName(),
                                                         resultsList
                                                           .toArray(new String[resultsList
                                                             .size()])));
        }
        else if (isDatabasePropertyResultSetType(method))
        {
          if (LOGGER.isLoggable(Level.FINE))
          {
            LOGGER.log(Level.FINER,
                       new StringFormat("Retrieving database property using method, %s",
                                        method));
          }
          dbProperties.add(retrieveResultSetTypeProperty(dbMetaData,
                                                         method,
                                                         ResultSet.TYPE_FORWARD_ONLY,
                                                         "TYPE_FORWARD_ONLY"));
          dbProperties.add(retrieveResultSetTypeProperty(dbMetaData,
                                                         method,
                                                         ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                         "TYPE_SCROLL_INSENSITIVE"));
          dbProperties.add(retrieveResultSetTypeProperty(dbMetaData,
                                                         method,
                                                         ResultSet.TYPE_SCROLL_SENSITIVE,
                                                         "TYPE_SCROLL_SENSITIVE"));
        }
      }
      catch (final IllegalAccessException | InvocationTargetException e)
      {
        LOGGER.log(Level.FINE,
                   e.getCause(),
                   new StringFormat("Could not execute method, %s", method));
      }
      catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
      {
        logSQLFeatureNotSupported("JDBC driver does not support " + method, e);
      }
      catch (final SQLException e)
      {
        // HYC00 = Optional feature not implemented
        if ("HYC00".equalsIgnoreCase(e.getSQLState()))
        {
          logSQLFeatureNotSupported("JDBC driver does not support " + method,
                                    e);
        }
        else
        {
          throw new SchemaCrawlerSQLException("Could not execute " + method, e);
        }
      }

    }

    dbInfo.addAll(dbProperties);

  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveAdditionalJdbcDriverInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final String url = dbMetaData.getURL();

    final MutableJdbcDriverInfo driverInfo = catalog.getJdbcDriverInfo();
    if (driverInfo != null)
    {
      try
      {
        final Driver jdbcDriver = DriverManager.getDriver(url);
        final DriverPropertyInfo[] propertyInfo = jdbcDriver
          .getPropertyInfo(url, new Properties());
        for (final DriverPropertyInfo driverPropertyInfo: propertyInfo)
        {
          driverInfo
            .addJdbcDriverProperty(new ImmutableJdbcDriverProperty(driverPropertyInfo));
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   "Could not obtain JDBC driver information",
                   e);
      }
    }

  }

  void retrieveCrawlHeaderInfo(final String title)
  {
    catalog.setCrawlHeaderInfo(title);
  }

  /**
   * Provides information on the database.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveDatabaseInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();

    final MutableDatabaseInfo dbInfo = catalog.getDatabaseInfo();

    dbInfo.setUserName(dbMetaData.getUserName());
    dbInfo.setProductName(dbMetaData.getDatabaseProductName());
    dbInfo.setProductVersion(dbMetaData.getDatabaseProductVersion());
  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveJdbcDriverInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final String url = dbMetaData.getURL();

    final MutableJdbcDriverInfo driverInfo = catalog.getJdbcDriverInfo();
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

  /**
   * Retrieves column data type metadata.
   *
   * @throws SQLException
   *         On a SQL exception
   * @throws SchemaCrawlerException
   */
  void retrieveSystemColumnDataTypes()
    throws SQLException, SchemaCrawlerException
  {
    final Schema systemSchema = new SchemaReference();

    final Statement statement;
    final MetadataResultSet results;

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (informationSchemaViews.hasOverrideTypeInfoSql())
    {
      final Query typeInfoSql = informationSchemaViews.getOverrideTypeInfoSql();
      final Connection connection = getDatabaseConnection();
      statement = connection.createStatement();
      results = new MetadataResultSet(statement,
                                      typeInfoSql,
                                      getSchemaInclusionRule());
    }
    else
    {
      statement = null;
      results = new MetadataResultSet(getMetaData().getTypeInfo());
    }

    try
    {
      while (results.next())
      {
        final String typeName = results.getString("TYPE_NAME");
        final int dataType = results.getInt("DATA_TYPE", 0);
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving data type: %s (with type id %d)",
                                    typeName,
                                    dataType));
        final long precision = results.getLong("PRECISION", 0L);
        final String literalPrefix = results.getString("LITERAL_PREFIX");
        final String literalSuffix = results.getString("LITERAL_SUFFIX");
        final String createParameters = results.getString("CREATE_PARAMS");
        final boolean isNullable = results
          .getInt("NULLABLE",
                  DatabaseMetaData.typeNullableUnknown) == DatabaseMetaData.typeNullable;
        final boolean isCaseSensitive = results.getBoolean("CASE_SENSITIVE");
        final SearchableType searchable = results
          .getEnumFromId("SEARCHABLE", SearchableType.unknown);
        final boolean isUnsigned = results.getBoolean("UNSIGNED_ATTRIBUTE");
        final boolean isFixedPrecisionScale = results
          .getBoolean("FIXED_PREC_SCALE");
        final boolean isAutoIncremented = results.getBoolean("AUTO_INCREMENT");
        final String localTypeName = results.getString("LOCAL_TYPE_NAME");
        final int minimumScale = results.getInt("MINIMUM_SCALE", 0);
        final int maximumScale = results.getInt("MAXIMUM_SCALE", 0);
        final int numPrecisionRadix = results.getInt("NUM_PREC_RADIX", 0);

        final MutableColumnDataType columnDataType = lookupOrCreateColumnDataType(systemSchema,
                                                                                  dataType,
                                                                                  typeName);
        // Set the Java SQL type code, but no mapped Java class is
        // available, so use the defaults
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

        catalog.addColumnDataType(columnDataType);
      }
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      if (statement != null)
      {
        statement.close();
      }
    }
  }

  void retrieveUserDefinedColumnDataTypes(final String catalogName,
                                          final String schemaName)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getUDTs(catalogName,
               schemaName,
               "%",
               null));)
    {
      while (results.next())
      {
        // "TYPE_CAT", "TYPE_SCHEM"
        final String typeName = results.getString("TYPE_NAME");
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving data type, %s", typeName));
        final int dataType = results.getInt("DATA_TYPE", 0);
        final String className = results.getString("CLASS_NAME");
        final String remarks = results.getString("REMARKS");
        final short baseTypeValue = results.getShort("BASE_TYPE", (short) 0);

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final ColumnDataType baseType = catalog
          .lookupColumnDataTypeByType(baseTypeValue);
        final MutableColumnDataType columnDataType = lookupOrCreateColumnDataType(schema,
                                                                                  dataType,
                                                                                  typeName,
                                                                                  className);
        columnDataType.setUserDefined(true);
        columnDataType.setBaseType(baseType);
        columnDataType.setRemarks(remarks);

        columnDataType.addAttributes(results.getAttributes());

        catalog.addColumnDataType(columnDataType);
      }
    }

  }

}
