/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveAdditionalColumnAttributes;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveAdditionalColumnMetadata;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveAdditionalTableAttributes;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveColumnDataTypes;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveDatabaseInfo;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveDatabaseUsers;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveForeignKeys;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveIndexInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveIndexes;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrievePrimaryKeys;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveRoutineInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveRoutineParameters;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveRoutines;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveSequenceInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveServerInfo;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveSynonymInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableColumnPrivileges;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableColumns;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableConstraintDefinitions;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableConstraintInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableConstraints;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableDefinitionsInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTablePrivileges;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTables;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTriggerInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveViewInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveViewTableUsage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.string.StringFormat;

/** SchemaCrawler uses database meta-data to get the details about the schema. */
public final class SchemaCrawler {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class.getName());

  private final SchemaCrawlerOptions options;
  private final RetrieverConnection retrieverConnection;
  private final SchemaInfoLevel infoLevel;
  private final RetrievalStopWatch stopWatch;
  private MutableCatalog catalog;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection An database connection.
   * @param schemaRetrievalOptions Database-specific schema retrieval overrides
   * @param options SchemaCrawler options
   * @throws SQLException
   */
  public SchemaCrawler(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions options) {
    try {
      retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);
      this.options = requireNonNull(options, "No SchemaCrawler options provided");
      infoLevel = options.getLoadOptions().getSchemaInfoLevel();
      stopWatch = new RetrievalStopWatch(infoLevel);
    } catch (final SQLException e) {
      throw new DatabaseAccessException(e.getMessage(), e);
    }
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @return Database metadata
   */
  public Catalog crawl() {
    try {
      catalog = new MutableCatalog("catalog", retrieverConnection.getConnectionInfo());

      crawlDatabaseInfo();
      LOGGER.log(Level.INFO, String.format("%n%s", catalog.getCrawlInfo()));

      crawlSchemas();
      crawlColumnDataTypes();
      crawlTables();
      crawlRoutines();
      crawlSynonyms();
      crawlSequences();

      return catalog;
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e.getMessage(), e);
    } finally {
      stopWatch.stopAndLogTime();
    }
  }

  private void crawlAdditionalTableColumnInformation(final TableExtRetriever retrieverExtra)
      throws Exception {
    LOGGER.log(Level.INFO, "Retrieving additional table column information");
    stopWatch.time(
        retrieveAdditionalColumnAttributes,
        retrieverExtra::retrieveAdditionalColumnAttributes,
        retrieveTableColumns);
    stopWatch.time(
        retrieveAdditionalColumnMetadata,
        retrieverExtra::retrieveAdditionalColumnMetadata,
        retrieveTableColumns);
    stopWatch.time(
        retrieveTableColumnPrivileges,
        retrieverExtra::retrieveTableColumnPrivileges,
        retrieveTableColumns);
  }

  private void crawlAdditionalTableInformation(
      final TableConstraintRetriever constraintRetriever, final TableExtRetriever retrieverExtra)
      throws Exception {
    stopWatch.time(
        retrieveTableConstraintDefinitions,
        constraintRetriever::retrieveTableConstraintDefinitions,
        retrieveTableConstraints);

    stopWatch.time(retrieveViewInformation, retrieverExtra::retrieveViewInformation);
    stopWatch.time(retrieveViewTableUsage, retrieverExtra::retrieveViewTableUsage);
    stopWatch.time(retrieveTableDefinitionsInformation, retrieverExtra::retrieveTableDefinitions);
    stopWatch.time(
        retrieveIndexInformation, () -> retrieverExtra.retrieveIndexInformation(), retrieveIndexes);

    stopWatch.time(
        retrieveAdditionalTableAttributes,
        () -> retrieverExtra.retrieveAdditionalTableAttributes());
    stopWatch.time(retrieveTablePrivileges, () -> retrieverExtra.retrieveTablePrivileges());
  }

  private void crawlColumnDataTypes() throws Exception {

    final DataTypeRetriever retriever =
        new DataTypeRetriever(retrieverConnection, catalog, options);

    stopWatch.time(retrieveColumnDataTypes, retriever::retrieveSystemColumnDataTypes);

    stopWatch.time(
        retrieveUserDefinedColumnDataTypes, retriever::retrieveUserDefinedColumnDataTypes);
  }

  private void crawlDatabaseInfo() throws Exception {

    if (!infoLevel.is(retrieveDatabaseInfo)) {
      LOGGER.log(Level.INFO, "Not retrieving database information, since this was not requested");
      return;
    }

    final DatabaseInfoRetriever retriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    stopWatch.time(retrieveAdditionalDatabaseInfo, retriever::retrieveAdditionalDatabaseInfo);

    stopWatch.time(retrieveServerInfo, retriever::retrieveServerInfo);
    stopWatch.time(retrieveDatabaseUsers, retriever::retrieveDatabaseUsers);

    stopWatch.time(retrieveAdditionalJdbcDriverInfo, retriever::retrieveAdditionalJdbcDriverInfo);
  }

  private void crawlRoutines() throws Exception {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveRoutines) && !limitOptions.isExcludeAll(ruleForRoutineInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving routines, since this was not requested");
      return;
    }

    final RoutineRetriever retriever = new RoutineRetriever(retrieverConnection, catalog, options);
    final RoutineExtRetriever retrieverExtra =
        new RoutineExtRetriever(retrieverConnection, catalog, options);
    final ProcedureParameterRetriever procedureParameterRetriever =
        new ProcedureParameterRetriever(retrieverConnection, catalog, options);
    final FunctionParameterRetriever functionParameterRetriever =
        new FunctionParameterRetriever(retrieverConnection, catalog, options);

    final Collection<RoutineType> routineTypes = limitOptions.getRoutineTypes();

    stopWatch.time(
        retrieveRoutines,
        () -> retriever.retrieveRoutines(routineTypes, limitOptions.get(ruleForRoutineInclusion)));

    final NamedObjectList<MutableRoutine> allRoutines = catalog.getAllRoutines();
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d routines", allRoutines.size()));
    if (allRoutines.isEmpty()) {
      return;
    }

    stopWatch.time(
        retrieveRoutineParameters,
        () -> {
          LOGGER.log(Level.INFO, "Retrieving routine columns");
          if (!limitOptions.isExcludeAll(ruleForRoutineParameterInclusion)) {
            if (routineTypes.contains(RoutineType.procedure)) {
              procedureParameterRetriever.retrieveProcedureParameters(
                  allRoutines, limitOptions.get(ruleForRoutineParameterInclusion));
            }

            if (routineTypes.contains(RoutineType.function)) {
              functionParameterRetriever.retrieveFunctionParameters(
                  allRoutines, limitOptions.get(ruleForRoutineParameterInclusion));
            }
          }
        });

    stopWatch.time(
        "filterAndSortRoutines",
        () -> {
          // Filter the list of routines based on grep criteria
          catalog.reduce(Routine.class, getRoutineReducer(options));
        });

    stopWatch.time(retrieveRoutineInformation, retrieverExtra::retrieveRoutineInformation);
  }

  private void crawlSchemas() throws Exception {

    final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection, catalog, options);

    stopWatch.time(
        "retrieveSchemas",
        () -> retriever.retrieveSchemas(options.getLimitOptions().get(ruleForSchemaInclusion)));

    stopWatch.time(
        "filterAndSortSchemas", () -> catalog.reduce(Schema.class, getSchemaReducer(options)));

    final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
    if (schemas.isEmpty()) {
      throw new ExecutionRuntimeException("No matching schemas found");
    }
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d schemas", schemas.size()));
  }

  private void crawlSequences() throws Exception {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSequenceInformation)
        && !limitOptions.isExcludeAll(ruleForSequenceInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    final SequenceRetriever retrieverExtra =
        new SequenceRetriever(retrieverConnection, catalog, options);

    stopWatch.time(
        retrieveSequenceInformation,
        () ->
            retrieverExtra.retrieveSequenceInformation(limitOptions.get(ruleForSequenceInclusion)));

    stopWatch.time(
        "filterAndSortSequences",
        () -> catalog.reduce(Sequence.class, getSequenceReducer(options)));
  }

  private void crawlSynonyms() throws Exception {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSynonymInformation)
        && !limitOptions.isExcludeAll(ruleForSynonymInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    final SynonymRetriever retrieverExtra =
        new SynonymRetriever(retrieverConnection, catalog, options);

    stopWatch.time(
        retrieveSynonymInformation,
        () -> retrieverExtra.retrieveSynonymInformation(limitOptions.get(ruleForSynonymInclusion)));

    stopWatch.time(
        "filterAndSortSynonms", () -> catalog.reduce(Synonym.class, getSynonymReducer(options)));
  }

  private void crawlTables() throws Exception {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveTables) && !limitOptions.isExcludeAll(ruleForTableInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving tables, since this was not requested");
      return;
    }

    final TableRetriever retriever = new TableRetriever(retrieverConnection, catalog, options);
    final TableColumnRetriever columnRetriever =
        new TableColumnRetriever(retrieverConnection, catalog, options);
    final PrimaryKeyRetriever pkRetriever =
        new PrimaryKeyRetriever(retrieverConnection, catalog, options);
    final ForeignKeyRetriever fkRetriever =
        new ForeignKeyRetriever(retrieverConnection, catalog, options);
    final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);
    final TableExtRetriever retrieverExtra =
        new TableExtRetriever(retrieverConnection, catalog, options);
    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);

    stopWatch.time(
        retrieveTables,
        () -> {
          LOGGER.log(Level.INFO, "Retrieving table names");
          retriever.retrieveTables(
              limitOptions.getTableNamePattern(),
              limitOptions.getTableTypes(),
              limitOptions.get(ruleForTableInclusion));
        });

    final NamedObjectList<MutableTable> allTables = catalog.getAllTables();
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d tables", allTables.size()));
    if (allTables.isEmpty()) {
      return;
    }

    stopWatch.time(
        retrieveTableColumns,
        () -> {
          if (!limitOptions.isExcludeAll(ruleForColumnInclusion)) {
            columnRetriever.retrieveTableColumns(
                allTables, limitOptions.get(ruleForColumnInclusion));
          }
        });

    stopWatch.time(
        retrievePrimaryKeys,
        () -> pkRetriever.retrievePrimaryKeys(allTables),
        retrieveTableColumns);

    stopWatch.time(
        retrieveForeignKeys,
        () -> fkRetriever.retrieveForeignKeys(allTables),
        retrieveTableColumns);

    stopWatch.time(
        "filterAndSortTables",
        () -> {
          // Filter the list of tables based on grep criteria, and
          // parent-child relationships
          catalog.reduce(Table.class, getTableReducer(options));

          // Sort the remaining tables
          final TablesGraph tablesGraph = new TablesGraph(allTables);
          tablesGraph.setTablesSortIndexes();
        });

    stopWatch.time(
        retrieveIndexes, () -> indexRetriever.retrieveIndexes(allTables), retrieveTableColumns);

    LOGGER.log(Level.INFO, "Retrieving additional table information");
    stopWatch.time(retrieveTableConstraints, constraintRetriever::retrieveTableConstraints);
    stopWatch.time(
        retrieveTableConstraintInformation,
        constraintRetriever::retrieveTableConstraintInformation,
        retrieveTableConstraints);
    // Required step: Match all constraints such as primary keys and foreign keys
    stopWatch.time(
        "matchTableConstraints",
        () -> constraintRetriever.matchTableConstraints(allTables),
        retrieveTableColumns);

    stopWatch.time(retrieveTriggerInformation, retrieverExtra::retrieveTriggerInformation);

    crawlAdditionalTableInformation(constraintRetriever, retrieverExtra);
    crawlAdditionalTableColumnInformation(retrieverExtra);
  }
}
