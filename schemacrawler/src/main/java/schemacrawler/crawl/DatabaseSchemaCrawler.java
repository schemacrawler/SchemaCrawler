/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
public final class DatabaseSchemaCrawler
  implements SchemaCrawler
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseSchemaCrawler.class.getName());

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
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
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
  public DatabaseSchemaCrawler(final Connection connection)
    throws SchemaCrawlerException
  {
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection specified");
    }
    this.connection = connection;

    // Check data source, and obtain the catalog name
    try
    {
      connection.getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      // NOTE: catalog remains null, which is ok for JDBC
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.SchemaCrawler#crawl(schemacrawler.schemacrawler.SchemaCrawlerOptions,
   *      schemacrawler.schemacrawler.CrawlHandler)
   */
  public void crawl(final SchemaCrawlerOptions options,
                    final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }

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

      handler.begin();

      crawlJdbcDriverInfo(retrieverConnection, options, handler);

      final MutableDatabaseInfo databaseInfo = crawlDatabaseInfo(retrieverConnection,
                                                                 options,
                                                                 handler);
      final NamedObjectList<MutableColumnDataType> columnDataTypes = databaseInfo
        .getColumnDataTypesList();

      crawlTables(retrieverConnection,
                  schemaCrawlerOptions,
                  handler,
                  columnDataTypes);
      crawlProcedures(retrieverConnection,
                      schemaCrawlerOptions,
                      handler,
                      columnDataTypes);

      handler.end();
    }
    catch (final SQLException e)
    {
      final String errorMessage = e.getMessage();
      LOGGER.log(Level.WARNING, "Database access error: " + errorMessage);
      throw new SchemaCrawlerException(errorMessage, e);
    }
    finally
    {
      if (retrieverConnection != null)
      {
        retrieverConnection.close();
      }
    }
  }

  private MutableDatabaseInfo crawlDatabaseInfo(final RetrieverConnection retrieverConnection,
                                                final SchemaCrawlerOptions options,
                                                final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    MutableDatabaseInfo dbInfo;
    try
    {
      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection);
      dbInfo = retriever.retrieveDatabaseInfo();
      if (infoLevel.isRetrieveColumnDataTypes())
      {
        retriever.retrieveColumnDataTypes(dbInfo);
      }
      if (infoLevel.isRetrieveDatabaseInfo())
      {
        if (infoLevel.isRetrieveAdditionalDatabaseInfo())
        {
          retriever.retrieveAdditionalDatabaseInfo(dbInfo);
        }
        if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
        {
          retriever.retrieveUserDefinedColumnDataTypes(dbInfo);
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
    handler.handle(dbInfo);

    return dbInfo;
  }

  private void crawlJdbcDriverInfo(final RetrieverConnection retrieverConnection,
                                   final SchemaCrawlerOptions options,
                                   final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    if (infoLevel.isRetrieveJdbcDriverInfo())
    {
      MutableJdbcDriverInfo driverInfo;
      try
      {
        final JdbcDriverInfoRetriever retriever = new JdbcDriverInfoRetriever(retrieverConnection);
        driverInfo = retriever.retrieveJdbcDriverInfo();
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException("Exception retrieving JDBC driver information",
                                         e);
      }
      handler.handle(driverInfo);
    }
  }

  private void crawlProcedures(final RetrieverConnection retrieverConnection,
                               final SchemaCrawlerOptions options,
                               final CrawlHandler handler,
                               final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    boolean retrieveProcedures = options.isShowStoredProcedures()
                                 && infoLevel.isRetrieveProcedures();
    if (!retrieveProcedures)
    {
      return;
    }

    ProcedureRetriever retriever;
    NamedObjectList<MutableProcedure> procedures;
    try
    {
      retriever = new ProcedureRetriever(retrieverConnection);
      final ProcedureExRetriever retrieverExtra = new ProcedureExRetriever(retrieverConnection);
      procedures = retriever
        .retrieveProcedures(options.getTableInclusionRule());
      if (infoLevel.isRetrieveProcedureInformation())
      {
        retrieverExtra.retrieveProcedureInformation(procedures);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving procedures", e);
    }

    for (final MutableProcedure procedure: procedures)
    {
      if (infoLevel.isRetrieveProcedureColumns())
      {
        try
        {
          retriever.retrieveProcedureColumns(procedure, options
            .getColumnInclusionRule(), columnDataTypes);
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException("Error retrieving metadata for procedure "
                                               + procedure,
                                           e);
        }
      }
      // set comparators
      procedure.setColumnComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForProcedureColumns()));
      // handle procedure
      handler.handle(procedure);
    }
  }

  private void crawlTables(final RetrieverConnection retrieverConnection,
                           final SchemaCrawlerOptions options,
                           final CrawlHandler handler,
                           final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    boolean retrieveTables = infoLevel.isRetrieveTables();
    if (!retrieveTables)
    {
      return;
    }

    TableRetriever retriever;
    TableExRetriever retrieverExtra;
    NamedObjectList<MutableTable> tables;
    try
    {
      retriever = new TableRetriever(retrieverConnection);
      retrieverExtra = new TableExRetriever(retrieverConnection);
      tables = retriever.retrieveTables(options.getTableTypes(), options
        .getTableInclusionRule());
      if (infoLevel.isRetrieveCheckConstraintInformation())
      {
        retrieverExtra.retrieveCheckConstraintInformation(tables);
      }
      if (infoLevel.isRetrieveViewInformation())
      {
        retrieverExtra.retrieveViewInformation(tables);
      }
      if (infoLevel.isRetrieveTablePrivileges())
      {
        retrieverExtra.retrieveTablePrivileges(tables);
      }
      if (infoLevel.isRetrieveTriggerInformation())
      {
        retrieverExtra.retrieveTriggerInformation(tables);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving tables", e);
    }

    for (final MutableTable table: tables)
    {
      if (infoLevel.isRetrieveTableColumns())
      {
        try
        {
          retriever.retrieveColumns(table,
                                    options.getColumnInclusionRule(),
                                    columnDataTypes);
          if (infoLevel.isRetrieveTableColumnPrivileges())
          {
            retrieverExtra.retrieveTableColumnPrivileges(table, table
              .getColumnsList());
          }
          retriever.retrievePrimaryKeys(table);
          if (table.getType() != TableType.view)
          {
            if (infoLevel.isRetrieveForeignKeys())
            {
              retriever.retrieveForeignKeys(tables, table);
            }
            if (infoLevel.isRetrieveIndices())
            {
              retriever.retrieveIndices(table, true, false);
              retriever.retrieveIndices(table, false, false);
            }
          }
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException("Error retrieving metadata for table "
                                               + table,
                                           e);
        }
      }
      // set comparators
      table.setColumnComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForTableColumns()));
      table.setForeignKeyComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForForeignKeys()));
      table.setIndexComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForIndexes()));
      // handle table
      handler.handle(table);
    }
  }

}
