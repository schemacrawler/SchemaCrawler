/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.schemacrawler.InformationSchemaKey.FUNCTIONS;
import static schemacrawler.schemacrawler.InformationSchemaKey.PROCEDURES;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about the database procedures. */
final class RoutineRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(RoutineRetriever.class.getName());

  RoutineRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void retrieveRoutines(
      final Collection<RoutineType> routineTypes, final InclusionRule routineInclusionRule)
      throws SQLException {

    requireNonNull(routineTypes, "No routine types provided");

    if (routineTypes.contains(RoutineType.procedure)) {
      LOGGER.log(Level.INFO, "Retrieving procedure names");
      retrieveProcedures(routineInclusionRule);
    }
    if (routineTypes.contains(RoutineType.function)) {
      LOGGER.log(Level.INFO, "Retrieving function names");
      retrieveFunctions(routineInclusionRule);
    }
  }

  private void createFunction(
      final MetadataResultSet results,
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Function> functionFilter) {
    final String catalogName = normalizeCatalogName(results.getString("FUNCTION_CAT"));
    final String schemaName = normalizeSchemaName(results.getString("FUNCTION_SCHEM"));
    final String functionName = results.getString("FUNCTION_NAME");
    LOGGER.log(
        Level.FINE,
        new StringFormat("Retrieving function <%s.%s.%s>", catalogName, schemaName, functionName));

    if (isBlank(functionName)) {
      return;
    }

    final FunctionReturnType functionType =
        results.getEnumFromShortId("FUNCTION_TYPE", FunctionReturnType.unknown);
    final String remarks = results.getString("REMARKS");
    final String specificName = results.getString("SPECIFIC_NAME");

    final Optional<SchemaReference> optionalSchema =
        schemas.lookup(new NamedObjectKey(catalogName, schemaName));
    if (!optionalSchema.isPresent()) {
      return;
    }
    final Schema schema = optionalSchema.get();

    final MutableFunction function = new MutableFunction(schema, functionName, specificName);
    if (functionFilter.test(function)) {
      function.setReturnType(functionType);
      function.setRemarks(remarks);
      function.addAttributes(results.getAttributes());

      catalog.addRoutine(function);
    }
  }

  private void createProcedure(
      final MetadataResultSet results,
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Procedure> procedureFilter) {
    final String catalogName = normalizeCatalogName(results.getString("PROCEDURE_CAT"));
    final String schemaName = normalizeSchemaName(results.getString("PROCEDURE_SCHEM"));
    final String procedureName = results.getString("PROCEDURE_NAME");
    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Retrieving procedure <%s.%s.%s>", catalogName, schemaName, procedureName));
    if (isBlank(procedureName)) {
      return;
    }
    final ProcedureReturnType procedureType =
        results.getEnumFromShortId("PROCEDURE_TYPE", ProcedureReturnType.unknown);
    final String remarks = results.getString("REMARKS");
    final String specificName = results.getString("SPECIFIC_NAME");

    final Optional<SchemaReference> optionalSchema =
        schemas.lookup(new NamedObjectKey(catalogName, schemaName));
    if (!optionalSchema.isPresent()) {
      return;
    }
    final Schema schema = optionalSchema.get();

    final MutableProcedure procedure = new MutableProcedure(schema, procedureName, specificName);
    if (procedureFilter.test(procedure)) {
      procedure.setReturnType(procedureType);
      procedure.setRemarks(remarks);
      procedure.addAttributes(results.getAttributes());

      catalog.addRoutine(procedure);
    }
  }

  private void retrieveFunctions(final InclusionRule routineInclusionRule) throws SQLException {

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final InclusionRuleFilter<Function> functionFilter =
        new InclusionRuleFilter<>(routineInclusionRule, false);
    if (functionFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving functions, since this was not requested");
      return;
    }

    switch (getRetrieverConnection().get(functionsRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving functions, using fast data dictionary retrieval");
        retrieveFunctionsFromDataDictionary(schemas, functionFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving functions");
        retrieveFunctionsFromMetadata(schemas, functionFilter);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving functions");
        break;
    }
  }

  private void retrieveFunctionsFromDataDictionary(
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Function> functionFilter)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(FUNCTIONS)) {
      throw new ExecutionRuntimeException("No functions SQL provided");
    }
    final Query functionsSql = informationSchemaViews.getQuery(FUNCTIONS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(functionsSql, statement, getSchemaInclusionRule()); ) {
      int numFunctions = 0;
      while (results.next()) {
        numFunctions = numFunctions + 1;
        createFunction(results, schemas, functionFilter);
      }
      LOGGER.log(Level.INFO, new StringFormat("Processed %d functions", numFunctions));
    }
  }

  private void retrieveFunctionsFromMetadata(
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Function> functionFilter) {
    for (final Schema schema : schemas) {
      LOGGER.log(Level.INFO, new StringFormat("Retrieving functions for schema <%s>", schema));

      final String catalogName = schema.getCatalogName();
      final String schemaName = schema.getName();

      try (final Connection connection = getRetrieverConnection().getConnection();
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection.getMetaData().getFunctions(catalogName, schemaName, null),
                  "DatabaseMetaData::getFunctions"); ) {
        int numFunctions = 0;
        while (results.next()) {
          numFunctions = numFunctions + 1;
          createFunction(results, schemas, functionFilter);
        }
        LOGGER.log(Level.INFO, new StringFormat("Processed %d functions", numFunctions));
      } catch (final AbstractMethodError e) {
        logSQLFeatureNotSupported(new StringFormat("Could not retrieve functions"), e);
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(new StringFormat("Could not retrieve functions"), e);
      }
    }
  }

  private void retrieveProcedures(final InclusionRule routineInclusionRule) throws SQLException {

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final InclusionRuleFilter<Procedure> procedureFilter =
        new InclusionRuleFilter<>(routineInclusionRule, false);
    if (procedureFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving procedures, since this was not requested");
      return;
    }

    switch (getRetrieverConnection().get(proceduresRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving procedures, using fast data dictionary retrieval");
        retrieveProceduresFromDataDictionary(schemas, procedureFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving procedures");
        retrieveProceduresFromMetadata(schemas, procedureFilter);
        break;

      default:
        break;
    }
  }

  private void retrieveProceduresFromDataDictionary(
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Procedure> procedureFilter)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(PROCEDURES)) {
      throw new ExecutionRuntimeException("No procedures SQL provided");
    }
    final Query proceduresSql = informationSchemaViews.getQuery(PROCEDURES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(proceduresSql, statement, getSchemaInclusionRule()); ) {
      int numProcedures = 0;
      while (results.next()) {
        numProcedures = numProcedures + 1;
        createProcedure(results, schemas, procedureFilter);
      }
      LOGGER.log(Level.INFO, new StringFormat("Processed %d procedures", numProcedures));
    }
  }

  private void retrieveProceduresFromMetadata(
      final NamedObjectList<SchemaReference> schemas,
      final InclusionRuleFilter<Procedure> procedureFilter)
      throws SQLException {
    for (final Schema schema : schemas) {
      LOGGER.log(Level.INFO, new StringFormat("Retrieving procedures for schema <%s>", schema));

      final String catalogName = schema.getCatalogName();
      final String schemaName = schema.getName();

      try (final Connection connection = getRetrieverConnection().getConnection();
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection.getMetaData().getProcedures(catalogName, schemaName, null),
                  "DatabaseMetaData::getProcedures"); ) {
        int numProcedures = 0;
        while (results.next()) {
          numProcedures = numProcedures + 1;
          createProcedure(results, schemas, procedureFilter);
        }
        LOGGER.log(Level.INFO, new StringFormat("Processed %d procedures", numProcedures));
      }
    }
  }
}
