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

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import us.fatehi.utility.string.StringFormat;

/**
 * SchemaCrawler uses database meta-data to get the details about the schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(SchemaCrawler.class.getName());

  private final Connection connection;
  private final SchemaCrawlerOptions options;
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private MutableCatalog catalog;
  private RetrieverConnection retrieverConnection;
  private final SchemaInfoLevel infoLevel;
  private final RetrievalStopWatch stopWatch;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection An database connection.
   * @param schemaRetrievalOptions Database-specific schema retrieval overrides
   * @param options SchemaCrawler options
   */
  public SchemaCrawler(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions options) {
    this.connection = requireNonNull(connection, "No connection specified");
    this.schemaRetrievalOptions =
        requireNonNull(
            schemaRetrievalOptions, "No database-specific schema retrieval overrides provided");
    this.options = requireNonNull(options, "No SchemaCrawler options provided");
    infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    stopWatch = new RetrievalStopWatch(infoLevel);
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @return Database metadata
   * @throws SchemaCrawlerException On an exception
   */
  public Catalog crawl() throws SchemaCrawlerException {
    catalog = new MutableCatalog("catalog");
    try {
      retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

      crawlDatabaseInfo();
      LOGGER.log(Level.INFO, String.format("%n%s", catalog.getCrawlInfo()));

      crawlSchemas();
      crawlColumnDataTypes();
      crawlTables();
      crawlRoutines();
      crawlSynonyms();
      crawlSequences();

      return catalog;
    } catch (final SQLException e) {
      throw new SchemaCrawlerException("Database access exception", e);
    }
  }

  private void crawlColumnDataTypes() throws SchemaCrawlerException {

    try {
      stopWatch.reset("crawlColumnDataTypes");

      final DatabaseInfoRetriever retriever =
          new DatabaseInfoRetriever(retrieverConnection, catalog, options);
      final DataTypeRetriever dataTypeRetriever =
          new DataTypeRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          retrieveColumnDataTypes, () -> dataTypeRetriever.retrieveSystemColumnDataTypes());

      stopWatch.time(
          retrieveUserDefinedColumnDataTypes,
          () -> {
            for (final Schema schema : retriever.getAllSchemas()) {
              dataTypeRetriever.retrieveUserDefinedColumnDataTypes(schema);
            }
          });

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving column data type information", e);
    }
  }

  private void crawlDatabaseInfo() throws SchemaCrawlerException {

    if (!infoLevel.is(retrieveDatabaseInfo)) {
      LOGGER.log(Level.INFO, "Not retrieving database information, since this was not requested");
      return;
    }

    try {

      stopWatch.reset("crawlDatabaseInfo");

      final DatabaseInfoRetriever retriever =
          new DatabaseInfoRetriever(retrieverConnection, catalog, options);

      LOGGER.log(Level.INFO, "Retrieving database information");

      stopWatch.time("retrieveDatabaseInfo", () -> retriever.retrieveDatabaseInfo());
      stopWatch.time("retrieveJdbcDriverInfo", () -> retriever.retrieveJdbcDriverInfo());
      stopWatch.time("retrieveCrawlInfo", () -> retriever.retrieveCrawlInfo());

      stopWatch.time(
          retrieveAdditionalDatabaseInfo, () -> retriever.retrieveAdditionalDatabaseInfo());

      stopWatch.time(retrieveServerInfo, () -> retriever.retrieveServerInfo());
      stopWatch.time(retrieveDatabaseUsers, () -> retriever.retrieveDatabaseUsers());

      stopWatch.time(
          retrieveAdditionalJdbcDriverInfo, () -> retriever.retrieveAdditionalJdbcDriverInfo());

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving database information", e);
    }
  }

  private void crawlRoutines() throws SchemaCrawlerException {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveRoutines) && !limitOptions.isExcludeAll(ruleForRoutineInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving routines, since this was not requested");
      return;
    }

    try {
      stopWatch.reset("crawlRoutines");

      final RoutineRetriever retriever =
          new RoutineRetriever(retrieverConnection, catalog, options);
      final RoutineExtRetriever retrieverExtra =
          new RoutineExtRetriever(retrieverConnection, catalog, options);
      final ProcedureParameterRetriever procedureParameterRetriever =
          new ProcedureParameterRetriever(retrieverConnection, catalog, options);
      final FunctionParameterRetriever functionParameterRetriever =
          new FunctionParameterRetriever(retrieverConnection, catalog, options);

      final Collection<RoutineType> routineTypes = limitOptions.getRoutineTypes();

      stopWatch.time(
          "retrieveRoutines",
          () -> {
            final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
            if (routineTypes.contains(RoutineType.procedure)) {
              LOGGER.log(Level.INFO, "Retrieving procedure names");
              retriever.retrieveProcedures(schemas, limitOptions.get(ruleForRoutineInclusion));
            }
            if (routineTypes.contains(RoutineType.function)) {
              LOGGER.log(Level.INFO, "Retrieving function names");
              retriever.retrieveFunctions(schemas, limitOptions.get(ruleForRoutineInclusion));
            }
          });

      final NamedObjectList<MutableRoutine> allRoutines = catalog.getAllRoutines();
      LOGGER.log(Level.INFO, new StringFormat("Retrieved %d routines", allRoutines.size()));
      if (allRoutines.isEmpty()) {
        return;
      }

      stopWatch.time(
          "retrieveRoutineParameters",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving routine columns");
            if (infoLevel.is(retrieveRoutineParameters)
                && !limitOptions.isExcludeAll(ruleForRoutineParameterInclusion)) {
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

      stopWatch.time(retrieveRoutineInformation, () -> retrieverExtra.retrieveRoutineInformation());

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving routine information", e);
    }
  }

  private void crawlSchemas() throws SchemaCrawlerException {

    try {
      stopWatch.reset("crawlSchemas");

      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          "retrieveSchemas",
          () -> retriever.retrieveSchemas(options.getLimitOptions().get(ruleForSchemaInclusion)));

      stopWatch.time(
          "filterAndSortSchemas", () -> catalog.reduce(Schema.class, getSchemaReducer(options)));

      stopWatch.stopAndLogTime();

      final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
      if (schemas.isEmpty()) {
        throw new SchemaCrawlerException("No matching schemas found");
      }
      LOGGER.log(Level.INFO, new StringFormat("Retrieved %d schemas", schemas.size()));

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving schema information", e);
    }
  }

  private void crawlSequences() throws SchemaCrawlerException {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSequenceInformation)
        && !limitOptions.isExcludeAll(ruleForSequenceInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    try {
      stopWatch.reset("crawlSequences");

      final SequenceRetriever retrieverExtra =
          new SequenceRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          retrieveSequenceInformation,
          () ->
              retrieverExtra.retrieveSequenceInformation(
                  limitOptions.get(ruleForSequenceInclusion)));

      stopWatch.time(
          "filterAndSortSequences",
          () -> catalog.reduce(Sequence.class, getSequenceReducer(options)));

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving sequence information", e);
    }
  }

  private void crawlSynonyms() throws SchemaCrawlerException {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSynonymInformation)
        && !limitOptions.isExcludeAll(ruleForSynonymInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    try {
      stopWatch.reset("crawlSynonyms");

      final SynonymRetriever retrieverExtra =
          new SynonymRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          retrieveSynonymInformation,
          () ->
              retrieverExtra.retrieveSynonymInformation(limitOptions.get(ruleForSynonymInclusion)));

      stopWatch.time(
          "filterAndSortSynonms", () -> catalog.reduce(Synonym.class, getSynonymReducer(options)));

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving synonym information", e);
    }
  }

  private void crawlTables() throws SchemaCrawlerException {

    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveTables) && !limitOptions.isExcludeAll(ruleForTableInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving tables, since this was not requested");
      return;
    }

    try {
      stopWatch.reset("crawlTables");

      final TableRetriever retriever = new TableRetriever(retrieverConnection, catalog, options);
      final TableColumnRetriever columnRetriever =
          new TableColumnRetriever(retrieverConnection, catalog, options);
      final ForeignKeyRetriever fkRetriever =
          new ForeignKeyRetriever(retrieverConnection, catalog, options);
      final TableConstraintRetriever constraintRetriever =
          new TableConstraintRetriever(retrieverConnection, catalog, options);
      final TableExtRetriever retrieverExtra =
          new TableExtRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          "retrieveTables",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving table names");
            final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
            retriever.retrieveTables(
                schemas,
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
          "retrieveColumns",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving table columns");
            if (infoLevel.is(retrieveTableColumns)
                && !limitOptions.isExcludeAll(ruleForColumnInclusion)) {
              columnRetriever.retrieveTableColumns(
                  allTables, limitOptions.get(ruleForColumnInclusion));
            }
          });

      stopWatch.time(
          "retrieveForeignKeys",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving foreign keys");
            if (infoLevel.is(retrieveForeignKeys)) {
              if (infoLevel.is(retrieveTableColumns)) {
                fkRetriever.retrieveForeignKeys(allTables);
              }
            } else {
              LOGGER.log(
                  Level.WARNING,
                  "Foreign-keys are not being retrieved, so tables cannot be sorted using the natural sort order");
            }
          });

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

      final PrimaryKeyRetriever primaryKeyRetriever =
          new PrimaryKeyRetriever(retrieverConnection, catalog, options);
      stopWatch.time(
          "retrievePrimaryKeys",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving primary keys");
            if (infoLevel.is(retrieveTableColumns)) {
              primaryKeyRetriever.retrievePrimaryKeys(allTables);
            }
          });

      final IndexRetriever indexRetriever =
          new IndexRetriever(retrieverConnection, catalog, options);
      stopWatch.time(
          "retrieveIndexes",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving indexes");
            if (infoLevel.is(retrieveTableColumns)) {
              if (infoLevel.is(retrieveIndexes)) {
                indexRetriever.retrieveIndexes(allTables);
              }
            }
          });

      LOGGER.log(Level.INFO, "Retrieving additional table information");
      stopWatch.time(
          retrieveTableConstraintInformation,
          () -> constraintRetriever.retrieveTableConstraintInformation());
      // Required step: Match all constraints such as primary keys and foreign keys
      stopWatch.time(
          retrieveTableColumns, () -> constraintRetriever.matchTableConstraints(allTables));
      stopWatch.time(
          retrieveTableConstraintDefinitions,
          () -> constraintRetriever.retrieveTableConstraintDefinitions());
      stopWatch.time(retrieveTriggerInformation, () -> retrieverExtra.retrieveTriggerInformation());
      stopWatch.time(retrieveViewInformation, () -> retrieverExtra.retrieveViewInformation());
      stopWatch.time(retrieveViewTableUsage, () -> retrieverExtra.retrieveViewTableUsage());
      stopWatch.time(
          retrieveTableDefinitionsInformation, () -> retrieverExtra.retrieveTableDefinitions());
      stopWatch.time(retrieveIndexInformation, () -> retrieverExtra.retrieveIndexInformation());

      stopWatch.time(
          retrieveAdditionalTableAttributes,
          () -> retrieverExtra.retrieveAdditionalTableAttributes());
      stopWatch.time(retrieveTablePrivileges, () -> retrieverExtra.retrieveTablePrivileges());
      stopWatch.time(
          retrieveAdditionalColumnAttributes,
          () -> retrieverExtra.retrieveAdditionalColumnAttributes());
      stopWatch.time(
          retrieveAdditionalColumnMetadata,
          () -> retrieverExtra.retrieveAdditionalColumnMetadata());

      stopWatch.time(
          retrieveTableColumnPrivileges, () -> retrieverExtra.retrieveTableColumnPrivileges());

      stopWatch.stopAndLogTime();

    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving table information", e);
    }
  }
}
