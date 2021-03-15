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
import us.fatehi.utility.StopWatch;
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
      LOGGER.log(Level.INFO, "Crawling column data types");

      final StopWatch stopWatch = new StopWatch("crawlColumnDataTypes");

      final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever =
          new DatabaseInfoRetriever(retrieverConnection, catalog, options);
      final DataTypeRetriever dataTypeRetriever =
          new DataTypeRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          "retrieveSystemColumnDataTypes",
          () -> {
            if (infoLevel.is(retrieveColumnDataTypes)) {
              LOGGER.log(Level.INFO, "Retrieving system column data types");
              dataTypeRetriever.retrieveSystemColumnDataTypes();
            } else {
              LOGGER.log(
                  Level.INFO,
                  "Not retrieving system column data types, since this was not requested");
            }
            return null;
          });

      stopWatch.time(
          "retrieveUserDefinedColumnDataTypes",
          () -> {
            if (infoLevel.is(retrieveUserDefinedColumnDataTypes)) {
              LOGGER.log(Level.INFO, "Retrieving user column data types");
              for (final Schema schema : retriever.getAllSchemas()) {
                dataTypeRetriever.retrieveUserDefinedColumnDataTypes(schema);
              }
            } else {
              LOGGER.log(
                  Level.INFO,
                  "Not retrieving user column data types, since this was not requested");
            }
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving column data type information", e);
    }
  }

  private void crawlDatabaseInfo() throws SchemaCrawlerException {
    try {
      final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
      if (!infoLevel.is(retrieveDatabaseInfo)) {
        LOGGER.log(Level.INFO, "Not retrieving database information, since this was not requested");
        return;
      }

      final StopWatch stopWatch = new StopWatch("crawlDatabaseInfo");

      final DatabaseInfoRetriever retriever =
          new DatabaseInfoRetriever(retrieverConnection, catalog, options);

      LOGGER.log(Level.INFO, "Retrieving database information");

      stopWatch.time(
          "retrieveDatabaseInfo",
          () -> {
            retriever.retrieveDatabaseInfo();
            return null;
          });

      stopWatch.time(
          "retrieveAdditionalDatabaseInfo",
          () -> {
            if (infoLevel.is(retrieveAdditionalDatabaseInfo)) {
              retriever.retrieveAdditionalDatabaseInfo();
            } else {
              LOGGER.log(
                  Level.INFO,
                  "Not retrieving additional database information, since this was not requested");
            }
            return null;
          });

      stopWatch.time(
          "retrieveServerInfo",
          () -> {
            if (infoLevel.is(retrieveServerInfo)) {
              retriever.retrieveServerInfo();
            } else {
              LOGGER.log(
                  Level.INFO, "Not retrieving server information, since this was not requested");
            }
            return null;
          });

      stopWatch.time(
          "retrieveDatabaseUsers",
          () -> {
            if (infoLevel.is(retrieveDatabaseUsers)) {
              retriever.retrieveDatabaseUsers();
            } else {
              LOGGER.log(Level.INFO, "Not retrieving database users, since this was not requested");
            }
            return null;
          });

      LOGGER.log(Level.INFO, "Retrieving JDBC driver information");
      stopWatch.time(
          "retrieveJdbcDriverInfo",
          () -> {
            retriever.retrieveJdbcDriverInfo();
            return null;
          });

      stopWatch.time(
          "retrieveAdditionalJdbcDriverInfo",
          () -> {
            if (infoLevel.is(retrieveAdditionalJdbcDriverInfo)) {
              retriever.retrieveAdditionalJdbcDriverInfo();
            } else {
              LOGGER.log(
                  Level.INFO,
                  "Not retrieving additional JDBC driver information, since this was not requested");
            }
            return null;
          });

      LOGGER.log(Level.INFO, "Retrieving SchemaCrawler crawl information");
      stopWatch.time(
          "retrieveCrawlInfo",
          () -> {
            retriever.retrieveCrawlInfo();
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving database information", e);
    }
  }

  private void crawlRoutines() throws SchemaCrawlerException {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveRoutines) && !limitOptions.isExcludeAll(ruleForRoutineInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving routines, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlRoutines");

    LOGGER.log(Level.INFO, "Crawling routines");

    final RoutineRetriever retriever;
    final RoutineExtRetriever retrieverExtra;
    final ProcedureParameterRetriever procedureParameterRetriever;
    final FunctionParameterRetriever functionParameterRetriever;
    try {
      retriever = new RoutineRetriever(retrieverConnection, catalog, options);
      retrieverExtra = new RoutineExtRetriever(retrieverConnection, catalog, options);
      procedureParameterRetriever =
          new ProcedureParameterRetriever(retrieverConnection, catalog, options);
      functionParameterRetriever =
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
            return null;
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
            return null;
          });

      stopWatch.time(
          "filterAndSortRoutines",
          () -> {
            // Filter the list of routines based on grep criteria
            catalog.reduce(Routine.class, getRoutineReducer(options));
            return null;
          });

      stopWatch.time(
          "retrieveRoutineInformation",
          () -> {
            if (infoLevel.is(retrieveRoutineInformation)) {
              retrieverExtra.retrieveRoutineInformation();
            }
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving routine information", e);
    }
  }

  private void crawlSchemas() throws SchemaCrawlerException {
    final StopWatch stopWatch = new StopWatch("crawlSchemas");

    LOGGER.log(Level.INFO, "Crawling schemas");

    try {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          "retrieveSchemas",
          () -> {
            retriever.retrieveSchemas(options.getLimitOptions().get(ruleForSchemaInclusion));
            return null;
          });

      stopWatch.time(
          "filterAndSortSchemas",
          () -> {
            catalog.reduce(Schema.class, getSchemaReducer(options));
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());

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

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSequenceInformation)
        && !limitOptions.isExcludeAll(ruleForSequenceInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlSequences");

    LOGGER.log(Level.INFO, "Crawling sequences");

    final SequenceRetriever retrieverExtra;
    try {
      retrieverExtra = new SequenceRetriever(retrieverConnection, catalog, options);

      stopWatch.time(
          "retrieveSequenceInformation",
          () -> {
            retrieverExtra.retrieveSequenceInformation(limitOptions.get(ruleForSequenceInclusion));
            return null;
          });

      stopWatch.time(
          "filterAndSortSequences",
          () -> {
            catalog.reduce(Sequence.class, getSequenceReducer(options));
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving sequence information", e);
    }
  }

  private void crawlSynonyms() throws SchemaCrawlerException {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveSynonymInformation)
        && !limitOptions.isExcludeAll(ruleForSynonymInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlSynonyms");

    LOGGER.log(Level.INFO, "Crawling synonyms");

    final SynonymRetriever retrieverExtra;
    try {
      retrieverExtra = new SynonymRetriever(retrieverConnection, catalog, options);
      stopWatch.time(
          "retrieveSynonymInformation",
          () -> {
            retrieverExtra.retrieveSynonymInformation(limitOptions.get(ruleForSynonymInclusion));
            return null;
          });

      stopWatch.time(
          "filterAndSortSynonms",
          () -> {
            catalog.reduce(Synonym.class, getSynonymReducer(options));
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving synonym information", e);
    }
  }

  private void crawlTables() throws SchemaCrawlerException {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    if (!(infoLevel.is(retrieveTables) && !limitOptions.isExcludeAll(ruleForTableInclusion))) {
      LOGGER.log(Level.INFO, "Not retrieving tables, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlTables");

    LOGGER.log(Level.INFO, "Crawling tables");

    try {
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
            return null;
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
            return null;
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
            return null;
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

            return null;
          });

      stopWatch.time(
          "retrievePrimaryKeys",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving primary keys");
            if (infoLevel.is(retrieveTableColumns)) {
              final PrimaryKeyRetriever primaryKeyRetriever =
                  new PrimaryKeyRetriever(retrieverConnection, catalog, options);
              primaryKeyRetriever.retrievePrimaryKeys(allTables);
            }
            return null;
          });

      stopWatch.time(
          "retrieveIndexes",
          () -> {
            LOGGER.log(Level.INFO, "Retrieving indexes");
            if (infoLevel.is(retrieveTableColumns)) {
              if (infoLevel.is(retrieveIndexes)) {
                final IndexRetriever indexRetriever =
                    new IndexRetriever(retrieverConnection, catalog, options);
                indexRetriever.retrieveIndexes(allTables);
              }
            }
            return null;
          });

      LOGGER.log(Level.INFO, "Retrieving additional table information");
      stopWatch.time(
          "retrieveTableConstraintInformation",
          () -> {
            if (infoLevel.is(retrieveTableConstraintInformation)) {
              constraintRetriever.retrieveTableConstraintInformation();
            }
            constraintRetriever.matchTableConstraints(allTables);
            return null;
          });
      stopWatch.time(
          "isRetrieveTableConstraintDefinitions",
          () -> {
            if (infoLevel.is(retrieveTableConstraintDefinitions)) {
              constraintRetriever.retrieveTableConstraintDefinitions();
            }
            return null;
          });
      stopWatch.time(
          "retrieveTriggerInformation",
          () -> {
            if (infoLevel.is(retrieveTriggerInformation)) {
              retrieverExtra.retrieveTriggerInformation();
            }
            return null;
          });
      stopWatch.time(
          "retrieveViewInformation",
          () -> {
            if (infoLevel.is(retrieveViewInformation)) {
              retrieverExtra.retrieveViewInformation();
            }
            return null;
          });
      stopWatch.time(
          "retrieveViewTableUsage",
          () -> {
            if (infoLevel.is(retrieveViewTableUsage)) {
              retrieverExtra.retrieveViewTableUsage();
            }
            return null;
          });
      stopWatch.time(
          "retrieveTableDefinitions",
          () -> {
            if (infoLevel.is(retrieveTableDefinitionsInformation)) {
              retrieverExtra.retrieveTableDefinitions();
            }
            return null;
          });
      stopWatch.time(
          "retrieveIndexInformation",
          () -> {
            if (infoLevel.is(retrieveIndexInformation)) {
              retrieverExtra.retrieveIndexInformation();
            }
            return null;
          });

      stopWatch.time(
          "retrieveAdditionalTableAttributes",
          () -> {
            if (infoLevel.is(retrieveAdditionalTableAttributes)) {
              retrieverExtra.retrieveAdditionalTableAttributes();
            }
            return null;
          });
      stopWatch.time(
          "retrieveTablePrivileges",
          () -> {
            if (infoLevel.is(retrieveTablePrivileges)) {
              retrieverExtra.retrieveTablePrivileges();
            }
            return null;
          });

      stopWatch.time(
          "retrieveAdditionalColumnAttributes",
          () -> {
            if (infoLevel.is(retrieveAdditionalColumnAttributes)) {
              retrieverExtra.retrieveAdditionalColumnAttributes();
            }
            return null;
          });
      stopWatch.time(
          "retrieveAdditionalColumnMetadata",
          () -> {
            if (infoLevel.is(retrieveAdditionalColumnMetadata)) {
              retrieverExtra.retrieveAdditionalColumnMetadata();
            }
            return null;
          });
      stopWatch.time(
          "retrieveTableColumnPrivileges",
          () -> {
            if (infoLevel.is(retrieveTableColumnPrivileges)) {
              retrieverExtra.retrieveTableColumnPrivileges();
            }
            return null;
          });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    } catch (final SchemaCrawlerSQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Exception retrieving table information", e);
    }
  }
}
