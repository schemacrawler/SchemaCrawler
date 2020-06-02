/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.InformationSchemaKey.USERS;
import static sf.util.Utility.isBlank;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Property;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import sf.util.DatabaseUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

final class DatabaseInfoRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(DatabaseInfoRetriever.class.getName());

  private static final List<String> ignoreMethods = Arrays.asList(
    "getDatabaseProductName",
    "getDatabaseProductVersion",
    "getURL",
    "getUserName",
    "getDriverName",
    "getDriverVersion");

  /**
   * Checks if a method is a result set method.
   *
   * @param method
   *   Method
   * @return Whether a method is a result set method
   */
  private static boolean isDatabasePropertiesResultSetMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean isPropertiesResultSetMethod =
      returnType.equals(ResultSet.class)
      && method.getParameterTypes().length == 0;
    return isPropertiesResultSetMethod;
  }

  /**
   * Checks if a method is a database property.
   *
   * @param method
   *   Method
   * @return Whether method is a database property
   */
  private static boolean isDatabasePropertyListMethod(final Method method)
  {
    final Class<?> returnType = method.getReturnType();
    final boolean isDatabasePropertyListMethod =
      returnType.equals(String.class) && method
        .getName()
        .endsWith("s") && method.getParameterTypes().length == 0;
    return isDatabasePropertyListMethod;
  }

  /**
   * Checks if a method is a database property.
   *
   * @param method
   *   Method
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

  private static ImmutableDatabaseProperty retrieveResultSetTypeProperty(final DatabaseMetaData dbMetaData,
                                                                         final Method method,
                                                                         final int resultSetType,
                                                                         final String resultSetTypeName)
    throws IllegalAccessException, InvocationTargetException
  {
    final String name =
      method.getName() + "For" + resultSetTypeName + "ResultSets";
    final Boolean propertyValue =
      (Boolean) method.invoke(dbMetaData, Integer.valueOf(resultSetType));
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
   *   On a SQL exception
   */
  void retrieveAdditionalDatabaseInfo()
  {
    final DatabaseMetaData dbMetaData = getMetaData();
    final MutableDatabaseInfo dbInfo = catalog.getDatabaseInfo();

    final Collection<ImmutableDatabaseProperty> dbProperties =
      new ArrayList<>();

    final Method[] methods = DatabaseMetaData.class.getMethods();
    for (final Method method : methods)
    {
      try
      {
        if (method.getParameterTypes().length > 0 || ignoreMethods.contains(
          method.getName()))
        {
          continue;
        }

        LOGGER.log(Level.FINER,
                   new StringFormat(
                     "Retrieving database property using method <%s>",
                     method));

        final Object methodReturnValue = method.invoke(dbMetaData);
        if (isDatabasePropertyListMethod(method))
        {
          final String value = (String) methodReturnValue;
          final String[] list = value == null? new String[0]: value.split(",");
          dbProperties.add(new ImmutableDatabaseProperty(method.getName(),
                                                         list));
        }
        else if (isDatabasePropertyMethod(method))
        {
          dbProperties.add(new ImmutableDatabaseProperty(method.getName(),
                                                         methodReturnValue));
        }
        else if (isDatabasePropertiesResultSetMethod(method))
        {
          final ResultSet results = (ResultSet) methodReturnValue;
          final List<String> resultsList =
            DatabaseUtility.readResultsVector(results);
          Collections.sort(resultsList);
          dbProperties.add(new ImmutableDatabaseProperty(method.getName(),
                                                         resultsList.toArray(new String[resultsList.size()])));
        }

      }
      catch (final IllegalAccessException | InvocationTargetException e)
      {
        LOGGER.log(Level.FINE,
                   new StringFormat("Could not execute method <%s>", method),
                   e.getCause());
      }
      catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
      {
        logSQLFeatureNotSupported(new StringFormat(
          "Database metadata method <%s> not supported",
          method), e);
      }
      catch (final SQLException e)
      {
        logPossiblyUnsupportedSQLFeature(new StringFormat(
          "SQL exception invoking method <%s>",
          method), e);
      }

    }

    final Collection<ImmutableDatabaseProperty> resultSetTypesProperties =
      retrieveResultSetTypesProperties(dbMetaData);
    dbProperties.addAll(resultSetTypesProperties);

    dbInfo.addAll(dbProperties);

  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException
   *   On a SQL exception
   */
  void retrieveAdditionalJdbcDriverInfo()
  {
    final MutableJdbcDriverInfo driverInfo = catalog.getJdbcDriverInfo();
    if (driverInfo == null)
    {
      return;
    }

    try
    {
      final DatabaseMetaData dbMetaData = getMetaData();
      final String url = dbMetaData.getURL();

      final Driver jdbcDriver = getRetrieverConnection().getDriver();
      final DriverPropertyInfo[] propertyInfo =
        jdbcDriver.getPropertyInfo(url, new Properties());
      for (final DriverPropertyInfo driverPropertyInfo : propertyInfo)
      {
        driverInfo.addJdbcDriverProperty(new ImmutableJdbcDriverProperty(
          driverPropertyInfo));
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not obtain JDBC driver information", e);
    }

  }

  void retrieveCrawlInfo()
  {
    catalog.setCrawlInfo();
  }

  /**
   * Provides information on the database.
   *
   * @throws SQLException
   *   On a SQL exception
   */
  void retrieveDatabaseInfo()
  {

    final MutableDatabaseInfo dbInfo = catalog.getDatabaseInfo();
    if (dbInfo == null)
    {
      return;
    }

    final DatabaseMetaData dbMetaData;
    try
    {
      dbMetaData = getMetaData();

      dbInfo.setUserName(dbMetaData.getUserName());
      dbInfo.setProductName(dbMetaData.getDatabaseProductName());
      dbInfo.setProductVersion(dbMetaData.getDatabaseProductVersion());
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not obtain database information", e);
    }

  }

  /**
   * Provides information on the JDBC driver.
   *
   * @throws SQLException
   *   On a SQL exception
   */
  void retrieveJdbcDriverInfo()
  {
    final MutableJdbcDriverInfo driverInfo = catalog.getJdbcDriverInfo();
    if (driverInfo == null)
    {
      return;
    }

    try
    {
      final DatabaseMetaData dbMetaData = getMetaData();
      final String url = dbMetaData.getURL();

      driverInfo.setDriverName(dbMetaData.getDriverName());
      driverInfo.setDriverVersion(dbMetaData.getDriverVersion());
      driverInfo.setConnectionUrl(url);
      final Driver jdbcDriver = getRetrieverConnection().getDriver();
      driverInfo.setJdbcDriverClassName(jdbcDriver
                                          .getClass()
                                          .getName());
      driverInfo.setJdbcCompliant(jdbcDriver.jdbcCompliant());
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not obtain JDBC driver information", e);
    }

  }

  void retrieveServerInfo()
  {
    final MutableDatabaseInfo dbInfo = catalog.getDatabaseInfo();
    if (dbInfo == null)
    {
      return;
    }

    final InformationSchemaViews informationSchemaViews =
      getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(InformationSchemaKey.SERVER_INFORMATION))
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving server information, since this was not requested");
      LOGGER.log(Level.FINE,
                 "Server information SQL statement was not provided");
      return;
    }
    final Query serverInfoSql =
      informationSchemaViews.getQuery(InformationSchemaKey.SERVER_INFORMATION);

    final Connection connection = getDatabaseConnection();
    try (
      final Statement statement = connection.createStatement();
      final MetadataResultSet results = new MetadataResultSet(serverInfoSql,
                                                              statement,
                                                              new IncludeAll())
    )
    {
      results.setDescription("retrieveServerInfo");
      while (results.next())
      {
        final String propertyName = results.getString("NAME");
        if (isBlank(propertyName))
        {
          continue;
        }

        final String propertyValue = results.getString("VALUE");
        final String propertyDescription = results.getString("DESCRIPTION");

        LOGGER.log(Level.FINER,
                   new StringFormat(
                     "Retrieving server information property: %s=%s",
                     propertyName,
                     propertyValue));

        final Property serverInfoProperty = new ImmutableServerInfoProperty(
          propertyName,
          propertyValue,
          propertyDescription);
        dbInfo.addServerInfo(serverInfoProperty);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve server information", e);
    }

  }

  void retrieveDatabaseUsers()
  {

    final InformationSchemaViews informationSchemaViews =
      getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(USERS))
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving database users information, since this was not requested");
      LOGGER.log(Level.FINE, "Database users SQL statement was not provided");
      return;
    }
    final Query databaseUsersSql = informationSchemaViews.getQuery(USERS);

    final Connection connection = getDatabaseConnection();
    try (
      final Statement statement = connection.createStatement();
      final MetadataResultSet results = new MetadataResultSet(databaseUsersSql,
                                                              statement,
                                                              new IncludeAll())
    )
    {
      results.setDescription("retrieveDatabaseUsers");
      while (results.next())
      {
        final String username = results.getString("USERNAME");
        if (isBlank(username))
        {
          continue;
        }
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving database user: %s", username));

        final ImmutableDatabaseUser databaseUser =
          new ImmutableDatabaseUser(username);
        databaseUser.addAttributes(results.getAttributes());
        catalog.addDatabaseUser(databaseUser);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve database users", e);
    }

  }

  /**
   * Retrieves column data type metadata.
   *
   * @throws SQLException
   *   On a SQL exception
   */
  void retrieveSystemColumnDataTypes()
    throws SQLException
  {
    final Schema systemSchema = new SchemaReference();

    final Statement statement;
    final MetadataResultSet results;

    final InformationSchemaViews informationSchemaViews =
      getRetrieverConnection().getInformationSchemaViews();
    if (informationSchemaViews.hasQuery(InformationSchemaKey.OVERRIDE_TYPE_INFO))
    {
      final Query typeInfoSql =
        informationSchemaViews.getQuery(InformationSchemaKey.OVERRIDE_TYPE_INFO);
      final Connection connection = getDatabaseConnection();
      statement = connection.createStatement();
      results =
        new MetadataResultSet(typeInfoSql, statement, getSchemaInclusionRule());
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
                   new StringFormat("Retrieving data type <%s> with type id %d",
                                    typeName,
                                    dataType));
        final long precision = results.getLong("PRECISION", 0L);
        final String literalPrefix = results.getString("LITERAL_PREFIX");
        final String literalSuffix = results.getString("LITERAL_SUFFIX");
        final String createParameters = results.getString("CREATE_PARAMS");
        final boolean isNullable =
          results.getInt("NULLABLE", DatabaseMetaData.typeNullableUnknown)
          == DatabaseMetaData.typeNullable;
        final boolean isCaseSensitive = results.getBoolean("CASE_SENSITIVE");
        final SearchableType searchable =
          results.getEnumFromId("SEARCHABLE", SearchableType.unknown);
        final boolean isUnsigned = results.getBoolean("UNSIGNED_ATTRIBUTE");
        final boolean isFixedPrecisionScale =
          results.getBoolean("FIXED_PREC_SCALE");
        final boolean isAutoIncremented = results.getBoolean("AUTO_INCREMENT");
        final String localTypeName = results.getString("LOCAL_TYPE_NAME");
        final int minimumScale = results.getInt("MINIMUM_SCALE", 0);
        final int maximumScale = results.getInt("MAXIMUM_SCALE", 0);
        final int numPrecisionRadix = results.getInt("NUM_PREC_RADIX", 0);

        final MutableColumnDataType columnDataType =
          lookupOrCreateColumnDataType(systemSchema, dataType, typeName);
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
      results.close();
      if (statement != null)
      {
        statement.close();
      }
    }
  }

  void retrieveUserDefinedColumnDataTypes(final Schema schema)
    throws SQLException
  {
    requireNonNull(schema, "No schema provided");

    final Optional<SchemaReference> schemaOptional =
      catalog.lookupSchema(schema.getFullName());
    if (!schemaOptional.isPresent())
    {
      LOGGER.log(Level.INFO,
                 new StringFormat(
                   "Cannot locate schema, so not retrieving data types for schema: %s",
                   schema));
      return;
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Retrieving data types for schema <%s>",
                                schema));

    final String catalogName = schema.getCatalogName();
    final String schemaName = schema.getName();

    try (
      final MetadataResultSet results = new MetadataResultSet(getMetaData().getUDTs(
        catalogName,
        schemaName,
        null,
        null))
    )
    {
      while (results.next())
      {
        // "TYPE_CAT", "TYPE_SCHEM"
        final String typeName = results.getString("TYPE_NAME");
        LOGGER.log(Level.FINE,
                   new StringFormat("Retrieving data type <%s.%s>",
                                    schema,
                                    typeName));
        final int dataType = results.getInt("DATA_TYPE", 0);
        final String className = results.getString("CLASS_NAME");
        final String remarks = results.getString("REMARKS");
        final short baseTypeValue = results.getShort("BASE_TYPE", (short) 0);

        final ColumnDataType baseType;
        if (baseTypeValue != 0)
        {
          baseType = catalog.lookupBaseColumnDataTypeByType(baseTypeValue);
        }
        else
        {
          baseType = null;
        }
        final MutableColumnDataType columnDataType =
          lookupOrCreateColumnDataType(schema, dataType, typeName, className);
        columnDataType.setUserDefined(true);
        columnDataType.setBaseType(baseType);
        columnDataType.setRemarks(remarks);

        columnDataType.addAttributes(results.getAttributes());

        catalog.addColumnDataType(columnDataType);
      }
    }

  }

  private Collection<ImmutableDatabaseProperty> retrieveResultSetTypesProperties(
    final DatabaseMetaData dbMetaData)
  {
    final Collection<ImmutableDatabaseProperty> dbProperties =
      new ArrayList<>();
    final String[] resultSetTypesMethods = new String[] {
      "deletesAreDetected",
      "insertsAreDetected",
      "updatesAreDetected",
      "othersInsertsAreVisible",
      "othersDeletesAreVisible",
      "othersUpdatesAreVisible",
      "ownDeletesAreVisible",
      "ownInsertsAreVisible",
      "ownUpdatesAreVisible",
      "supportsResultSetType"
    };
    for (final String methodName : resultSetTypesMethods)
    {
      try
      {
        final Method method =
          DatabaseMetaData.class.getMethod(methodName, int.class);
        LOGGER.log(Level.FINER,
                   new StringFormat(
                     "Retrieving database property using method <%s>",
                     method));

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
      catch (final Exception e)
      {
        LOGGER.log(Level.FINE,
                   new StringFormat("Could not execute method <%s>",
                                    methodName),
                   e.getCause());
        continue;
      }
    }

    return dbProperties;
  }

}
