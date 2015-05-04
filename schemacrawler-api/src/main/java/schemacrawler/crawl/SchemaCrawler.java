/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.schemacrawler.SchemaInfoLevel;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class
    .getName());

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
      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        catalog);
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
      if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
      {
        LOGGER.log(Level.INFO, "Retrieving user column data types");
        for (final Schema schema: retriever.getSchemas())
        {
          retriever.retrieveUserDefinedColumnDataTypes(schema.getCatalogName(),
                                                       schema.getName());
        }
      }
      else
      {
        LOGGER
          .log(Level.INFO,
               "Not retrieving user column data types, since this was not requested");
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving column data type information",
                                       e);
    }
  }

  private static void crawlDatabaseInfo(final MutableCatalog catalog,
                                        final RetrieverConnection retrieverConnection,
                                        final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    try
    {

      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        catalog);

      LOGGER.log(Level.INFO, "Retrieving SchemaCrawler information");

      LOGGER.log(Level.INFO, "Retrieving database information");
      retriever.retrieveDatabaseInfo();
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

      LOGGER.log(Level.INFO, "Retrieving JDBC driver information");
      retriever.retrieveJdbcDriverInfo();
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

      LOGGER.log(Level.INFO,
                 "Retrieving SchemaCrawler crawl header information");
      retriever.retrieveCrawlHeaderInfo(options.getTitle());

    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
  }

  private static void crawlRoutines(final MutableCatalog catalog,
                                    final RetrieverConnection retrieverConnection,
                                    final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveRoutines = infoLevel.isRetrieveRoutines();
    if (!retrieveRoutines)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving routines, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving routines");

    final RoutineRetriever retriever;
    final RoutineExtRetriever retrieverExtra;
    try
    {
      retriever = new RoutineRetriever(retrieverConnection, catalog);
      retrieverExtra = new RoutineExtRetriever(retrieverConnection, catalog);
      final Collection<RoutineType> routineTypes = options.getRoutineTypes();
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
      final NamedObjectList<MutableRoutine> allRoutines = catalog
        .getAllRoutines();
      for (final MutableRoutine routine: allRoutines)
      {
        if (infoLevel.isRetrieveRoutineColumns())
        {
          if (routine instanceof MutableProcedure
              && routineTypes.contains(RoutineType.procedure))
          {
            retriever
              .retrieveProcedureColumns((MutableProcedure) routine,
                                        options.getRoutineColumnInclusionRule());
          }

          if (routine instanceof MutableFunction
              && routineTypes.contains(RoutineType.function))
          {
            retriever
              .retrieveFunctionColumns((MutableFunction) routine,
                                       options.getRoutineColumnInclusionRule());
          }
        }
      }

      // Filter the list of routines based on grep criteria
      final Predicate<Routine> routineFilter = routineFilter(options);
      ((Reducible) catalog).reduce(Routine.class,
                                   new RoutinesReducer(routineFilter));

      if (infoLevel.isRetrieveRoutineInformation())
      {
        retrieverExtra.retrieveRoutineInformation();
      }
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving routines", e);
      }
    }
  }

  private static void crawlSchemas(final MutableCatalog catalog,
                                   final RetrieverConnection retrieverConnection,
                                   final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {

    LOGGER.log(Level.INFO, "Retrieving schemas");

    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection,
                                                            catalog);

      retriever.retrieveSchemas(options.getSchemaInclusionRule());

      ((Reducible) catalog).reduce(Schema.class, new SchemasReducer(options));
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
  }

  private static void crawlSequences(final MutableCatalog catalog,
                                     final RetrieverConnection retrieverConnection,
                                     final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveSequences = infoLevel.isRetrieveSequenceInformation();
    if (!retrieveSequences)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving sequences, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving sequences");

    final SequenceRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SequenceRetriever(retrieverConnection, catalog);
      retrieverExtra.retrieveSequenceInformation(options
        .getSequenceInclusionRule());

      ((Reducible) catalog).reduce(Sequence.class,
                                   new SequencesReducer(options));
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving schemas", e);
      }
    }
  }

  private static void crawlSynonyms(final MutableCatalog catalog,
                                    final RetrieverConnection retrieverConnection,
                                    final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveSynonyms = infoLevel.isRetrieveSynonymInformation();
    if (!retrieveSynonyms)
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving synonyms, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving synonyms");

    final SynonymRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SynonymRetriever(retrieverConnection, catalog);
      retrieverExtra.retrieveSynonymInformation(options
        .getSynonymInclusionRule());

      ((Reducible) catalog).reduce(Synonym.class, new SynonymsReducer(options));
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving schemas", e);
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

    LOGGER.log(Level.INFO, "Retrieving tables");

    final TableRetriever retriever;
    final TableExtRetriever retrieverExtra;
    try
    {
      retriever = new TableRetriever(retrieverConnection, catalog);
      retrieverExtra = new TableExtRetriever(retrieverConnection, catalog);

      for (final Schema schema: retriever.getSchemas())
      {
        retriever.retrieveTables(schema.getCatalogName(),
                                 schema.getName(),
                                 options.getTableNamePattern(),
                                 options.getTableTypes(),
                                 options.getTableInclusionRule());
      }

      final NamedObjectList<MutableTable> allTables = catalog.getAllTables();
      for (final MutableTable table: allTables)
      {
        if (infoLevel.isRetrieveTableColumns())
        {
          retriever.retrieveColumns(table, options.getColumnInclusionRule());
        }
      }

      if (!infoLevel.isRetrieveForeignKeys())
      {
        LOGGER
          .log(Level.WARNING,
               "Foreign-keys are not being retrieved, so tables cannot be sorted using the natural sort order");
      }

      for (final MutableTable table: allTables)
      {
        final boolean isView = table instanceof MutableView;
        if (!isView && infoLevel.isRetrieveTableColumns())
        {
          retriever.retrievePrimaryKey(table);
          if (infoLevel.isRetrieveIndices())
          {
            retriever.retrieveIndices(table, true);
            retriever.retrieveIndices(table, false);
            //
            table.replacePrimaryKey();
          }
          if (infoLevel.isRetrieveForeignKeys())
          {
            retriever.retrieveForeignKeys(table);
          }
        }
      }

      final TablesGraph tablesGraph = new TablesGraph(allTables);
      tablesGraph.setTablesSortIndices();

      // Filter the list of tables based on grep criteria, and
      // parent-child relationships
      final Predicate<Table> tableFilter = tableFilter(options);
      ((Reducible) catalog).reduce(Table.class, new TablesReducer(options,
                                                                  tableFilter));

      if (infoLevel.isRetrieveTableConstraintInformation())
      {
        retrieverExtra.retrieveTableConstraintInformation();
      }
      if (infoLevel.isRetrieveTriggerInformation())
      {
        retrieverExtra.retrieveTriggerInformation();
      }
      if (infoLevel.isRetrieveViewInformation())
      {
        retrieverExtra.retrieveViewInformation();
      }
      if (infoLevel.isRetrieveTableDefinitionsInformation())
      {
        retrieverExtra.retrieveTableDefinitions();
      }
      if (infoLevel.isRetrieveIndexInformation())
      {
        retrieverExtra.retrieveIndexInformation();
      }
      if (infoLevel.isRetrieveAdditionalTableAttributes())
      {
        retrieverExtra.retrieveAdditionalTableAttributes();
      }
      if (infoLevel.isRetrieveTablePrivileges())
      {
        retrieverExtra.retrieveTablePrivileges();
      }
      if (infoLevel.isRetrieveAdditionalColumnAttributes())
      {
        retrieverExtra.retrieveAdditionalColumnAttributes();
      }
      if (infoLevel.isRetrieveTableColumnPrivileges())
      {
        retrieverExtra.retrieveTableColumnPrivileges();
      }

    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        throw new SchemaCrawlerException(e.getMessage(), e.getCause());
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving tables", e);
      }
    }

  }

  private final Connection connection;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection
   *        An database connection.
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public SchemaCrawler(final Connection connection)
    throws SchemaCrawlerException
  {
    this.connection = requireNonNull(connection, "No connection specified");
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

    RetrieverConnection retrieverConnection = null;
    try
    {
      SchemaCrawlerOptions schemaCrawlerOptions = options;
      if (schemaCrawlerOptions == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }
      retrieverConnection = new RetrieverConnection(connection,
                                                    schemaCrawlerOptions);

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
