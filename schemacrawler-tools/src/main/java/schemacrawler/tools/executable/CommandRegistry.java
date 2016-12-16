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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.integration.graph.GraphCommandProvider;
import schemacrawler.tools.integration.scripting.ScriptCommandProvider;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.StringFormat;

/**
 * Command registry for mapping commands to executable.
 *
 * @author Sualeh Fatehi
 */
public final class CommandRegistry
  implements Iterable<String>
{

  private static final Logger LOGGER = Logger
    .getLogger(CommandRegistry.class.getName());

  private static Map<String, CommandProvider> loadCommandRegistry()
    throws SchemaCrawlerException
  {

    final List<CommandProvider> commandProviders = new ArrayList<>();

    for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
      .values())
    {
      commandProviders
        .add(new SchemaExecutableCommandProvider(schemaTextDetailType));
    }

    for (final Operation operation: Operation.values())
    {
      commandProviders.add(new OperationExecutableCommandProvider(operation));
    }

    commandProviders.addAll(Arrays.asList(new ScriptCommandProvider(),
                                          new GraphCommandProvider()));

    try
    {
      final ServiceLoader<CommandProvider> serviceLoader = ServiceLoader
        .load(CommandProvider.class);
      for (final CommandProvider commandRegistryEntry: serviceLoader)
      {
        final String executableCommand = commandRegistryEntry.getCommand();
        LOGGER.log(Level.FINER,
                   new StringFormat("Loading executable, %s=%s",
                                    executableCommand,
                                    commandRegistryEntry.getClass().getName()));
        commandProviders.add(commandRegistryEntry);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load extended command registry",
                                       e);
    }

    final Map<String, CommandProvider> commandRegistry = new HashMap<>();
    for (final CommandProvider commandProvider: commandProviders)
    {
      commandRegistry.put(commandProvider.getCommand(), commandProvider);
    }
    return commandRegistry;
  }

  private final Map<String, CommandProvider> commandRegistry;

  public CommandRegistry()
    throws SchemaCrawlerException
  {
    commandRegistry = loadCommandRegistry();
  }

  public String getHelpAdditionalText(final String command)
  {
    final String helpAdditionalText;
    if (commandRegistry.containsKey(command))
    {
      helpAdditionalText = commandRegistry.get(command).getHelpAdditionalText();
    }
    else
    {
      helpAdditionalText = null;
    }

    return helpAdditionalText;
  }

  public String getHelpResource(final String command)
  {
    final String helpResource;
    if (commandRegistry.containsKey(command))
    {
      helpResource = commandRegistry.get(command).getHelpResource();
    }
    else
    {
      helpResource = null;
    }

    return helpResource;
  }

  public boolean hasCommand(final String command)
  {
    return command != null && commandRegistry.containsKey(command);
  }

  @Override
  public Iterator<String> iterator()
  {
    return lookupAvailableCommands().iterator();
  }

  Executable configureNewExecutable(final String command,
                                    final SchemaCrawlerOptions schemaCrawlerOptions,
                                    final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    final CommandProvider commandProvider;
    if (commandRegistry.containsKey(command))
    {
      commandProvider = commandRegistry.get(command);
    }
    else
    {
      commandProvider = new OperationExecutableCommandProvider(command);
    }

    return commandProvider.configureNewExecutable(schemaCrawlerOptions,
                                                  outputOptions);
  }

  private Collection<String> lookupAvailableCommands()
  {
    final List<String> availableCommands = new ArrayList<>(commandRegistry
      .keySet());
    Collections.sort(availableCommands);
    return availableCommands;
  }

}
