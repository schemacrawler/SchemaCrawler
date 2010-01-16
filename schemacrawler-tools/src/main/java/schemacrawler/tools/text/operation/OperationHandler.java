/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.operation;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.DatabaseTraversalHandler;

/**
 * Text formatting of operations output.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationHandler
  implements DatabaseTraversalHandler
{

  private static final Logger LOGGER = Logger.getLogger(OperationHandler.class
    .getName());

  private final Connection connection;
  private final DataTextFormatter dataFormatter;
  private final Query query;

  /**
   * Text formatting of operations output.
   * 
   * @param options
   *        Options for text formatting of operations output
   */
  public OperationHandler(final Operation operation,
                          final Query query,
                          final OperationOptions options,
                          final OutputOptions outputOptions,
                          final Connection connection)
    throws SchemaCrawlerException
  {
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
    }
    this.connection = connection;

    if (query == null)
    {
      throw new SchemaCrawlerException("No query provided");
    }
    this.query = query;

    if (options == null)
    {
      throw new SchemaCrawlerException("No operation options provided");
    }
    dataFormatter = new DataTextFormatter(operation, options, outputOptions);
  }

  /**
   * {@inheritDoc}
   * 
   * @see DatabaseTraversalHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    try
    {
      if (connection.isClosed())
      {
        throw new SchemaCrawlerException("Connection is closed");
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Connection is closed", e);
    }

    dataFormatter.begin();
  }

  /**
   * {@inheritDoc}
   * 
   * @see DatabaseTraversalHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    if (!query.isQueryOver())
    {
      final String title = query.getName();
      final String sql = query.getQuery();
      executeSqlAndHandleData(title, sql);
    }

    dataFormatter.end();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  public void handle(final ColumnDataType dataType)
    throws SchemaCrawlerException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.ColumnMap[])
   */
  public void handle(final ColumnMap[] weakAssociations)
    throws SchemaCrawlerException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.DatabaseInfo)
   */
  public void handle(final DatabaseInfo database)
    throws SchemaCrawlerException
  {
    dataFormatter.handle(database);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.JdbcDriverInfo)
   */
  public void handle(final JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException
  {
    dataFormatter.handle(jdbcDriverInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.Procedure)
   */
  public void handle(final Procedure procedure)
    throws SchemaCrawlerException
  {
    // No-op
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.DatabaseTraversalHandler#handle(schemacrawler.schema.SchemaCrawlerInfo)
   */
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException
  {
    dataFormatter.handle(schemaCrawlerInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @see DatabaseTraversalHandler#handle(Table)
   */
  public void handle(final Table table)
    throws SchemaCrawlerException
  {
    if (query.isQueryOver())
    {
      final String title = table.getFullName();
      final String sql = query.getQueryForTable(table);
      executeSqlAndHandleData(title, sql);
    }
  }

  private void executeSqlAndHandleData(final String title, final String sql)
    throws SchemaCrawlerException
  {
    LOGGER.fine(String.format("Executing query for %s: %s", title, sql));
    Statement statement = null;
    ResultSet results = null;
    try
    {
      statement = connection.createStatement();
      final boolean hasResults = statement.execute(sql);
      // Pass into data handler for output
      if (hasResults)
      {
        results = statement.getResultSet();
        dataFormatter.handleData(title, results);
      }
    }
    catch (final SQLException e)
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
        if (statement != null)
        {
          statement.close();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Error releasing resources", e);
      }
    }
  }

}
