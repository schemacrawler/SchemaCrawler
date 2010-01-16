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

package schemacrawler.tools.executable;


import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.OutputOptions;
import sf.util.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 * 
 * @author Sualeh Fatehi
 * @param <O>
 *        Tool-specific options for execution.
 */
public abstract class BaseExecutable
  implements Executable
{

  private static final long serialVersionUID = -7346631903113057945L;

  private static final Logger LOGGER = Logger.getLogger(BaseExecutable.class
    .getName());

  protected final String command;
  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected OutputOptions outputOptions;
  protected Config additionalConfiguration;

  public BaseExecutable(final String command)
  {
    this.command = command;
    schemaCrawlerOptions = new SchemaCrawlerOptions();
    outputOptions = new OutputOptions();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#execute(java.sql.Connection)
   */
  public final void execute(final Connection connection)
    throws Exception
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    adjustSchemaInfoLevel();

    final SchemaCrawler crawler = new SchemaCrawler(connection);
    final Database database = crawler.crawl(schemaCrawlerOptions);
    executeOn(database, connection);
  }

  public final Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#getCommand()
   */
  public final String getCommand()
  {
    return command;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#getOutputOptions()
   */
  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#getSchemaCrawlerOptions()
   */
  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  public final void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    if (additionalConfiguration == null)
    {
      this.additionalConfiguration = new Config();
    }
    else
    {
      this.additionalConfiguration = additionalConfiguration;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#setOutputOptions(schemacrawler.tools.options.OutputOptions)
   */
  public final void setOutputOptions(final OutputOptions outputOptions)
  {
    this.outputOptions = outputOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.executable.Executable#setSchemaCrawlerOptions(schemacrawler.schemacrawler.SchemaCrawlerOptions)
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

  protected abstract void executeOn(Database database, Connection connection)
    throws Exception;

  /**
   * Initializes the executable before execution.
   */
  private final void adjustSchemaInfoLevel()
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
