/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.FunctionColumn;
import schemacrawler.schema.FunctionColumnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.utility.Query;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database function columns.
 *
 * @author Sualeh Fatehi
 */
final class FunctionColumnRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(FunctionColumnRetriever.class.getName());

  FunctionColumnRetriever(final RetrieverConnection retrieverConnection,
                          final MutableCatalog catalog,
                          final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  void retrieveFunctionColumns(final NamedObjectList<MutableRoutine> allRoutines,
                               final InclusionRule columnInclusionRule)
    throws SQLException
  {
    requireNonNull(allRoutines, "No functions provided");

    final InclusionRuleFilter<FunctionColumn> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                                       true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER
        .log(Level.INFO,
             "Not retrieving function columns, since this was not requested");
      return;
    }

    final MetadataRetrievalStrategy functionColumnRetrievalStrategy = getRetrieverConnection()
      .getFunctionColumnRetrievalStrategy();
    switch (functionColumnRetrievalStrategy)
    {
      case data_dictionary_all:
        LOGGER
          .log(Level.INFO,
               "Retrieving function columns, using fast data dictionary retrieval");
        retrieveFunctionColumnsFromDataDictionary(allRoutines, columnFilter);
        break;

      case metadata_all:
        LOGGER
          .log(Level.INFO,
               "Retrieving function columns, using fast meta-data retrieval");
        retrieveFunctionColumnsFromMetadataForAllFunctions(allRoutines,
                                                           columnFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving function columns");
        retrieveFunctionColumnsFromMetadata(allRoutines, columnFilter);
        break;

      default:
        break;
    }

  }

  private void createFunctionColumn(final MetadataResultSet results,
                                    final NamedObjectList<MutableRoutine> allRoutines,
                                    final InclusionRuleFilter<FunctionColumn> columnFilter)
  {
    final String columnCatalogName = normalizeCatalogName(results
      .getString("FUNCTION_CAT"));
    final String schemaName = normalizeSchemaName(results
      .getString("FUNCTION_SCHEM"));
    final String functionName = results.getString("FUNCTION_NAME");
    final String columnName = results.getString("COLUMN_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");

    LOGGER.log(Level.FINE,
               new StringFormat("Retrieving function column <%s.%s.%s.%s.%s>",
                                columnCatalogName,
                                schemaName,
                                functionName,
                                specificName,
                                columnName));
    if (isBlank(columnName))
    {
      return;
    }

    final Optional<MutableRoutine> optionalRoutine = allRoutines.lookup(Arrays
      .asList(columnCatalogName, schemaName, functionName, specificName));
    if (!optionalRoutine.isPresent())
    {
      return;
    }

    final MutableRoutine routine = optionalRoutine.get();
    if (routine.getRoutineType() != RoutineType.function)
    {
      return;
    }

    final MutableFunction function = (MutableFunction) routine;
    final MutableFunctionColumn column = lookupOrCreateFunctionColumn(function,
                                                                      columnName);
    if (columnFilter.test(column)
        && belongsToSchema(function, columnCatalogName, schemaName))
    {
      final FunctionColumnType columnType = results
        .getEnumFromShortId("COLUMN_TYPE", FunctionColumnType.unknown);
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int length = results.getInt("LENGTH", 0);
      final int precision = results.getInt("PRECISION", 0);
      final boolean isNullable = results
        .getShort("NULLABLE",
                  (short) DatabaseMetaData.functionNullableUnknown) == (short) DatabaseMetaData.functionNullable;
      final String remarks = results.getString("REMARKS");
      column.setOrdinalPosition(ordinalPosition);
      column.setFunctionColumnType(columnType);
      column
        .setColumnDataType(lookupOrCreateColumnDataType(function.getSchema(),
                                                        dataType,
                                                        typeName));
      column.setSize(length);
      column.setPrecision(precision);
      column.setNullable(isNullable);
      column.setRemarks(remarks);

      column.addAttributes(results.getAttributes());

      LOGGER.log(Level.FINER,
                 new StringFormat("Adding column to function <%s>",
                                  column.getFullName()));
      function.addColumn(column);
    }

  }

  private MutableFunctionColumn lookupOrCreateFunctionColumn(final MutableFunction function,
                                                             final String columnName)
  {
    final Optional<MutableFunctionColumn> columnOptional = function
      .lookupColumn(columnName);
    final MutableFunctionColumn column;
    if (columnOptional.isPresent())
    {
      column = columnOptional.get();
    }
    else
    {
      column = new MutableFunctionColumn(function, columnName);
    }
    return column;
  }

  private void retrieveFunctionColumnsFromDataDictionary(final NamedObjectList<MutableRoutine> allRoutines,
                                                         final InclusionRuleFilter<FunctionColumn> columnFilter)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(InformationSchemaKey.FUNCTION_COLUMNS))
    {
      throw new SchemaCrawlerSQLException("No function columns SQL provided",
                                          null);
    }
    final Query functionColumnsSql = informationSchemaViews
      .getQuery(InformationSchemaKey.FUNCTION_COLUMNS);
    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(functionColumnsSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      results.setDescription("retrieveFunctionColumnsFromDataDictionary");
      while (results.next())
      {
        createFunctionColumn(results, allRoutines, columnFilter);
      }
    }
  }

  private void retrieveFunctionColumnsFromMetadata(final NamedObjectList<MutableRoutine> allRoutines,
                                                   final InclusionRuleFilter<FunctionColumn> columnFilter)
    throws SchemaCrawlerSQLException
  {
    for (final MutableRoutine routine: allRoutines)
    {
      if (routine.getRoutineType() != RoutineType.function)
      {
        continue;
      }
      final MutableFunction function = (MutableFunction) routine;

      LOGGER.log(Level.FINE, "Retrieving function columns for " + function);
      try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getFunctionColumns(function.getSchema().getCatalogName(),
                            function.getSchema().getName(),
                            function.getName(),
                            null));)
      {
        while (results.next())
        {
          createFunctionColumn(results, allRoutines, columnFilter);
        }
      }
      catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
      {
        logSQLFeatureNotSupported(new StringFormat("Could not retrieve columns for function %s",
                                                   function),
                                  e);
      }
      catch (final SQLException e)
      {
        logPossiblyUnsupportedSQLFeature(new StringFormat("Could not retrieve columns for function %s",
                                                          function),
                                         e);
      }
    }
  }

  private void retrieveFunctionColumnsFromMetadataForAllFunctions(final NamedObjectList<MutableRoutine> allRoutines,
                                                                  final InclusionRuleFilter<FunctionColumn> columnFilter)
    throws SQLException
  {
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getFunctionColumns(null, null, "%", "%"));)
    {
      while (results.next())
      {
        createFunctionColumn(results, allRoutines, columnFilter);
      }
    }
    catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
    {
      logSQLFeatureNotSupported(new StringFormat("Could not retrieve columns for functions"),
                                e);
    }
    catch (final SQLException e)
    {
      logPossiblyUnsupportedSQLFeature(new StringFormat("Could not retrieve columns for functions"),
                                       e);
    }
  }

}
