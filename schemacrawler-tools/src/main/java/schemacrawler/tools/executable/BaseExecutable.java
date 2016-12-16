/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.executable;


import static sf.util.Utility.isBlank;

import java.sql.Connection;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseExecutable
  implements Executable
{

  protected final String command;
  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected OutputOptions outputOptions;
  protected Config additionalConfiguration;

  protected BaseExecutable(final String command)
  {
    if (isBlank(command))
    {
      throw new IllegalArgumentException("No command specified");
    }
    this.command = command;

    schemaCrawlerOptions = new SchemaCrawlerOptions();
    outputOptions = new OutputOptions();
  }

  /**
   * Executes main functionality for SchemaCrawler.
   *
   * @param connection
   *        Database connection
   * @throws Exception
   *         On an exception
   */
  @Override
  public final void execute(final Connection connection)
    throws Exception
  {
    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = SchemaCrawlerUtility
      .matchDatabaseSpecificOverrideOptions(connection);
    execute(connection, databaseSpecificOverrideOptions);
  }

  @Override
  public final Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#getCommand()
   */
  @Override
  public final String getCommand()
  {
    return command;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#getOutputOptions()
   */
  @Override
  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#getSchemaCrawlerOptions()
   */
  @Override
  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  @Override
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
  @Override
  public final void setOutputOptions(final OutputOptions outputOptions)
  {
    if (outputOptions != null)
    {
      this.outputOptions = outputOptions;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#setSchemaCrawlerOptions(schemacrawler.schemacrawler.SchemaCrawlerOptions)
   */
  @Override
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

}
