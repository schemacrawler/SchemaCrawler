/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.tools.operation;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutorException;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.tools.util.FormatUtils;
import sf.util.Utilities;

/**
 * Base functionality for operations.
 * 
 * @author sfatehi
 */
public abstract class BaseOperator
  implements CrawlHandler
{

  private static final Logger LOGGER = Logger.getLogger(BaseOperator.class
    .getName());

  private final Connection connection;
  private final DataHandler dataHandler;
  private final Operation operation;
  protected final PrintWriter out;
  private final String query;
  private final Statement statement;
  private int tableCount;
  private final OperatorOptions options;

  /**
   * Constructs a new table dropper.
   * 
   * @param operation
   *          Operation to perform.
   * @param connection
   *          Database connection to use
   */
  BaseOperator(final OperatorOptions options, final String query,
               final Connection connection, final DataHandler dataHandler)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    operation = options.getOperation();
    if (operation == null)
    {
      throw new SchemaCrawlerException("Cannot perform null operation");
    }

    if (dataHandler == null)
    {
      throw new SchemaCrawlerException("No data handler provided");
    }
    this.dataHandler = dataHandler;

    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
    }

    if (query == null)
    {
      throw new SchemaCrawlerException("No query provided");
    }

    try
    {
      if (!operation.isSelectOperation())
      {
        connection.setAutoCommit(true);
      }
      statement = connection.createStatement();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot set autocommit", e);
    }
    this.connection = connection;
    this.query = query;
    try
    {
      out = options.getOutputOptions().getOutputWriter();
    }
    catch (IOException e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {

    try
    {
      if (connection == null || connection.isClosed())
      {
        throw new SchemaCrawlerException("Connection is closed");
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Connection is closed", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {

    try
    {
      connection.close();
      out.flush();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot close connection", e);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#getInfoLevelHint()
   */
  public SchemaInfoLevel getInfoLevelHint()
  {
    return SchemaInfoLevel.BASIC;
  }

  protected Operation getOperation()
  {
    return operation;
  }

  /**
   * Gets the table count.
   * 
   * @return Table count
   */
  public int getTableCount()
  {
    return tableCount;
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#handle(DatabaseInfo)
   */
  public void handle(final DatabaseInfo databaseInfo)
  {
    if (!getNoInfo())
    {
      FormatUtils.printDatabaseInfo(databaseInfo, out);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#handle(Procedure)
   */
  public final void handle(final Procedure procedure)
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlHandler#handle(Table)
   */
  public final void handle(final Table table)
  {

    tableCount++;

    // Create sql
    String sql = null;
    sql = Utilities.expandTemplateFromProperties(query,
                                                 createTableProperties(table));
    sql = Utilities.expandTemplateFromProperties(sql);
    LOGGER.fine("Executing: " + sql);

    ResultSet results = null;
    try
    {
      final boolean hasResults = statement.execute(sql);
      // Pass into data handler for output
      if (hasResults)
      {
        results = statement.getResultSet();
        if (operation.isAggregateOperation())
        {
          handleAggregateOperationForTable(table, results);
        }
        else
        {
          handleOperationForTable(table, results);
        }
      }
      out.flush();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Error executing: " + sql, e);
    }
    catch (final QueryExecutorException e)
    {
      LOGGER.log(Level.WARNING, "Error executing: " + sql, e);
    }
    finally
    {
      try
      {
        if (results != null)
        {
          results.close();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Error releasing resources", e);
      }
    }

  }

  /**
   * Handles an operation, for a given table.
   * 
   * @param table
   *          Table
   * @param results
   *          Results
   * @throws SQLException
   *           On an exception
   */
  private void handleOperationForTable(final Table table,
                                       final ResultSet results)
    throws QueryExecutorException
  {
    dataHandler.handleTitle(table.getName());
    dataHandler.handleData(results);
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   * 
   * @param table
   *          Table
   * @param results
   *          Results
   * @throws SQLException
   *           On an exception
   */
  private void handleAggregateOperationForTable(final Table table,
                                                final ResultSet results)
    throws SQLException
  {
    long aggregate = 0;
    if (results.next())
    {
      aggregate = results.getLong(1);
    }
    final String message = getMessage(aggregate);
    handleTable(tableCount,
                table.getName(),
                table.getType().toString(),
                aggregate,
                message);
  }

  private Properties createTableProperties(final Table table)
  {
    final Properties properties = new Properties();
    properties.setProperty("table", table.getFullName());
    properties.setProperty("columns", table.getColumnsListAsString());
    properties.setProperty("tabletype", table.getType().toString());
    return properties;
  }

  private String getMessage(final double aggregate)
  {

    Number number;
    if (Utilities.isIntegral(aggregate))
    {
      number = new Integer((int) aggregate);
    }
    else
    {
      number = new Double(aggregate);
    }
    final String message = MessageFormat.format(operation
      .getCountMessageFormat(), new Object[] {
      number
    });
    return message;
  }

  /**
   * Prints information on the table.
   * 
   * @param ordinalPosition
   *          Position of table in the schema
   * @param tableName
   *          Table name
   * @param tableType
   *          Table type
   * @param count
   *          Count
   * @param message
   *          Message to print
   */
  public abstract void handleTable(final int ordinalPosition,
                                   final String tableName,
                                   final String tableType, final long count,
                                   final String message);

  boolean getNoFooter()
  {
    return options.getOutputOptions().isNoFooter();
  }

  boolean getNoHeader()
  {
    return options.getOutputOptions().isNoHeader();
  }

  boolean getNoInfo()
  {
    return options.getOutputOptions().isNoInfo();
  }

}
