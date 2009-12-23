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
public abstract class Executable
{

  private static final Logger LOGGER = Logger.getLogger(Executable.class
    .getName());

  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected String command;
  protected Config config;
  protected OutputOptions outputOptions;

  public Executable()
  {
    schemaCrawlerOptions = new SchemaCrawlerOptions();
    outputOptions = new OutputOptions();
    config = new Config();
  }

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param connection
   *        Database connection
   * @throws Exception
   *         On an exception
   */
  public abstract void execute(Connection connection)
    throws ExecutionException;

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param dataSource
   *        Data-source
   * @throws Exception
   *         On an exception
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

  public final String getCommand()
  {
    return command;
  }

  public final Config getConfig()
  {
    return config;
  }

  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  public final void setCommand(final String command)
  {
    this.command = command;
  }

  public final void setConfig(final Config config)
  {
    if (config != null)
    {
      this.config = config;
    }
  }

  public final void setOutputOptions(final OutputOptions outputOptions)
  {
    if (outputOptions != null)
    {
      this.outputOptions = outputOptions;
    }
  }

  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    if (schemaCrawlerOptions != null)
    {
      this.schemaCrawlerOptions = schemaCrawlerOptions;
    }
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
