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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.analysis.counts.TableRowCountsFilter;
import schemacrawler.analysis.counts.TableRowCountsRetriever;
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
import sf.util.SchemaCrawlerLogger;
import sf.util.StopWatch;
import sf.util.StringFormat;

/**
 * SchemaCrawler uses database meta-data to get the details about the schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(SchemaCrawler.class.getName());

  private final Connection connection;
  private final SchemaCrawlerOptions options;
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private MutableCatalog catalog;
  private RetrieverConnection retrieverConnection;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection
   *   An database connection.
   * @param schemaRetrievalOptions
   *   Database-specific schema retrieval overrides
   * @param options
   *   SchemaCrawler options
   */
  public SchemaCrawler(final Connection connection,
                       final SchemaRetrievalOptions schemaRetrievalOptions,
                       final SchemaCrawlerOptions options)
  {
    this.connection = requireNonNull(connection, "No connection specified");
    this.schemaRetrievalOptions =
      requireNonNull(schemaRetrievalOptions, "No database-specific schema retrieval overrides provided");
    this.options = requireNonNull(options, "No SchemaCrawler options provided");
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @return Database metadata
   * @throws SchemaCrawlerException
   *   On an exception
   */
  public Catalog crawl()
    throws SchemaCrawlerException
  {
    catalog = new MutableCatalog("catalog");
    try
    {
      retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

      crawlDatabaseInfo();
      LOGGER.log(Level.INFO, String.format("%n%s", catalog.getCrawlInfo()));

      crawlSchemas();
      crawlColumnDataTypes();
      crawlTables();
      crawlRoutines();
      crawlSynonyms();
      crawlSequences();
      crawlAnalysis();

      return catalog;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Database access exception", e);
    }
  }

  private void crawlAnalysis()
    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();

    final StopWatch stopWatch = new StopWatch("crawlAnalysis");

    LOGGER.log(Level.INFO, "Crawling schema analysis");
    try
    {
      final WeakAssociationsRetriever weakAssociationsRetriever = new WeakAssociationsRetriever(catalog);
      stopWatch.time("retrieveWeakAssociations", () -> {
        final boolean retrieveWeakAssociations = infoLevel.isRetrieveWeakAssociations();
        if (retrieveWeakAssociations)
        {
          weakAssociationsRetriever.retrieveWeakAssociations();
          return null;
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving weak associations, since this was not requested");
          return null;
        }
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving weak association information", e);
    }

    LOGGER.log(Level.INFO, "Crawling table row counts");
    try
    {
      final TableRowCountsRetriever rowCountsRetriever =
        new TableRowCountsRetriever(retrieverConnection.getConnection(), catalog);
      stopWatch.time("retrieveTableRowCounts", () -> {
        final boolean loadRowCounts = options.getLoadOptions().isLoadRowCounts();
        if (loadRowCounts)
        {
          rowCountsRetriever.retrieveTableRowCounts();
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving table row counts, since this was not requested");
        }
        return null;
      });

      stopWatch.time("filterEmptyTables", () -> {
        catalog.reduce(Table.class, getTableReducer(new TableRowCountsFilter(options.getFilterOptions())));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving table row counts", e);
    }
  }

  private void crawlColumnDataTypes()
    throws SchemaCrawlerException
  {
    try
    {
      LOGGER.log(Level.INFO, "Crawling column data types");

      final StopWatch stopWatch = new StopWatch("crawlColumnDataTypes");

      final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection, catalog, options);

      stopWatch.time("retrieveSystemColumnDataTypes", () -> {
        if (infoLevel.isRetrieveColumnDataTypes())
        {
          LOGGER.log(Level.INFO, "Retrieving system column data types");
          retriever.retrieveSystemColumnDataTypes();
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving system column data types, since this was not requested");
        }
        return null;
      });

      stopWatch.time("retrieveUserDefinedColumnDataTypes", () -> {
        if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
        {
          LOGGER.log(Level.INFO, "Retrieving user column data types");
          for (final Schema schema : retriever.getAllSchemas())
          {
            retriever.retrieveUserDefinedColumnDataTypes(schema);
          }
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving user column data types, since this was not requested");
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving column data type information", e);
    }
  }

  private void crawlDatabaseInfo()
    throws SchemaCrawlerException
  {
    try
    {
      final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
      final boolean retrieveDatabaseInfo = infoLevel.isRetrieveDatabaseInfo();
      if (!retrieveDatabaseInfo)
      {
        LOGGER.log(Level.INFO, "Not retrieving database information, since this was not requested");
        return;
      }

      final StopWatch stopWatch = new StopWatch("crawlDatabaseInfo");

      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection, catalog, options);

      LOGGER.log(Level.INFO, "Retrieving database information");

      stopWatch.time("retrieveDatabaseInfo", () -> {
        retriever.retrieveDatabaseInfo();
        return null;
      });

      stopWatch.time("retrieveAdditionalDatabaseInfo", () -> {
        if (infoLevel.isRetrieveAdditionalDatabaseInfo())
        {
          retriever.retrieveAdditionalDatabaseInfo();
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving additional database information, since this was not requested");
        }
        return null;
      });

      stopWatch.time("retrieveServerInfo", () -> {
        if (infoLevel.isRetrieveServerInfo())
        {
          retriever.retrieveServerInfo();
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving server information, since this was not requested");
        }
        return null;
      });

      LOGGER.log(Level.INFO, "Retrieving JDBC driver information");
      stopWatch.time("retrieveJdbcDriverInfo", () -> {
        retriever.retrieveJdbcDriverInfo();
        return null;
      });

      stopWatch.time("retrieveAdditionalJdbcDriverInfo", () -> {
        if (infoLevel.isRetrieveAdditionalJdbcDriverInfo())
        {
          retriever.retrieveAdditionalJdbcDriverInfo();
        }
        else
        {
          LOGGER.log(Level.INFO, "Not retrieving additional JDBC driver information, since this was not requested");
        }
        return null;
      });

      LOGGER.log(Level.INFO, "Retrieving SchemaCrawler crawl information");
      stopWatch.time("retrieveCrawlInfo", () -> {
        retriever.retrieveCrawlInfo();
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information", e);
    }
  }

  private void crawlRoutines()
    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    final boolean retrieveRoutines = infoLevel.isRetrieveRoutines()
                                     && !limitOptions.isExcludeAll(ruleForRoutineInclusion);
    if (!retrieveRoutines)
    {
      LOGGER.log(Level.INFO, "Not retrieving routines, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlRoutines");

    LOGGER.log(Level.INFO, "Crawling routines");

    final RoutineRetriever retriever;
    final RoutineExtRetriever retrieverExtra;
    final ProcedureParameterRetriever procedureParameterRetriever;
    final FunctionParameterRetriever functionParameterRetriever;
    try
    {
      retriever = new RoutineRetriever(retrieverConnection, catalog, options);
      retrieverExtra = new RoutineExtRetriever(retrieverConnection, catalog, options);
      procedureParameterRetriever = new ProcedureParameterRetriever(retrieverConnection, catalog, options);
      functionParameterRetriever = new FunctionParameterRetriever(retrieverConnection, catalog, options);

      final Collection<RoutineType> routineTypes = limitOptions.getRoutineTypes();

      stopWatch.time("retrieveRoutines", () -> {
        final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
        if (routineTypes.contains(RoutineType.procedure))
        {
          LOGGER.log(Level.INFO, "Retrieving procedure names");
          retriever.retrieveProcedures(schemas, limitOptions.get(ruleForRoutineInclusion));
        }
        if (routineTypes.contains(RoutineType.function))
        {
          LOGGER.log(Level.INFO, "Retrieving function names");
          retriever.retrieveFunctions(schemas, limitOptions.get(ruleForRoutineInclusion));
        }
        return null;
      });

      final NamedObjectList<MutableRoutine> allRoutines = catalog.getAllRoutines();
      LOGGER.log(Level.INFO, new StringFormat("Retrieved %d routines", allRoutines.size()));
      if (allRoutines.isEmpty())
      {
        return;
      }

      stopWatch.time("retrieveRoutineParameters", () -> {
        LOGGER.log(Level.INFO, "Retrieving routine columns");
        if (infoLevel.isRetrieveRoutineParameters() && !limitOptions.isExcludeAll(ruleForRoutineParameterInclusion))
        {
          if (routineTypes.contains(RoutineType.procedure))
          {
            procedureParameterRetriever.retrieveProcedureParameters(allRoutines,
                                                                    limitOptions.get(ruleForRoutineParameterInclusion));
          }

          if (routineTypes.contains(RoutineType.function))
          {
            functionParameterRetriever.retrieveFunctionParameters(allRoutines,
                                                                  limitOptions.get(ruleForRoutineParameterInclusion));
          }
        }
        return null;
      });

      stopWatch.time("filterAndSortRoutines", () -> {
        // Filter the list of routines based on grep criteria
        catalog.reduce(Routine.class, getRoutineReducer(options));
        return null;
      });

      stopWatch.time("retrieveRoutineInformation", () -> {
        if (infoLevel.isRetrieveRoutineInformation())
        {
          retrieverExtra.retrieveRoutineInformation();
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving routine information", e);
    }
  }

  private void crawlSchemas()
    throws SchemaCrawlerException
  {
    final StopWatch stopWatch = new StopWatch("crawlSchemas");

    LOGGER.log(Level.INFO, "Crawling schemas");

    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection, catalog, options);

      stopWatch.time("retrieveSchemas", () -> {
        retriever.retrieveSchemas(options.getLimitOptions().get(ruleForSchemaInclusion));
        return null;
      });

      stopWatch.time("filterAndSortSchemas", () -> {
        catalog.reduce(Schema.class, getSchemaReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());

      final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
      if (schemas.isEmpty())
      {
        throw new SchemaCrawlerException("No matching schemas found");
      }
      LOGGER.log(Level.INFO, new StringFormat("Retrieved %d schemas", schemas.size()));
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving schema information", e);
    }
  }

  private void crawlSequences()
    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    final boolean retrieveSequences = infoLevel.isRetrieveSequenceInformation() &&
                                      !limitOptions.isExcludeAll(ruleForSequenceInclusion);
    if (!retrieveSequences)
    {
      LOGGER.log(Level.INFO, "Not retrieving sequences, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlSequences");

    LOGGER.log(Level.INFO, "Crawling sequences");

    final SequenceRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SequenceRetriever(retrieverConnection, catalog, options);

      stopWatch.time("retrieveSequenceInformation", () -> {
        retrieverExtra.retrieveSequenceInformation(limitOptions.get(ruleForSequenceInclusion));
        return null;
      });

      stopWatch.time("filterAndSortSequences", () -> {
        catalog.reduce(Sequence.class, getSequenceReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving sequence information", e);
    }
  }

  private void crawlSynonyms()
    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    final boolean retrieveSynonyms = infoLevel.isRetrieveSynonymInformation() &&
                                     !limitOptions.isExcludeAll(ruleForSynonymInclusion);
    if (!retrieveSynonyms)
    {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlSynonyms");

    LOGGER.log(Level.INFO, "Crawling synonyms");

    final SynonymRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SynonymRetriever(retrieverConnection, catalog, options);
      stopWatch.time("retrieveSynonymInformation", () -> {
        retrieverExtra.retrieveSynonymInformation(limitOptions.get(ruleForSynonymInclusion));
        return null;
      });

      stopWatch.time("filterAndSortSynonms", () -> {
        catalog.reduce(Synonym.class, getSynonymReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving synonym information", e);
    }
  }

  private void crawlTables()
    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getLoadOptions().getSchemaInfoLevel();
    final LimitOptions limitOptions = options.getLimitOptions();
    final boolean retrieveTables = infoLevel.isRetrieveTables()
                                   && !limitOptions.isExcludeAll(ruleForTableInclusion);
    if (!retrieveTables)
    {
      LOGGER.log(Level.INFO, "Not retrieving tables, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlTables");

    LOGGER.log(Level.INFO, "Crawling tables");

    try
    {
      final TableRetriever retriever = new TableRetriever(retrieverConnection, catalog, options);
      final TableColumnRetriever columnRetriever = new TableColumnRetriever(retrieverConnection, catalog, options);
      final ForeignKeyRetriever fkRetriever = new ForeignKeyRetriever(retrieverConnection, catalog, options);
      final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);
      final TableExtRetriever retrieverExtra = new TableExtRetriever(retrieverConnection, catalog, options);

      stopWatch.time("retrieveTables", () -> {
        LOGGER.log(Level.INFO, "Retrieving table names");
        final NamedObjectList<SchemaReference> schemas = retriever.getAllSchemas();
        retriever.retrieveTables(schemas,
                                 limitOptions.getTableNamePattern(),
                                 limitOptions.getTableTypes(),
                                 limitOptions.get(ruleForTableInclusion));
        return null;
      });

      final NamedObjectList<MutableTable> allTables = catalog.getAllTables();
      LOGGER.log(Level.INFO, new StringFormat("Retrieved %d tables", allTables.size()));
      if (allTables.isEmpty())
      {
        return;
      }

      stopWatch.time("retrieveColumns", () -> {
        LOGGER.log(Level.INFO, "Retrieving table columns");
        if (infoLevel.isRetrieveTableColumns() && !limitOptions.isExcludeAll(ruleForColumnInclusion))
        {
          columnRetriever.retrieveTableColumns(allTables, limitOptions.get(ruleForColumnInclusion));
        }
        return null;
      });

      stopWatch.time("retrieveForeignKeys", () -> {
        LOGGER.log(Level.INFO, "Retrieving foreign keys");
        if (infoLevel.isRetrieveForeignKeys())
        {
          if (infoLevel.isRetrieveTableColumns())
          {
            fkRetriever.retrieveForeignKeys(allTables);
            if (infoLevel.isRetrieveForeignKeyDefinitions())
            {
              fkRetriever.retrieveForeignKeyDefinitions(allTables);
            }
          }
        }
        else
        {
          LOGGER.log(Level.WARNING,
                     "Foreign-keys are not being retrieved, so tables cannot be sorted using the natural sort order");
        }
        return null;
      });

      stopWatch.time("filterAndSortTables", () -> {
        // Filter the list of tables based on grep criteria, and
        // parent-child relationships
        catalog.reduce(Table.class, getTableReducer(options));

        // Sort the remaining tables
        final TablesGraph tablesGraph = new TablesGraph(allTables);
        tablesGraph.setTablesSortIndexes();

        return null;
      });

      stopWatch.time("retrievePrimaryKeys", () -> {
        LOGGER.log(Level.INFO, "Retrieving primary keys");
        if (infoLevel.isRetrieveTableColumns())
        {
          final PrimaryKeyRetriever primaryKeyRetriever = new PrimaryKeyRetriever(retrieverConnection, catalog, options);
          primaryKeyRetriever.retrievePrimaryKeys(allTables);
          if (infoLevel.isRetrievePrimaryKeyDefinitions())
          {
            retrieverExtra.retrievePrimaryKeyDefinitions(allTables);
          }
        }
        return null;
      });

      stopWatch.time("retrieveIndexes", () -> {
        LOGGER.log(Level.INFO, "Retrieving indexes");
        if (infoLevel.isRetrieveTableColumns())
        {
          if (infoLevel.isRetrieveIndexes())
          {
            final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
            indexRetriever.retrieveIndexes(allTables);
          }
        }
        return null;
      });

      LOGGER.log(Level.INFO, "Retrieving additional table information");
      stopWatch.time("retrieveTableConstraintInformation", () -> {
        if (infoLevel.isRetrieveTableConstraintInformation())
        {
          constraintRetriever.retrieveTableConstraintInformation();
        }
        return null;
      });
      stopWatch.time("isRetrieveTableConstraintDefinitions", () -> {
        if (infoLevel.isRetrieveTableConstraintDefinitions())
        {
          constraintRetriever.retrieveTableConstraintDefinitions();
        }
        return null;
      });
      stopWatch.time("retrieveTriggerInformation", () -> {
        if (infoLevel.isRetrieveTriggerInformation())
        {
          retrieverExtra.retrieveTriggerInformation();
        }
        return null;
      });
      stopWatch.time("retrieveViewInformation", () -> {
        if (infoLevel.isRetrieveViewInformation())
        {
          retrieverExtra.retrieveViewInformation();
        }
        return null;
      });
      stopWatch.time("retrieveTableDefinitions", () -> {
        if (infoLevel.isRetrieveTableDefinitionsInformation())
        {
          retrieverExtra.retrieveTableDefinitions();
        }
        return null;
      });
      stopWatch.time("retrieveIndexInformation", () -> {
        if (infoLevel.isRetrieveIndexInformation())
        {
          retrieverExtra.retrieveIndexInformation();
          if (infoLevel.isRetrieveIndexColumnInformation())
          {
            retrieverExtra.retrieveIndexColumnInformation();
          }
        }
        return null;
      });

      stopWatch.time("retrieveAdditionalTableAttributes", () -> {
        if (infoLevel.isRetrieveAdditionalTableAttributes())
        {
          retrieverExtra.retrieveAdditionalTableAttributes();
        }
        return null;
      });
      stopWatch.time("retrieveTablePrivileges", () -> {
        if (infoLevel.isRetrieveTablePrivileges())
        {
          retrieverExtra.retrieveTablePrivileges();
        }
        return null;
      });

      stopWatch.time("retrieveAdditionalColumnAttributes", () -> {
        if (infoLevel.isRetrieveAdditionalColumnAttributes())
        {
          retrieverExtra.retrieveAdditionalColumnAttributes();
        }
        return null;
      });
      stopWatch.time("retrieveAdditionalColumnMetadata", () -> {
        if (infoLevel.isRetrieveAdditionalColumnMetadata())
        {
          retrieverExtra.retrieveAdditionalColumnMetadata();
        }
        return null;
      });
      stopWatch.time("retrieveTableColumnPrivileges", () -> {
        if (infoLevel.isRetrieveTableColumnPrivileges())
        {
          retrieverExtra.retrieveTableColumnPrivileges();
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());
    }
    catch (final SchemaCrawlerSQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e.getCause());
    }
    catch (final SchemaCrawlerException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Exception retrieving table information", e);
    }
  }

}
