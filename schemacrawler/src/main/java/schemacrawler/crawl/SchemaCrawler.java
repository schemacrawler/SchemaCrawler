/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schema.Schema;

/**
 * SchemaCrawler uses database metadata to get the details about the
 * schema.
 */
public final class SchemaCrawler
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class
    .getName());

  /**
   * Gets the entire schema.
   * 
   * @param dataSource
   *        Data source
   * @param additionalConnectionConfiguration
   *        Additional connection configuration for INFORMATION_SCHEMA
   * @param infoLevel
   *        Schema info level
   * @param options
   *        Options
   * @return Schema
   */
  public static Schema getSchema(final DataSource dataSource,
                                 final Properties additionalConnectionConfiguration,
                                 final SchemaInfoLevel infoLevel,
                                 final SchemaCrawlerOptions options)
  {

    Connection connection = null;
    String catalog = null;
    try
    {
      connection = dataSource.getConnection();
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
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      additionalConnectionConfiguration,
                                                      schemaMaker);
      crawler.crawl(options);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return schemaMaker.getSchema();

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
    return getSchema(dataSource, null, infoLevel, options);
  }

  private final DataSource dataSource;

  private final Properties additionalConnectionConfiguration;

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
    this(dataSource, null, crawlHandler);
  }

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param dataSource
   *        An data source.
   * @param crawlHandler
   *        A crawl handler instance
   * @param additionalConnectionConfiguration
   *        Additional connection configuration for INFORMATION_SCHEMA
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public SchemaCrawler(final DataSource dataSource,
                       final Properties additionalConnectionConfiguration,
                       final CrawlHandler crawlHandler)
    throws SchemaCrawlerException
  {

    if (dataSource == null)
    {
      throw new SchemaCrawlerException("No data source specified");
    }
    this.dataSource = dataSource;

    if (additionalConnectionConfiguration == null)
    {
      this.additionalConnectionConfiguration = new Properties();
    }
    else
    {
      this.additionalConnectionConfiguration = additionalConnectionConfiguration;
    }
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

      retrieverConnection = new RetrieverConnection(dataSource,
                                                    additionalConnectionConfiguration);

      final SchemaInfoLevel infoLevel = handler.getInfoLevelHint();
      SchemaCrawlerOptions schemaCrawlerOptions = options;
      if (schemaCrawlerOptions == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }

      handler.begin();
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
      if (infoLevel.isGreaterThan(SchemaInfoLevel.BASIC))
      {
        retriever.retrieveColumnDataTypes(dbInfo);
      }
      if (infoLevel.isGreaterThan(SchemaInfoLevel.VERBOSE))
      {
        retriever.retrieveAdditionalDatabaseInfo(dbInfo);
        retriever.retrieveUserDefinedColumnDataTypes(dbInfo);
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

  private void crawlProcedures(final RetrieverConnection retrieverConnection,
                               final SchemaInfoLevel infoLevel,
                               final SchemaCrawlerOptions options,
                               final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SchemaCrawlerException
  {
    ProcedureRetriever retriever;
    NamedObjectList<MutableProcedure> procedures;
    try
    {
      retriever = new ProcedureRetriever(retrieverConnection);
      final ProcedureExRetriever retrieverExtra = new ProcedureExRetriever(retrieverConnection);
      procedures = retriever.retrieveProcedures(options
        .isShowStoredProcedures(), options.getTableInclusionRule());
      if (infoLevel.isGreaterThanOrEqualTo(SchemaInfoLevel.VERBOSE))
      {
        retrieverExtra.retrieveProcedureInformation(procedures);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving procedures", e);
    }

    for (int i = 0; i < procedures.size(); i++)
    {
      final MutableProcedure procedure = procedures.get(i);
      if (infoLevel != SchemaInfoLevel.MINIMUM)
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
    TableRetriever retriever;
    TableExRetriever retrieverExtra;
    NamedObjectList<MutableTable> tables;
    try
    {
      retriever = new TableRetriever(retrieverConnection);
      retrieverExtra = new TableExRetriever(retrieverConnection);
      tables = retriever.retrieveTables(options.getTableTypes(), options
        .getTableInclusionRule());
      if (infoLevel.isGreaterThanOrEqualTo(SchemaInfoLevel.VERBOSE))
      {
        retrieverExtra.retrieveCheckConstraintInformation(tables);
        retrieverExtra.retrieveViewInformation(tables);
      }
      if (infoLevel == SchemaInfoLevel.MAXIMUM)
      {
        retrieverExtra.retrievePrivileges(null, tables);
        retrieverExtra.retrieveTriggerInformation(tables);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving tables", e);
    }

    for (int i = 0; i < tables.size(); i++)
    {
      final MutableTable table = tables.get(i);
      if (infoLevel.isGreaterThan(SchemaInfoLevel.MINIMUM))
      {
        try
        {
          retriever.retrieveColumns(table,
                                    options.getColumnInclusionRule(),
                                    columnDataTypes);
          if (infoLevel == SchemaInfoLevel.MAXIMUM)
          {
            retrieverExtra.retrievePrivileges(table, table.getColumnsList());
          }
          retriever.retrievePrimaryKeys(table);
          if (!table.getType().isView()
              && infoLevel.isGreaterThanOrEqualTo(SchemaInfoLevel.VERBOSE))
          {
            retriever.retrieveForeignKeys(tables, i);
            retriever.retrieveIndices(table, true, false);
            retriever.retrieveIndices(table, false, false);
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
