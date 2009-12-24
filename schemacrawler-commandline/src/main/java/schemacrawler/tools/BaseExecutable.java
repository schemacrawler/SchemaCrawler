/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import sf.util.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 * 
 * @author Sualeh Fatehi
 * @param <O>
 *        Tool-specific options for execution.
 */
public abstract class BaseExecutable
  implements ExecutableOptions, Executable
{

  private static final long serialVersionUID = -7346631903113057945L;

  private static final Logger LOGGER = Logger.getLogger(BaseExecutable.class
    .getName());

  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected String command;
  protected Config config;
  protected OutputOptions outputOptions;
  protected ConnectionOptions connectionOptions;

  public BaseExecutable()
  {
    schemaCrawlerOptions = new SchemaCrawlerOptions();
    outputOptions = new OutputOptions();
    config = new Config();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute()
   */
  public final void execute()
    throws Exception
  {
    if (connectionOptions == null)
    {
      throw new SchemaCrawlerException("No connection options provided");
    }
    Connection connection = null;
    try
    {
      connection = connectionOptions.createConnection();
      execute(connection);
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
        LOGGER.log(Level.INFO, "Closed database connection, " + connection);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(java.sql.Connection)
   */
  public abstract void execute(Connection connection)
    throws ExecutionException;

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  public final void execute(final DataSource dataSource)
    throws ExecutionException
  {
    if (dataSource == null)
    {
      throw new IllegalArgumentException("No data-source provided");
    }
    Connection connection = null;
    try
    {
      try
      {
        connection = dataSource.getConnection();
      }
      catch (final SQLException e)
      {
        throw new ExecutionException("Could not create database connection", e);
      }
      LOGGER.log(Level.INFO, "Obtained database connection, " + connection);
      execute(connection);
    }
    finally
    {
      if (connection != null)
      {
        try
        {
          connection.close();
          LOGGER.log(Level.INFO, "Closed database connection, " + connection);
        }
        catch (final SQLException e)
        {
          LOGGER.log(Level.WARNING, "Could not close database connection, "
                                    + connection, e);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#getCommand()
   */
  public final String getCommand()
  {
    return command;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#getConfig()
   */
  public final Config getConfig()
  {
    return config;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#getConnectionOptions()
   */
  public final ConnectionOptions getConnectionOptions()
  {
    return connectionOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#getOutputOptions()
   */
  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#getSchemaCrawlerOptions()
   */
  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setCommand(java.lang.String)
   */
  public final void setCommand(final String command)
  {
    this.command = command;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setConfig(schemacrawler.schemacrawler.Config)
   */
  public final void setConfig(final Config config)
  {
    this.config = config;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setConnectionOptions(schemacrawler.schemacrawler.ConnectionOptions)
   */
  public final void setConnectionOptions(final ConnectionOptions connectionOptions)
  {
    this.connectionOptions = connectionOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setExecutableOptions(schemacrawler.tools.ExecutableOptions)
   */
  public final void setExecutableOptions(final ExecutableOptions executableOptions)
  {
    if (executableOptions != null)
    {
      final String command = executableOptions.getCommand();
      if (command != null)
      {
        this.command = command;
      }
      final Config config = executableOptions.getConfig();
      if (config != null)
      {
        this.config = config;
      }

      final ConnectionOptions connectionOptions = executableOptions
        .getConnectionOptions();
      if (connectionOptions != null)
      {
        this.connectionOptions = connectionOptions;
      }
      final OutputOptions outputOptions = executableOptions.getOutputOptions();
      if (outputOptions != null)
      {
        this.outputOptions = outputOptions;
      }
      final SchemaCrawlerOptions schemaCrawlerOptions = executableOptions
        .getSchemaCrawlerOptions();
      if (schemaCrawlerOptions != null)
      {
        this.schemaCrawlerOptions = schemaCrawlerOptions;
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setOutputOptions(schemacrawler.tools.OutputOptions)
   */
  public final void setOutputOptions(final OutputOptions outputOptions)
  {
    this.outputOptions = outputOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#setSchemaCrawlerOptions(schemacrawler.schemacrawler.SchemaCrawlerOptions)
   */
  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public final String toString()
  {
    return ObjectToString.toString(this);
  }

  /**
   * Initializes the executable before execution.
   */
  protected final void adjustSchemaInfoLevel()
  {
    final SchemaInfoLevel infoLevel = schemaCrawlerOptions.getSchemaInfoLevel();
    if (!schemaCrawlerOptions.isAlphabeticalSortForTables()
        && !infoLevel.isRetrieveForeignKeys())
    {
      infoLevel.setRetrieveTableColumns(true);
      infoLevel.setRetrieveForeignKeys(true);
      LOGGER
        .log(Level.WARNING,
             "Adjusted schema info level to retrieve foreign-keys, so tables can be sorted using the natural sort order");
    }

    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG, toString());
    }
  }

}
