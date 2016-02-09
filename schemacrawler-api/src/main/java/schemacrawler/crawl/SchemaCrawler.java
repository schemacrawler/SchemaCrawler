/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static schemacrawler.filter.FilterFactory.routineFilter;
import static schemacrawler.filter.FilterFactory.tableFilter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import sf.util.StopWatch;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawler.class.getName());

  /**
   * Gets the result set columns metadata.
   *
   * @param resultSet
   *        Result set
   * @return Schema
   */
  public static ResultsColumns getResultColumns(final ResultSet resultSet)
  {
    ResultsColumns resultColumns = null;
    try
    {
      final ResultsRetriever resultsRetriever = new ResultsRetriever(resultSet);
      resultColumns = resultsRetriever.retrieveResults();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
  }

  private static void crawlColumnDataTypes(final MutableCatalog catalog,
                                           final RetrieverConnection retrieverConnection,
                                           final SchemaCrawlerOptions options)
                                             throws SchemaCrawlerException
  {
    try
    {
      LOGGER.log(Level.INFO, "Crawling column data types");

      final StopWatch stopWatch = new StopWatch("crawlColumnDataTypes");

      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        catalog);

      stopWatch.time("retrieveSystemColumnDataTypes", () -> {
        if (infoLevel.isRetrieveColumnDataTypes())
        {
          LOGGER.log(Level.INFO, "Retrieving system column data types");
          retriever.retrieveSystemColumnDataTypes();
        }
        else
        {
          LOGGER
            .log(Level.INFO,
                 "Not retrieving system column data types, since this was not requested");
        }
        return null;
      });

      stopWatch.time("retrieveUserDefinedColumnDataTypes", () -> {
        if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
        {
          LOGGER.log(Level.INFO, "Retrieving user column data types");
          for (final Schema schema: retriever.getSchemas())
          {
            retriever.retrieveUserDefinedColumnDataTypes(
                                                         schema
                                                           .getCatalogName(),
                                                         schema.getName());
          }
        }
        else
        {
          LOGGER
            .log(Level.INFO,
                 "Not retrieving user column data types, since this was not requested");
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving column data type information",
                                         e);
      }
    }
  }

  private static void crawlDatabaseInfo(final MutableCatalog catalog,
                                        final RetrieverConnection retrieverConnection,
                                        final SchemaCrawlerOptions options)
                                          throws SchemaCrawlerException
  {
    try
    {
      final StopWatch stopWatch = new StopWatch("crawlDatabaseInfo");

      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        catalog);

      LOGGER.log(Level.INFO, "Crawling SchemaCrawler information");

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
          LOGGER
            .log(Level.INFO,
                 "Not retrieving additional database information, since this was not requested");
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
          LOGGER
            .log(Level.INFO,
                 "Not retrieving additional JDBC driver information, since this was not requested");
        }
        return null;
      });

      LOGGER.log(Level.INFO, "Retrieving SchemaCrawler crawl information");
      stopWatch.time("retrieveCrawlHeaderInfo", () -> {
        retriever.retrieveCrawlHeaderInfo(options.getTitle());
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving database information",
                                         e);
      }
    }
  }

  private static void crawlRoutines(final MutableCatalog catalog,
                                    final RetrieverConnection retrieverConnection,
                                    final SchemaCrawlerOptions options)
                                      throws SchemaCrawlerException
  {
    final StopWatch stopWatch = new StopWatch("crawlRoutines");

    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveRoutines = infoLevel.isRetrieveRoutines();
    if (!retrieveRoutines)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving routines, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Crawling routines");

    final RoutineRetriever retriever;
    final RoutineExtRetriever retrieverExtra;
    try
    {
      retriever = new RoutineRetriever(retrieverConnection, catalog);
      retrieverExtra = new RoutineExtRetriever(retrieverConnection, catalog);
      final Collection<RoutineType> routineTypes = options.getRoutineTypes();

      stopWatch.time("retrieveRoutines", () -> {
        for (final Schema schema: retriever.getSchemas())
        {
          if (routineTypes.contains(RoutineType.procedure))
          {
            retriever.retrieveProcedures(schema.getCatalogName(),
                                         schema.getName(),
                                         options.getRoutineInclusionRule());
          }
          if (routineTypes.contains(RoutineType.function))
          {
            retriever.retrieveFunctions(schema.getCatalogName(),
                                        schema.getName(),
                                        options.getRoutineInclusionRule());
          }
        }
        return null;
      });

      final NamedObjectList<MutableRoutine> allRoutines = catalog
        .getAllRoutines();

      stopWatch.time("retrieveRoutineColumns", () -> {
        LOGGER.log(Level.INFO, "Retrieving routine columns");
        for (final MutableRoutine routine: allRoutines)
        {
          if (infoLevel.isRetrieveRoutineColumns())
          {
            if (routine instanceof MutableProcedure
                && routineTypes.contains(RoutineType.procedure))
            {
              retriever
                .retrieveProcedureColumns((MutableProcedure) routine,
                                          options
                                            .getRoutineColumnInclusionRule());
            }

            if (routine instanceof MutableFunction
                && routineTypes.contains(RoutineType.function))
            {
              retriever
                .retrieveFunctionColumns((MutableFunction) routine,
                                         options
                                           .getRoutineColumnInclusionRule());
            }
          }
        }
        return null;
      });

      stopWatch.time("filterRoutines", () -> {
        // Filter the list of routines based on grep criteria
        final Predicate<Routine> routineFilter = routineFilter(options);
        ((Reducible) catalog).reduce(Routine.class,
                                     new RoutinesReducer(routineFilter));
        return null;
      });

      stopWatch.time("retrieveRoutineInformation", () -> {
        if (infoLevel.isRetrieveRoutineInformation())
        {
          retrieverExtra.retrieveRoutineInformation();
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving routine information",
                                         e);
      }
    }
  }

  private static void crawlSchemas(final MutableCatalog catalog,
                                   final RetrieverConnection retrieverConnection,
                                   final SchemaCrawlerOptions options)
                                     throws SchemaCrawlerException
  {
    final StopWatch stopWatch = new StopWatch("crawlSchemas");

    LOGGER.log(Level.INFO, "Crawling schemas");

    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection,
                                                            catalog);

      stopWatch.time("retrieveSchemas", () -> {
        retriever.retrieveSchemas(options.getSchemaInclusionRule());
        return null;
      });

      stopWatch.time("sortAndFilterSchemas", () -> {
        ((Reducible) catalog).reduce(Schema.class, new SchemasReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving schema information",
                                         e);
      }
    }
  }

  private static void crawlSequences(final MutableCatalog catalog,
                                     final RetrieverConnection retrieverConnection,
                                     final SchemaCrawlerOptions options)
                                       throws SchemaCrawlerException
  {
    final StopWatch stopWatch = new StopWatch("crawlSequences");

    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveSequences = infoLevel.isRetrieveSequenceInformation();
    if (!retrieveSequences)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving sequences, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Crawling sequences");

    final SequenceRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SequenceRetriever(retrieverConnection, catalog);

      stopWatch.time("retrieveSequenceInformation", () -> {
        retrieverExtra
          .retrieveSequenceInformation(options.getSequenceInclusionRule());
        return null;
      });

      stopWatch.time("sortAndFilterSequences", () -> {
        ((Reducible) catalog).reduce(Sequence.class,
                                     new SequencesReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving sequence information",
                                         e);
      }
    }
  }

  private static void crawlSynonyms(final MutableCatalog catalog,
                                    final RetrieverConnection retrieverConnection,
                                    final SchemaCrawlerOptions options)
                                      throws SchemaCrawlerException
  {
    final StopWatch stopWatch = new StopWatch("crawlSynonyms");

    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveSynonyms = infoLevel.isRetrieveSynonymInformation();
    if (!retrieveSynonyms)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving synonyms, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Crawling synonyms");

    final SynonymRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SynonymRetriever(retrieverConnection, catalog);
      stopWatch.time("retrieveSynonymInformation", () -> {
        retrieverExtra
          .retrieveSynonymInformation(options.getSynonymInclusionRule());
        return null;
      });

      stopWatch.time("sortAndFilterSynonms", () -> {
        ((Reducible) catalog).reduce(Synonym.class,
                                     new SynonymsReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving synonym information",
                                         e);
      }
    }
  }

  private static void crawlTables(final MutableCatalog catalog,
                                  final RetrieverConnection retrieverConnection,
                                  final SchemaCrawlerOptions options)
                                    throws SchemaCrawlerException
  {

    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveTables = infoLevel.isRetrieveTables();
    if (!retrieveTables)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving tables, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlTables");

    LOGGER.log(Level.INFO, "Crawling tables");

    try
    {
      final TableRetriever retriever = new TableRetriever(retrieverConnection,
                                                          catalog);
      final TableColumnRetriever columnRetriever = new TableColumnRetriever(retrieverConnection,
                                                                            catalog);
      final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection,
                                                               catalog);
      final ForeignKeyRetriever fkRetriever = new ForeignKeyRetriever(retrieverConnection,
                                                                      catalog);
      final TableExtRetriever retrieverExtra = new TableExtRetriever(retrieverConnection,
                                                                     catalog);

      stopWatch.time("retrieveTables", () -> {
        for (final Schema schema: retriever.getSchemas())
        {
          retriever.retrieveTables(schema.getCatalogName(),
                                   schema.getName(),
                                   options.getTableNamePattern(),
                                   options.getTableTypes(),
                                   options.getTableInclusionRule());
        }
        return null;
      });

      final NamedObjectList<MutableTable> allTables = catalog.getAllTables();

      stopWatch.time("retrieveColumns", () -> {
        if (infoLevel.isRetrieveTableColumns())
        {
          columnRetriever.retrieveColumns(allTables,
                                          options.getColumnInclusionRule());
        }
        return null;
      });

      stopWatch.time("retrieveForeignKeys", () -> {
        if (infoLevel.isRetrieveForeignKeys())
        {
          if (infoLevel.isRetrieveTableColumns())
          {
            fkRetriever.retrieveForeignKeys(allTables);
          }
        }
        else
        {
          LOGGER
            .log(Level.WARNING,
                 "Foreign-keys are not being retrieved, so tables cannot be sorted using the natural sort order");
        }
        return null;
      });

      stopWatch.time("filterAndSortTables", () -> {
        // Filter the list of tables based on grep criteria, and
        // parent-child relationships
        final Predicate<Table> tableFilter = tableFilter(options);
        ((Reducible) catalog).reduce(Table.class,
                                     new TablesReducer(options, tableFilter));

        // Sort the remaining tables
        final TablesGraph tablesGraph = new TablesGraph(allTables);
        tablesGraph.setTablesSortIndexes();

        return null;
      });

      stopWatch.time("retrieveIndexes", () -> {
        LOGGER.log(Level.INFO, "Retrieving primary keys and indexes");
        if (infoLevel.isRetrieveTableColumns())
        {
          for (final MutableTable table: allTables)
          {
            final boolean isView = table instanceof MutableView;
            if (!isView)
            {
              indexRetriever.retrievePrimaryKey(table);
            }
          }
          if (infoLevel.isRetrieveIndexes())
          {
            indexRetriever.retrieveIndexes(allTables);
            for (final MutableTable table: allTables)
            {
              table.replacePrimaryKey();
            }
          }
        }
        return null;
      });

      stopWatch.time("retrieveTableConstraintInformation", () -> {
        if (infoLevel.isRetrieveTableConstraintInformation())
        {
          retrieverExtra.retrieveTableConstraintInformation();
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
      stopWatch.time("retrieveTableColumnPrivileges", () -> {
        if (infoLevel.isRetrieveTableColumnPrivileges())
        {
          retrieverExtra.retrieveTableColumnPrivileges();
        }
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.toString());
    }
    catch (final Exception e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else if (e instanceof SchemaCrawlerException)
      {
        throw (SchemaCrawlerException) e;
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving table information",
                                         e);
      }
    }
  }

  private final Connection connection;
  private final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection
   *        An database connection.
   * @param databaseSpecificOverrideOptions
   *        Database specific overrides
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public SchemaCrawler(final Connection connection,
                       final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
                         throws SchemaCrawlerException
  {
    this.connection = requireNonNull(connection, "No connection specified");
    this.databaseSpecificOverrideOptions = requireNonNull(databaseSpecificOverrideOptions,
                                                          "No database specific overrides provided");
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @param options
   *        SchemaCrawler options that control what metadata is returned
   * @return Database metadata
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public Catalog crawl(final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final MutableCatalog catalog = new MutableCatalog("catalog");
    try
    {
      final SchemaCrawlerOptions schemaCrawlerOptions;
      if (options == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }
      else
      {
        schemaCrawlerOptions = options;
      }
      final RetrieverConnection retrieverConnection = new RetrieverConnection(connection,
                                                                              databaseSpecificOverrideOptions);

      crawlSchemas(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlDatabaseInfo(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlColumnDataTypes(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlTables(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlRoutines(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlSynonyms(catalog, retrieverConnection, schemaCrawlerOptions);
      crawlSequences(catalog, retrieverConnection, schemaCrawlerOptions);

      return catalog;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Database access exception", e);
    }
  }

}
