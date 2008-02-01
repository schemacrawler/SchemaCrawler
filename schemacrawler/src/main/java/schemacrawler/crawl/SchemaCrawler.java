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

import javax.sql.DataSource;

import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schema.TableType;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
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
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
  }

  /**
   * Gets the entire schema.
   * 
   * @param dataSource
   *        Data source
   * @param infoLevel
   *        Schema info level
   * @param options
   *        Options
   * @return Schema
   */
  public static Schema getSchema(final DataSource dataSource,
                                 final SchemaInfoLevel infoLevel,
                                 final SchemaCrawlerOptions options)
  {
    Connection connection;
    try
    {
      connection = dataSource.getConnection();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return null;
    }

    String catalog = null;
    try
    {
      catalog = connection.getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      // NOTE: catalog remains null, which is ok for JDBC
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not close connection", e);
      }
    }

    final CachingCrawlerHandler schemaMaker = new CachingCrawlerHandler(catalog,
                                                                        infoLevel);
    try
    {
      final SchemaCrawler crawler = new SchemaCrawler(dataSource, schemaMaker);
      crawler.crawl(options);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return schemaMaker.getSchema();
  }

  private final DataSource dataSource;
  private final CrawlHandler handler;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param dataSource
   *        An data source.
   * @param crawlHandler
   *        A crawl handler instance
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public SchemaCrawler(final DataSource dataSource,
                       final CrawlHandler crawlHandler)
    throws SchemaCrawlerException
  {
    if (dataSource == null)
    {
      throw new SchemaCrawlerException("No data source specified");
    }
    this.dataSource = dataSource;

    if (crawlHandler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }
    handler = crawlHandler;
  }

  /**
   * Crawls the schema for all tables and views.
   * 
   * @param options
   *        Options
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void crawl(final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    RetrieverConnection retrieverConnection = null;
    try
    {
      SchemaCrawlerOptions schemaCrawlerOptions = options;
      if (schemaCrawlerOptions == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }
      retrieverConnection = new RetrieverConnection(dataSource,
                                                    schemaCrawlerOptions);

      final SchemaInfoLevel infoLevel = handler.getInfoLevelHint();
      handler.begin();

      crawlJdbcDriverInfo(retrieverConnection, infoLevel);

      final MutableDatabaseInfo databaseInfo = crawlDatabaseInfo(retrieverConnection,
                                                                 infoLevel);
      final NamedObjectList<MutableColumnDataType> columnDataTypes = databaseInfo
        .getColumnDataTypesList();

      crawlTables(retrieverConnection,
                  infoLevel,
                  schemaCrawlerOptions,
                  columnDataTypes);
      crawlProcedures(retrieverConnection,
                      infoLevel,
                      schemaCrawlerOptions,
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
                                                final SchemaInfoLevel infoLevel)
    throws SchemaCrawlerException
  {
    MutableDatabaseInfo dbInfo;
    try
    {
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
                                   final SchemaInfoLevel infoLevel)
    throws SchemaCrawlerException
  {
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
                               final SchemaInfoLevel infoLevel,
                               final SchemaCrawlerOptions options,
                               final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SchemaCrawlerException
  {
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
      procedure.setColumnComparator(options.getProcedureColumnComparator());
      // handle procedure
      handler.handle(procedure);
    }
  }

  private void crawlTables(final RetrieverConnection retrieverConnection,
                           final SchemaInfoLevel infoLevel,
                           final SchemaCrawlerOptions options,
                           final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SchemaCrawlerException
  {
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
              retriever.retrieveForeignKeys(tables, table.getName());
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
      table.setColumnComparator(options.getTableColumnComparator());
      table.setForeignKeyComparator(options.getTableForeignKeyComparator());
      table.setIndexComparator(options.getTableIndexComparator());
      // handle table
      handler.handle(table);
    }
  }

}
