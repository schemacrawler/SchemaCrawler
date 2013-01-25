/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;

abstract class BaseCommandChainExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseCommandChainExecutable.class.getName());

  private final List<Executable> executables;
  private final CommandRegistry commandRegistry;

  protected BaseCommandChainExecutable(final String command)
    throws SchemaCrawlerException
  {
    super(command);

    commandRegistry = new CommandRegistry();
    executables = new ArrayList<Executable>();
  }

  public final Executable addNext(final Executable executable)
  {
    if (executable != null)
    {
      executables.add(executable);
    }
    return executable;
  }

  public final Executable addNext(final String command)
    throws SchemaCrawlerException
  {
    try
    {
      final Executable executable = commandRegistry.newExecutable(command);
      if (executable == null)
      {
        return executable;
      }

      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(additionalConfiguration);
      executable.setOutputOptions(outputOptions);
      return addNext(executable);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String.format("Cannot chain executable, unknown command, %s",
                                                     command));
    }
  }

  protected final void executeChain(final Database database,
                                    final Connection connection)
    throws Exception
  {
    if (executables.isEmpty())
    {
      LOGGER.log(Level.INFO, "No commands to execute");
      return;
    }

    for (final Executable executable: executables)
    {
      if (executable instanceof BaseExecutable)
      {
        ((BaseExecutable) executable).executeOn(database, connection);
      }
    }
  }

}
