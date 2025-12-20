/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveRoutineReferences;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveRoutines;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveSequenceInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveServerInfo;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveSynonymInformation;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableColumnPrivileges;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableColumns;
import static schemacrawler.schemacrawler.SchemaInfoRetrieval.retrieveTableConstraintColumns;
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
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.database.ConnectionInfoBuilder;
import us.fatehi.utility.database.DatabaseInformation;
import us.fatehi.utility.database.JdbcDriverInformation;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.string.StringFormat;

/** SchemaCrawler uses database meta-data to get the details about the schema. */
public final class SchemaCrawler {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class.getName());

  private final SchemaCrawlerOptions options;
  private final RetrieverConnection retrieverConnection;
  private final SchemaInfoLevel infoLevel;
  private final int maxThreads;
  private RetrievalTaskRunner taskRunner;
  private MutableCatalog catalog;

  /**
   * Constructs a SchemaCrawler object, from a database connection source.
   *
   * @param dataSource A database connection source.
   * @param schemaRetrievalOptions Database-specific schema retrieval overrides
   * @param options SchemaCrawler options
   */
  public SchemaCrawler(
      final DatabaseConnectionSource dataSource,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions options) {
    try {
      retrieverConnection = new RetrieverConnection(dataSource, schemaRetrievalOptions);
      this.options = requireNonNull(options, "No SchemaCrawler options provided");

      final LoadOptions loadOptions = options.loadOptions();
      infoLevel = loadOptions.schemaInfoLevel();
      maxThreads = loadOptions.maxThreads();

    } catch (final SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @return Database metadata
   */
  public Catalog crawl() {
    try {
      try (final Connection connection =
          retrieverConnection.getConnection("crawl connection information"); ) {
        final ConnectionInfoBuilder connectionInfoBuilder =
            ConnectionInfoBuilder.builder(connection);
        final DatabaseInformation databaseInfo = connectionInfoBuilder.buildDatabaseInformation();
        final JdbcDriverInformation jdbcDriverInfo =
            connectionInfoBuilder.buildJdbcDriverInformation();
        LOGGER.log(
            Level.CONFIG,
            new StringFormat(
                "Making a database connection to:%n%s%s", databaseInfo, jdbcDriverInfo));
        catalog =
            new MutableCatalog(
                "catalog",
                new MutableDatabaseInfo(databaseInfo),
                new MutableJdbcDriverInfo(jdbcDriverInfo));
      }

      final String runId = catalog.getCrawlInfo().getRunId();
      taskRunner = new RetrievalTaskRunner(runId, infoLevel, maxThreads);

      crawlDatabaseInfo();
      LOGGER.log(Level.INFO, "%n%s".formatted(catalog.getCrawlInfo()));

      crawlSchemas();
      crawlColumnDataTypes();
      crawlTables();
      crawlRoutines();
      crawlSynonyms();
      crawlSequences();
      postCrawl();

      return catalog;
    } catch (final RuntimeException e) {
      throw e;
    } catch (final SQLException e) {
      throw new DatabaseAccessException(e);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    } finally {
      if (taskRunner != null) {
        taskRunner.stopAndLogTime();
      }
    }
  }

  private void crawlColumnDataTypes() throws Exception {

    final DataTypeRetriever retriever =
        new DataTypeRetriever(retrieverConnection, catalog, options);

    taskRunner.add(retrieveColumnDataTypes, retriever::retrieveSystemColumnDataTypes).submit();

    taskRunner
        .add(retrieveUserDefinedColumnDataTypes, retriever::retrieveUserDefinedColumnDataTypes)
        .submit();
  }

  private void crawlDatabaseInfo() throws Exception {

    if (!infoLevel.is(retrieveDatabaseInfo)) {
      LOGGER.log(Level.INFO, "Not retrieving database information, since this was not requested");
      return;
    }

    final DatabaseInfoRetriever retriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);

    taskRunner
        .add(retrieveAdditionalDatabaseInfo, retriever::retrieveAdditionalDatabaseInfo)
        .add(retrieveServerInfo, retriever::retrieveServerInfo)
        .add(retrieveDatabaseUsers, retriever::retrieveDatabaseUsers)
        .add(retrieveAdditionalJdbcDriverInfo, retriever::retrieveAdditionalJdbcDriverInfo)
        .submit();
  }

  private void crawlRoutines() throws Exception {

    final LimitOptions limitOptions = options.limitOptions();
    if (!infoLevel.is(retrieveRoutines) || limitOptions.isExcludeAll(ruleForRoutineInclusion)) {
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

    final Collection<RoutineType> routineTypes = limitOptions.routineTypes();

    taskRunner
        .add(
            retrieveRoutines,
            () ->
                retriever.retrieveRoutines(routineTypes, limitOptions.get(ruleForRoutineInclusion)))
        .submit();

    final NamedObjectList<MutableRoutine> allRoutines = catalog.getAllRoutines();
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d routines", allRoutines.size()));
    if (allRoutines.isEmpty()) {
      return;
    }

    taskRunner
        .add(
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
            })
        .submit();

    taskRunner
        .add(
            "filterAndSortRoutines",
            () -> {
              // Filter the list of routines based on grep criteria
              catalog.reduce(Routine.class, getRoutineReducer(options));
            })
        .submit();

    taskRunner.add(retrieveRoutineInformation, retrieverExtra::retrieveRoutineInformation).submit();
    taskRunner.add(retrieveRoutineReferences, retrieverExtra::retrieveRoutineReferences).submit();
  }

  private void crawlSchemas() throws Exception {

    final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection, catalog, options);

    taskRunner
        .add(
            "retrieveSchemas",
            () -> retriever.retrieveSchemas(options.limitOptions().get(ruleForSchemaInclusion)))
        .submit();

    taskRunner
        .add("filterAndSortSchemas", () -> catalog.reduce(Schema.class, getSchemaReducer(options)))
        .submit();

    final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
    if (schemas.isEmpty()) {
      throw new ExecutionRuntimeException("No matching schemas found");
    }
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d schemas", schemas.size()));
  }

  private void crawlSequences() throws Exception {

    final LimitOptions limitOptions = options.limitOptions();
    if (!infoLevel.is(retrieveSequenceInformation)
        || limitOptions.isExcludeAll(ruleForSequenceInclusion)) {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    final SequenceRetriever retrieverExtra =
        new SequenceRetriever(retrieverConnection, catalog, options);

    taskRunner
        .add(
            retrieveSequenceInformation,
            () ->
                retrieverExtra.retrieveSequenceInformation(
                    limitOptions.get(ruleForSequenceInclusion)))
        .submit();

    taskRunner
        .add(
            "filterAndSortSequences",
            () -> catalog.reduce(Sequence.class, getSequenceReducer(options)))
        .submit();
  }

  private void crawlSynonyms() throws Exception {

    final LimitOptions limitOptions = options.limitOptions();
    if (!infoLevel.is(retrieveSynonymInformation)
        || limitOptions.isExcludeAll(ruleForSynonymInclusion)) {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    final SynonymRetriever retrieverExtra =
        new SynonymRetriever(retrieverConnection, catalog, options);

    taskRunner
        .add(
            retrieveSynonymInformation,
            () ->
                retrieverExtra.retrieveSynonymInformation(
                    limitOptions.get(ruleForSynonymInclusion)))
        .submit();

    taskRunner
        .add(
            "filterAndSortSynonms", () -> catalog.reduce(Synonym.class, getSynonymReducer(options)))
        .submit();
  }

  private void crawlTables() throws Exception {

    final LimitOptions limitOptions = options.limitOptions();
    if (!infoLevel.is(retrieveTables) || limitOptions.isExcludeAll(ruleForTableInclusion)) {
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
    final TableConstraintMatcher constraintMatcher =
        new TableConstraintMatcher(retrieverConnection, catalog, options);
    final TableExtRetriever retrieverExtra =
        new TableExtRetriever(retrieverConnection, catalog, options);
    final ViewExtRetriever viewExtRetriever =
        new ViewExtRetriever(retrieverConnection, catalog, options);
    final TriggerRetriever triggerRetriever =
        new TriggerRetriever(retrieverConnection, catalog, options);
    final TablePrivilegeRetriever retrieverPrivilege =
        new TablePrivilegeRetriever(retrieverConnection, catalog, options);
    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);

    taskRunner
        .add(
            retrieveTables,
            () -> {
              LOGGER.log(Level.INFO, "Retrieving table names");
              retriever.retrieveTables(
                  limitOptions.tableNamePattern(),
                  limitOptions.tableTypes(),
                  limitOptions.get(ruleForTableInclusion));
            })
        .submit();

    final NamedObjectList<MutableTable> allTables = catalog.getAllTables();
    LOGGER.log(Level.INFO, new StringFormat("Retrieved %d tables", allTables.size()));
    if (allTables.isEmpty()) {
      return;
    }

    taskRunner
        .add(
            retrieveTableColumns,
            () -> {
              if (!limitOptions.isExcludeAll(ruleForColumnInclusion)) {
                columnRetriever.retrieveTableColumns(
                    allTables, limitOptions.get(ruleForColumnInclusion));
              }
            })
        .submit();

    taskRunner
        .add(
            retrievePrimaryKeys,
            () -> pkRetriever.retrievePrimaryKeys(allTables),
            retrieveTableColumns)
        .add(
            retrieveForeignKeys,
            () -> fkRetriever.retrieveForeignKeys(allTables),
            retrieveTableColumns)
        .add(retrieveIndexes, () -> indexRetriever.retrieveIndexes(allTables), retrieveTableColumns)
        .add(
            retrieveTableConstraints,
            constraintRetriever::retrieveTableConstraints,
            retrieveTableColumns)
        .submit();

    taskRunner
        .add(
            retrieveTableConstraintColumns,
            constraintRetriever::retrieveTableConstraintColumns,
            retrieveTableConstraints)
        .add(retrieveTriggerInformation, triggerRetriever::retrieveTriggerInformation)
        .submit();

    // Should be run independently, since filter and sort modifies the tables collection
    taskRunner
        .add(
            "filterAndSortTables",
            () -> {
              // Filter the list of tables based on grep criteria, and
              // parent-child relationships
              catalog.reduce(Table.class, getTableReducer(options));

              // Sort the remaining tables
              final TablesGraph tablesGraph = new TablesGraph(allTables);
              tablesGraph.setTablesSortIndexes();
            })
        .submit();

    // Should be run independently, since table constraints are modified
    taskRunner
        .add(
            "matchTableConstraints",
            () -> constraintMatcher.matchTableConstraints(allTables),
            retrieveTableColumns)
        .submit();

    taskRunner
        .add(
            retrieveTableConstraintDefinitions,
            constraintRetriever::retrieveCheckConstraints,
            retrieveTableConstraints)
        .add(
            retrieveTableConstraintInformation,
            constraintRetriever::retrieveTableConstraintInformation,
            retrieveTableConstraints)
        .add(retrieveViewInformation, viewExtRetriever::retrieveViewInformation, retrieveTables)
        .add(retrieveViewTableUsage, viewExtRetriever::retrieveViewTableUsage, retrieveTables)
        .add(
            retrieveTableDefinitionsInformation,
            retrieverExtra::retrieveTableDefinitions,
            retrieveTables)
        .add(
            retrieveIndexInformation,
            () -> indexRetriever.retrieveIndexInformation(),
            retrieveIndexes)
        .add(
            retrieveAdditionalTableAttributes,
            () -> retrieverExtra.retrieveAdditionalTableAttributes(),
            retrieveTables)
        .add(
            retrieveTablePrivileges,
            () -> retrieverPrivilege.retrieveTablePrivileges(),
            retrieveTables)
        .add(
            retrieveTableColumnPrivileges,
            retrieverPrivilege::retrieveTableColumnPrivileges,
            retrieveTableColumns)
        .add(
            retrieveAdditionalColumnAttributes,
            retrieverExtra::retrieveAdditionalColumnAttributes,
            retrieveTableColumns)
        .submit();

    taskRunner
        .add(
            retrieveAdditionalColumnMetadata,
            retrieverExtra::retrieveAdditionalColumnMetadata,
            retrieveTableColumns)
        .submit();
  }

  private void postCrawl() throws Exception {
    final TableReferencesMatcher referencesMatcher =
        new TableReferencesMatcher(retrieverConnection, catalog, options);
    taskRunner
        .add(
            "collectTableReferences",
            () -> referencesMatcher.collectTableReferences(),
            retrieveTables)
        .submit();
  }
}
