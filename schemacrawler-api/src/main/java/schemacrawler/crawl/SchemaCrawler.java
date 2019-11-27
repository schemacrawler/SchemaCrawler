/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.filter.ReducerFactory.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import sf.util.SchemaCrawlerLogger;
import sf.util.StopWatch;
import sf.util.StringFormat;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawler.class.getName());

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
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(
        retrieverConnection,
        catalog,
        options);

      stopWatch.time("retrieveSystemColumnDataTypes", () -> {
        if (infoLevel.isRetrieveColumnDataTypes())
        {
          LOGGER.log(Level.INFO, "Retrieving system column data types");
          retriever.retrieveSystemColumnDataTypes();
        }
        else
        {
          LOGGER.log(Level.INFO,
                     "Not retrieving system column data types, since this was not requested");
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
          LOGGER.log(Level.INFO,
                     "Not retrieving user column data types, since this was not requested");
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
      throw new SchemaCrawlerException(
        "Exception retrieving column data type information",
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
      final boolean retrieveDatabaseInfo = infoLevel.isRetrieveDatabaseInfo();
      if (!retrieveDatabaseInfo)
      {
        LOGGER.log(Level.INFO,
                   "Not retrieving database information, since this was not requested");
        return;
      }

      final StopWatch stopWatch = new StopWatch("crawlDatabaseInfo");

      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(
        retrieverConnection,
        catalog,
        options);

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
          LOGGER.log(Level.INFO,
                     "Not retrieving additional database information, since this was not requested");
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
          LOGGER.log(Level.INFO,
                     "Not retrieving server information, since this was not requested");
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
          LOGGER.log(Level.INFO,
                     "Not retrieving additional JDBC driver information, since this was not requested");
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
      throw new SchemaCrawlerException(
        "Exception retrieving database information",
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

    final StopWatch stopWatch = new StopWatch("crawlRoutines");

    LOGGER.log(Level.INFO, "Crawling routines");

    final RoutineRetriever retriever;
    final RoutineExtRetriever retrieverExtra;
    final ProcedureParameterRetriever procedureParameterRetriever;
    final FunctionParameterRetriever functionParameterRetriever;
    try
    {
      retriever = new RoutineRetriever(retrieverConnection, catalog, options);
      retrieverExtra = new RoutineExtRetriever(retrieverConnection,
                                               catalog,
                                               options);
      procedureParameterRetriever = new ProcedureParameterRetriever(
        retrieverConnection,
        catalog,
        options);
      functionParameterRetriever = new FunctionParameterRetriever(retrieverConnection,
                                                                  catalog,
                                                                  options);

      final Collection<RoutineType> routineTypes = options.getRoutineTypes();

      stopWatch.time("retrieveRoutines", () -> {
        final NamedObjectList<SchemaReference> schemas = retriever
          .getAllSchemas();
        if (routineTypes.contains(RoutineType.procedure))
        {
          LOGGER.log(Level.INFO, "Retrieving procedure names");
          retriever
            .retrieveProcedures(schemas, options.getRoutineInclusionRule());
        }
        if (routineTypes.contains(RoutineType.function))
        {
          LOGGER.log(Level.INFO, "Retrieving function names");
          retriever
            .retrieveFunctions(schemas, options.getRoutineInclusionRule());
        }
        return null;
      });

      final NamedObjectList<MutableRoutine> allRoutines = catalog
        .getAllRoutines();
      LOGGER.log(Level.INFO,
                 new StringFormat("Retrieved %d routines", allRoutines.size()));
      if (allRoutines.isEmpty())
      {
        return;
      }

      stopWatch.time("retrieveRoutineParameters", () -> {
        LOGGER.log(Level.INFO, "Retrieving routine columns");
        if (infoLevel.isRetrieveRoutineParameters())
        {
          if (routineTypes.contains(RoutineType.procedure))
          {
            procedureParameterRetriever.retrieveProcedureParameters(allRoutines,
                                                                    options
                                                                .getRoutineParameterInclusionRule());
          }

          if (routineTypes.contains(RoutineType.function))
          {
            functionParameterRetriever.retrieveFunctionParameters(allRoutines,
                                                                  options
                                                              .getRoutineParameterInclusionRule());
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
      throw new SchemaCrawlerException(
        "Exception retrieving routine information",
        e);
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
                                                            catalog,
                                                            options);

      stopWatch.time("retrieveSchemas", () -> {
        retriever.retrieveSchemas(options.getSchemaInclusionRule());
        return null;
      });

      stopWatch.time("filterAndSortSchemas", () -> {
        catalog.reduce(Schema.class, getSchemaReducer(options));
        return null;
      });

      LOGGER.log(Level.INFO, stopWatch.stringify());

      final NamedObjectList<SchemaReference> schemas = retriever
        .getAllSchemas();
      if (schemas.isEmpty())
      {
        throw new SchemaCrawlerException("No matching schemas found");
      }
      LOGGER.log(Level.INFO,
                 new StringFormat("Retrieved %d schemas", schemas.size()));
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
      throw new SchemaCrawlerException("Exception retrieving schema information",
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

    final StopWatch stopWatch = new StopWatch("crawlSequences");

    LOGGER.log(Level.INFO, "Crawling sequences");

    final SequenceRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SequenceRetriever(retrieverConnection,
                                             catalog,
                                             options);

      stopWatch.time("retrieveSequenceInformation", () -> {
        retrieverExtra
          .retrieveSequenceInformation(options.getSequenceInclusionRule());
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
      throw new SchemaCrawlerException(
        "Exception retrieving sequence information",
        e);
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

    final StopWatch stopWatch = new StopWatch("crawlSynonyms");

    LOGGER.log(Level.INFO, "Crawling synonyms");

    final SynonymRetriever retrieverExtra;
    try
    {
      retrieverExtra = new SynonymRetriever(retrieverConnection,
                                            catalog,
                                            options);
      stopWatch.time("retrieveSynonymInformation", () -> {
        retrieverExtra
          .retrieveSynonymInformation(options.getSynonymInclusionRule());
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
      throw new SchemaCrawlerException(
        "Exception retrieving synonym information",
        e);
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
      LOGGER
        .log(Level.INFO, "Not retrieving tables, since this was not requested");
      return;
    }

    final StopWatch stopWatch = new StopWatch("crawlTables");

    LOGGER.log(Level.INFO, "Crawling tables");

    try
    {
      final TableRetriever retriever = new TableRetriever(retrieverConnection,
                                                          catalog,
                                                          options);
      final TableColumnRetriever columnRetriever = new TableColumnRetriever(
        retrieverConnection,
        catalog,
        options);
      final IndexRetriever indexRetriever = new IndexRetriever(
        retrieverConnection,
        catalog,
        options);
      final ForeignKeyRetriever fkRetriever = new ForeignKeyRetriever(
        retrieverConnection,
        catalog,
        options);
      final TableConstraintRetriever constraintRetriever = new TableConstraintRetriever(
        retrieverConnection,
        catalog,
        options);
      final TableExtRetriever retrieverExtra = new TableExtRetriever(
        retrieverConnection,
        catalog,
        options);

      stopWatch.time("retrieveTables", () -> {
        LOGGER.log(Level.INFO, "Retrieving table names");
        final NamedObjectList<SchemaReference> schemas = retriever
          .getAllSchemas();
        retriever.retrieveTables(schemas,
                                 options.getTableNamePattern(),
                                 options.getTableTypes(),
                                 options.getTableInclusionRule());
        return null;
      });

      final NamedObjectList<MutableTable> allTables = catalog.getAllTables();
      LOGGER.log(Level.INFO,
                 new StringFormat("Retrieved %d tables", allTables.size()));
      if (allTables.isEmpty())
      {
        return;
      }

      stopWatch.time("retrieveColumns", () -> {
        LOGGER.log(Level.INFO, "Retrieving table columns");
        if (infoLevel.isRetrieveTableColumns())
        {
          columnRetriever
            .retrieveTableColumns(allTables, options.getColumnInclusionRule());
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

      stopWatch.time("retrieveIndexes", () -> {
        LOGGER.log(Level.INFO, "Retrieving primary keys and indexes");
        if (infoLevel.isRetrieveTableColumns())
        {
          if (infoLevel.isRetrieveIndexes())
          {
            indexRetriever.retrieveIndexes(allTables);
          }
          // Setting primary keys will use indexes with a similar name,
          // if available
          indexRetriever.retrievePrimaryKeys(allTables);
          if (infoLevel.isRetrievePrimaryKeyDefinitions())
          {
            retrieverExtra.retrievePrimaryKeyDefinitions(allTables);
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
      throw new SchemaCrawlerException("Exception retrieving table information",
                                       e);
    }
  }

  private final Connection connection;
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private final SchemaCrawlerOptions schemaCrawlerOptions;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   *
   * @param connection             An database connection.
   * @param schemaRetrievalOptions Database-specific schema retrieval overrides
   * @param schemaCrawlerOptions   SchemaCrawler options
   */
  public SchemaCrawler(final Connection connection,
                       final SchemaRetrievalOptions schemaRetrievalOptions,
                       final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.connection = requireNonNull(connection, "No connection specified");
    this.schemaRetrievalOptions = requireNonNull(schemaRetrievalOptions,
                                                 "No database-specific schema retrieval overrides provided");
    this.schemaCrawlerOptions = requireNonNull(schemaCrawlerOptions,
                                               "No SchemaCrawler options provided");
  }

  /**
   * Crawls the database, to obtain database metadata.
   *
   * @return Database metadata
   * @throws SchemaCrawlerException On an exception
   */
  public Catalog crawl()
    throws SchemaCrawlerException
  {
    final MutableCatalog catalog = new MutableCatalog("catalog");
    try
    {
      final RetrieverConnection retrieverConnection = new RetrieverConnection(
        connection,
        schemaRetrievalOptions);

      crawlDatabaseInfo(catalog, retrieverConnection, schemaCrawlerOptions);
      LOGGER.log(Level.INFO, String.format("%n%s", catalog.getCrawlInfo()));

      crawlSchemas(catalog, retrieverConnection, schemaCrawlerOptions);
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
