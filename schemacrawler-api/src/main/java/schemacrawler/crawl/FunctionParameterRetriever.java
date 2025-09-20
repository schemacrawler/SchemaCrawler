/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.sql.DatabaseMetaData.functionNullable;
import static java.sql.DatabaseMetaData.functionNullableUnknown;
import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.InformationSchemaKey.FUNCTION_COLUMNS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about the database function parameters. */
final class FunctionParameterRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(FunctionParameterRetriever.class.getName());

  FunctionParameterRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options) {
    super(retrieverConnection, catalog, options);
  }

  void retrieveFunctionParameters(
      final NamedObjectList<MutableRoutine> allRoutines, final InclusionRule parameterInclusionRule)
      throws SQLException {
    requireNonNull(allRoutines, "No functions provided");

    final InclusionRuleFilter<FunctionParameter> parameterFilter =
        new InclusionRuleFilter<>(parameterInclusionRule, true);
    if (parameterFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving function parameters, since this was not requested");
      return;
    }

    switch (getRetrieverConnection().get(functionParametersRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(
            Level.INFO, "Retrieving function parameters, using fast data dictionary retrieval");
        retrieveFunctionParametersFromDataDictionary(allRoutines, parameterFilter);
        break;

      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO,
            "Retrieving function parameters, using fast data dictionary retrieval over schemas");
        retrieveFunctionParametersOverSchemas(allRoutines, parameterFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving function parameters");
        retrieveFunctionParametersFromMetadata(allRoutines, parameterFilter);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving function parameters");
        break;
    }
  }

  private boolean createFunctionParameter(
      final MetadataResultSet results,
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<FunctionParameter> parameterFilter) {
    final String columnCatalogName = normalizeCatalogName(results.getString("FUNCTION_CAT"));
    final String schemaName = normalizeSchemaName(results.getString("FUNCTION_SCHEM"));
    final String functionName = results.getString("FUNCTION_NAME");
    String columnName = results.getString("COLUMN_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");

    final ParameterModeType parameterMode =
        getFunctionParameterMode(
            results.getInt("COLUMN_TYPE", DatabaseMetaData.functionColumnUnknown));

    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Retrieving function parameter <%s.%s.%s.%s.%s>",
            columnCatalogName, schemaName, functionName, specificName, columnName));
    if (isBlank(columnName) && parameterMode == ParameterModeType.result) {
      columnName = "<return value>";
    }
    if (isBlank(columnName)) {
      return false;
    }

    final Optional<MutableRoutine> optionalRoutine =
        allRoutines.lookup(
            new NamedObjectKey(columnCatalogName, schemaName, functionName, specificName));
    if (!optionalRoutine.isPresent()) {
      return false;
    }

    final MutableRoutine routine = optionalRoutine.get();
    if (routine.getRoutineType() != RoutineType.function) {
      return false;
    }

    final MutableFunction function = (MutableFunction) routine;
    final MutableFunctionParameter parameter =
        lookupOrCreateFunctionParameter(function, columnName);
    parameter.withQuoting(getRetrieverConnection().getIdentifiers());
    if (parameterFilter.test(parameter)
        && belongsToSchema(function, columnCatalogName, schemaName)) {
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int length = results.getInt("LENGTH", 0);
      final int precision = results.getInt("PRECISION", 0);
      final boolean isNullable =
          results.getShort("NULLABLE", (short) functionNullableUnknown) == functionNullable;
      final String remarks = results.getString("REMARKS");
      parameter.setOrdinalPosition(ordinalPosition);
      parameter.setParameterMode(parameterMode);
      parameter.setColumnDataType(
          lookupOrCreateColumnDataType(user_defined, function.getSchema(), dataType, typeName));
      parameter.setSize(length);
      parameter.setPrecision(precision);
      parameter.setNullable(isNullable);
      parameter.setRemarks(remarks);

      parameter.addAttributes(results.getAttributes());

      LOGGER.log(Level.FINER, new StringFormat("Adding parameter to function <%s>", parameter));
      function.addParameter(parameter);
      return true;
    }
    return false;
  }

  private ParameterModeType getFunctionParameterMode(final int columnType) {
    switch (columnType) {
      case DatabaseMetaData.functionColumnIn:
        return ParameterModeType.in;
      case DatabaseMetaData.functionColumnInOut:
        return ParameterModeType.inOut;
      case DatabaseMetaData.functionColumnOut:
        return ParameterModeType.out;
      case DatabaseMetaData.functionColumnResult:
        return ParameterModeType.result;
      case DatabaseMetaData.functionReturn:
        return ParameterModeType.returnValue;
      default:
        return ParameterModeType.unknown;
    }
  }

  private MutableFunctionParameter lookupOrCreateFunctionParameter(
      final MutableFunction function, final String columnName) {
    final Optional<MutableFunctionParameter> parameterOptional =
        function.lookupParameter(columnName);
    return parameterOptional.orElseGet(() -> new MutableFunctionParameter(function, columnName));
  }

  private void retrieveFunctionParametersFromDataDictionary(
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<FunctionParameter> parameterFilter)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(FUNCTION_COLUMNS)) {
      throw new ExecutionRuntimeException("No function parameters SQL provided");
    }
    final String name = "function parameters from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query functionColumnsSql = informationSchemaViews.getQuery(FUNCTION_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(functionColumnsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = createFunctionParameter(results, allRoutines, parameterFilter);
        retrievalCounts.countIfIncluded(added);
      }
    }
    retrievalCounts.log();
  }

  private void retrieveFunctionParametersFromMetadata(
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<FunctionParameter> parameterFilter) {
    final String name = "function parameters from metadata";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final MutableRoutine routine : allRoutines) {
      if (routine.getRoutineType() != RoutineType.function) {
        continue;
      }
      LOGGER.log(Level.INFO, new StringFormat("Retrieving %s for %s", name, routine.key()));

      final MutableFunction function = (MutableFunction) routine;
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getFunctionColumns(
                          function.getSchema().getCatalogName(),
                          function.getSchema().getName(),
                          function.getName(),
                          null),
                  "DatabaseMetaData::getFunctionColumns"); ) {
        while (results.next()) {
          retrievalCounts.count();
          final boolean added = createFunctionParameter(results, allRoutines, parameterFilter);
          retrievalCounts.countIfIncluded(added);
        }
      } catch (final AbstractMethodError e) {
        logSQLFeatureNotSupported(
            new StringFormat("Could not retrieve parameters for function %s", function), e);
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(
            new StringFormat("Could not retrieve parameters for function %s", function), e);
      }
    }
    retrievalCounts.log();
  }

  private void retrieveFunctionParametersOverSchemas(
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<FunctionParameter> parameterFilter)
      throws SQLException {

    final Collection<Schema> schemas = catalog.getSchemas();

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(FUNCTION_COLUMNS)) {
      throw new ExecutionRuntimeException("No function parameters SQL provided");
    }
    final Query functionColumnsSql = informationSchemaViews.getQuery(FUNCTION_COLUMNS);

    final String name = "function parameters from data dictionary over schemas";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getRoutines(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(functionColumnsSql, statement, getLimitMap(schema)); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          final boolean added = createFunctionParameter(results, allRoutines, parameterFilter);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve function parameters for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }
}
