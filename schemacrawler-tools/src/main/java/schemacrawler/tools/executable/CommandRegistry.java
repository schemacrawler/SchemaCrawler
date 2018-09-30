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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.iosource.EmptyInputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.options.OutputOptions;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Command registry for mapping commands to executable.
 *
 * @author Sualeh Fatehi
 */
public final class CommandRegistry
  implements Iterable<CommandDescription>
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(CommandRegistry.class.getName());

  private static List<CommandProvider> loadCommandRegistry()
    throws SchemaCrawlerException
  {

    final List<CommandProvider> commandProviders = new ArrayList<>();

    commandProviders.add(new SchemaExecutableCommandProvider());
    commandProviders.add(new OperationExecutableCommandProvider());

    try
    {
      final ServiceLoader<CommandProvider> serviceLoader = ServiceLoader
        .load(CommandProvider.class);
      for (final CommandProvider commandProvider: serviceLoader)
      {
        LOGGER.log(Level.CONFIG,
                   new StringFormat("Loading commands %s, provided by %s",
                                    commandProvider.getSupportedCommands(),
                                    commandProvider.getClass().getName()));
        commandProviders.add(commandProvider);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load extended command registry",
                                       e);
    }

    return commandProviders;
  }

  private final List<CommandProvider> commandRegistry;

  public CommandRegistry()
    throws SchemaCrawlerException
  {
    commandRegistry = loadCommandRegistry();
  }

  public InputResource getHelp(final String command)
  {
    for (final CommandProvider commandProvider: commandRegistry)
    {
      if (commandProvider.getSupportedCommands().contains(command))
      {
        return commandProvider.getHelp();
      }
    }
    return new EmptyInputResource();
  }

  public boolean isCommandSupported(final String command)
  {
    for (final CommandProvider commandProvider: commandRegistry)
    {
      for (final String supportedCommand: commandProvider
        .getSupportedCommands())
      {
        if (supportedCommand.equalsIgnoreCase(command))
        {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Iterator<CommandDescription> iterator()
  {
    return getSupportedCommands().iterator();
  }

  SchemaCrawlerCommand configureNewCommand(final String command,
                                           final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    CommandProvider executableCommandProvider = null;
    for (final CommandProvider commandProvider: commandRegistry)
    {
      if (commandProvider.supportsSchemaCrawlerCommand(command,
                                                       schemaCrawlerOptions,
                                                       outputOptions))
      {
        executableCommandProvider = commandProvider;
        break;
      }
    }
    if (executableCommandProvider == null)
    {
      executableCommandProvider = new OperationExecutableCommandProvider();
    }

    final SchemaCrawlerCommand scCommand;
    try
    {
      scCommand = executableCommandProvider.newSchemaCrawlerCommand(command);
      scCommand.setSchemaCrawlerOptions(schemaCrawlerOptions);
      scCommand.setOutputOptions(outputOptions);
    }
    catch (final Throwable e)
    {
      // Mainly catch NoClassDefFoundError, which is a Throwable, for
      // missing third-party jars
      LOGGER.log(Level.CONFIG, e.getMessage(), e);
      throw new SchemaCrawlerCommandLineException(String
        .format("Cannot run command <%s>", command));
    }

    return scCommand;
  }

  private Collection<CommandDescription> getSupportedCommands()
  {
    final Collection<CommandDescription> supportedCommandDescriptions = new HashSet<>();
    for (final CommandProvider commandProvider: commandRegistry)
    {
      final String description = commandProvider.getDescription();
      for (final String command: commandProvider.getSupportedCommands())
      {
        supportedCommandDescriptions
          .add(new CommandDescription(command, description));
      }
    }

    final List<CommandDescription> supportedCommandsOrdered = new ArrayList<>(supportedCommandDescriptions);
    Collections.sort(supportedCommandsOrdered);
    return supportedCommandsOrdered;
  }

}
