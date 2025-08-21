/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.InformationSchemaKey.PROCEDURE_COLUMNS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.string.StringFormat;

/**
 * A retriever uses database metadata to get the details about the database procedure parameters.
 */
final class ProcedureParameterRetriever extends AbstractRetriever {

  private static final Logger LOGGER =
      Logger.getLogger(ProcedureParameterRetriever.class.getName());

  ProcedureParameterRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options) {
    super(retrieverConnection, catalog, options);
  }

  void retrieveProcedureParameters(
      final NamedObjectList<MutableRoutine> allRoutines, final InclusionRule parameterInclusionRule)
      throws SQLException {
    requireNonNull(allRoutines, "No procedures provided");

    final InclusionRuleFilter<ProcedureParameter> parameterFilter =
        new InclusionRuleFilter<>(parameterInclusionRule, true);
    if (parameterFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving procedure parameters, since this was not requested");
      return;
    }

    switch (getRetrieverConnection().get(procedureParametersRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(
            Level.INFO, "Retrieving procedure parameters, using fast data dictionary retrieval");
        retrieveProcedureParametersFromDataDictionary(allRoutines, parameterFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving procedure parameters");
        retrieveProcedureParametersFromMetadata(allRoutines, parameterFilter);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving procedure parameters");
        break;
    }
  }

  private boolean createProcedureParameter(
      final MetadataResultSet results,
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<ProcedureParameter> parameterFilter) {
    final String columnCatalogName = normalizeCatalogName(results.getString("PROCEDURE_CAT"));
    final String schemaName = normalizeSchemaName(results.getString("PROCEDURE_SCHEM"));
    final String procedureName = results.getString("PROCEDURE_NAME");
    String columnName = results.getString("COLUMN_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");

    final ParameterModeType parameterMode =
        getProcedureParameterMode(
            results.getInt("COLUMN_TYPE", DatabaseMetaData.procedureColumnUnknown));

    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Retrieving procedure parameter <%s.%s.%s.%s.%s>",
            columnCatalogName, schemaName, procedureName, specificName, columnName));
    if (isBlank(columnName) && parameterMode == ParameterModeType.result) {
      columnName = "<return value>";
    }
    if (isBlank(columnName)) {
      return false;
    }

    final Optional<MutableRoutine> optionalRoutine =
        allRoutines.lookup(
            new NamedObjectKey(columnCatalogName, schemaName, procedureName, specificName));
    if (!optionalRoutine.isPresent()) {
      return false;
    }

    final MutableRoutine routine = optionalRoutine.get();
    if (routine.getRoutineType() != RoutineType.procedure) {
      return false;
    }

    final MutableProcedure procedure = (MutableProcedure) routine;
    final MutableProcedureParameter parameter =
        lookupOrCreateProcedureParameter(procedure, columnName);
    parameter.withQuoting(getRetrieverConnection().getIdentifiers());
    if (parameterFilter.test(parameter)
        && belongsToSchema(procedure, columnCatalogName, schemaName)) {
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int length = results.getInt("LENGTH", 0);
      final int precision = results.getInt("PRECISION", 0);
      final boolean isNullable =
          results.getShort("NULLABLE", (short) DatabaseMetaData.procedureNullableUnknown) > 0;
      final String remarks = results.getString("REMARKS");
      parameter.setOrdinalPosition(ordinalPosition);
      parameter.setParameterMode(parameterMode);
      parameter.setColumnDataType(
          lookupOrCreateColumnDataType(user_defined, procedure.getSchema(), dataType, typeName));
      parameter.setSize(length);
      parameter.setPrecision(precision);
      parameter.setNullable(isNullable);
      parameter.setRemarks(remarks);

      parameter.addAttributes(results.getAttributes());

      LOGGER.log(Level.FINER, new StringFormat("Adding parameter to procedure <%s>", parameter));
      procedure.addParameter(parameter);
      return true;
    }
    return false;
  }

  private ParameterModeType getProcedureParameterMode(final int columnType) {
    switch (columnType) {
      case DatabaseMetaData.procedureColumnIn:
        return ParameterModeType.in;
      case DatabaseMetaData.procedureColumnInOut:
        return ParameterModeType.inOut;
      case DatabaseMetaData.procedureColumnOut:
        return ParameterModeType.out;
      case DatabaseMetaData.procedureColumnResult:
        return ParameterModeType.result;
      case DatabaseMetaData.procedureColumnReturn:
        return ParameterModeType.returnValue;
      default:
        return ParameterModeType.unknown;
    }
  }

  private MutableProcedureParameter lookupOrCreateProcedureParameter(
      final MutableProcedure procedure, final String columnName) {
    final Optional<MutableProcedureParameter> parameterOptional =
        procedure.lookupParameter(columnName);
    return parameterOptional.orElseGet(() -> new MutableProcedureParameter(procedure, columnName));
  }

  private void retrieveProcedureParametersFromDataDictionary(
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<ProcedureParameter> parameterFilter)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(PROCEDURE_COLUMNS)) {
      throw new ExecutionRuntimeException("No procedure parameters SQL provided");
    }
    final String name = "procedure parameters from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query procedureColumnsSql = informationSchemaViews.getQuery(PROCEDURE_COLUMNS);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(procedureColumnsSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = createProcedureParameter(results, allRoutines, parameterFilter);
        retrievalCounts.countIfIncluded(added);
      }
    }
    retrievalCounts.log();
  }

  private void retrieveProcedureParametersFromMetadata(
      final NamedObjectList<MutableRoutine> allRoutines,
      final InclusionRuleFilter<ProcedureParameter> parameterFilter)
      throws SQLException {
    final String name = "procedure parameters from metadata";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final MutableRoutine routine : allRoutines) {
      if (routine.getRoutineType() != RoutineType.procedure) {
        continue;
      }
      LOGGER.log(Level.INFO, new StringFormat("Retrieving %s for %s", name, routine));

      final MutableProcedure procedure = (MutableProcedure) routine;
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getProcedureColumns(
                          procedure.getSchema().getCatalogName(),
                          procedure.getSchema().getName(),
                          procedure.getName(),
                          null),
                  "DatabaseMetaData::getProcedureColumns"); ) {
        while (results.next()) {
          retrievalCounts.count();
          final boolean added = createProcedureParameter(results, allRoutines, parameterFilter);
          retrievalCounts.countIfIncluded(added);
        }
      } catch (final SQLException e) {
        throw new WrappedSQLException(
            String.format("Could not retrieve procedure parameters for procedure <%s>", procedure),
            e);
      }
    }
    retrievalCounts.log();
  }
}
