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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.ToolOptions;
import sf.util.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 * 
 * @author Sualeh Fatehi
 * @param <O>
 *        Tool-specific options for execution.
 */
public abstract class Executable<O extends ToolOptions>
{

  private static final Logger LOGGER = Logger.getLogger(Executable.class
    .getName());

  private final String name;
  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected O toolOptions;

  /**
   * Creates an executable with some default options.
   */
  protected Executable(final String name)
  {
    this.name = name;
    schemaCrawlerOptions = new SchemaCrawlerOptions();
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
    throws Exception;

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param dataSource
   *        Data-source
   * @throws Exception
   *         On an exception
   */
  public final void execute(final DataSource dataSource)
    throws Exception
  {
    if (dataSource == null)
    {
      throw new IllegalArgumentException("No data-source provided");
    }
    Connection connection = null;
    try
    {
      connection = dataSource.getConnection();
      LOGGER.log(Level.INFO, "Obtained database connection, " + connection);
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
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the schema crawler options.
   * 
   * @return SchemaCrawlerOptions
   */
  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Gets the tool options.
   * 
   * @return Tool options
   */
  public final O getToolOptions()
  {
    return toolOptions;
  }

  /**
   * Sets the schema crawler options.
   * 
   * @param schemaCrawlerOptions
   *        SchemaCrawlerOptions
   */
  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  /**
   * Gets the tool options.
   * 
   * @param toolOptions
   *        Tool options
   */
  public final void setToolOptions(final O toolOptions)
  {
    this.toolOptions = toolOptions;
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
  protected final void initialize()
  {
    final SchemaInfoLevel infoLevel = toolOptions.getSchemaInfoLevel();
    if (!schemaCrawlerOptions.isAlphabeticalSortForTables()
        && !infoLevel.isRetrieveForeignKeys())
    {
      infoLevel.setRetrieveTableColumns(true);
      infoLevel.setRetrieveForeignKeys(true);
      LOGGER
        .log(Level.WARNING,
             "Adjusted schema info level to retrieve foreign-keys, so tables can be sorted using the natural sort order");
    }
    schemaCrawlerOptions.setSchemaInfoLevel(infoLevel);

    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG, this.toString());
    }
  }

}
