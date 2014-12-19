/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
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
        retriever.retrieveSystemColumnDataTypes();
      }
      if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
      {
        for (final Schema schema: retriever.getSchemas())
        {
          retriever.retrieveUserDefinedColumnDataTypes(schema.getCatalogName(),
                                                       schema.getName());
        }
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

      if (infoLevel.isRetrieveSchemaCrawlerInfo())
      {
        retriever.retrieveSchemaCrawlerInfo();
        if (infoLevel.isRetrieveAdditionalSchemaCrawlerInfo())
        {
          retriever.retrieveAdditionalSchemaCrawlerInfo();
        }
      }
      if (infoLevel.isRetrieveDatabaseInfo())
      {
        retriever.retrieveDatabaseInfo();
        if (infoLevel.isRetrieveAdditionalDatabaseInfo())
        {
          retriever.retrieveAdditionalDatabaseInfo();
        }
      }
      if (infoLevel.isRetrieveJdbcDriverInfo())
      {
        retriever.retrieveJdbcDriverInfo();
        if (infoLevel.isRetrieveAdditionalJdbcDriverInfo())
        {
          retriever.retrieveAdditionalJdbcDriverInfo();
        }
      }
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
      return;
    }

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
      ((Reducible) catalog).reduce(options);

      if (infoLevel.isRetrieveRoutineInformation())
      {
        retrieverExtra.retrieveRoutineInformation();
      }
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        final Throwable cause = e.getCause();
        throw new SchemaCrawlerException(e.getMessage() + ": "
                                         + cause.getMessage(), cause);
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
    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection,
                                                            catalog);

      retriever.retrieveSchemas(options.getSchemaInclusionRule());
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
      return;
    }

    final SequenceRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SequenceRetriever(retrieverConnection, catalog);
      retrieverExtra.retrieveSequenceInformation(options
        .getSequenceInclusionRule());
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        final Throwable cause = e.getCause();
        throw new SchemaCrawlerException(e.getMessage() + ": "
                                         + cause.getMessage(), cause);
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
      return;
    }

    final SynonymRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SynonymRetriever(retrieverConnection, catalog);
      retrieverExtra.retrieveSynonymInformation(options
        .getSynonymInclusionRule());
    }
    catch (final SQLException e)
    {
      if (e instanceof SchemaCrawlerSQLException)
      {
        final Throwable cause = e.getCause();
        throw new SchemaCrawlerException(e.getMessage() + ": "
                                         + cause.getMessage(), cause);
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
      return;
    }

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
      ((Reducible) catalog).reduce(options);

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
        final Throwable cause = e.getCause();
        throw new SchemaCrawlerException(e.getMessage() + ": "
                                         + cause.getMessage(), cause);
      }
      else
      {
        throw new SchemaCrawlerException("Exception retrieving tables", e);
      }
    }

  }

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class
    .getName());

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
