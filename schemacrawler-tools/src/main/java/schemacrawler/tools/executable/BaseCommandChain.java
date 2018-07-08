/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import sf.util.SchemaCrawlerLogger;

/**
 * Allows chaining multiple scCommands with the same configuration. The
 * catalog is obtained just once, and passed on from executable to
 * executable for efficiency in execution.
 */
abstract class BaseCommandChain
  extends BaseSchemaCrawlerCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(BaseCommandChain.class.getName());

  private final List<SchemaCrawlerCommand> scCommands;
  private final CommandRegistry commandRegistry;

  protected BaseCommandChain(final String command)
    throws SchemaCrawlerException
  {
    super(command);

    commandRegistry = new CommandRegistry();
    scCommands = new ArrayList<>();
  }

  @Override
  public final boolean isAvailable()
  {
    return true;
  }

  protected final SchemaCrawlerCommand addNextAndConfigureForExecution(final String command,
                                                                       final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    try
    {
      final SchemaCrawlerCommand scCommand = commandRegistry
        .configureNewCommand(command, schemaCrawlerOptions, outputOptions);
      if (scCommand == null)
      {
        return null;
      }

      scCommand.setAdditionalConfiguration(additionalConfiguration);
      scCommand.setCatalog(catalog);
      scCommand.setConnection(connection);
      scCommand.setIdentifiers(identifiers);

      scCommands.add(scCommand);

      return scCommand;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String
        .format("Cannot chain commands, unknown command <%s>", command));
    }
  }

  protected final void executeChain()
    throws Exception
  {
    if (scCommands.isEmpty())
    {
      LOGGER.log(Level.INFO, "No commands to execute");
      return;
    }

    for (final SchemaCrawlerCommand scCommand: scCommands)
    {
      scCommand.beforeExecute();
      scCommand.execute();
    }
  }

}
