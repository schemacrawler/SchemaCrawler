/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
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
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.FormattedStringSupplier;

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

    commandProviders.addAll(Arrays
      .asList(new ExecutableCommandProvider("script",
                                            "schemacrawler.tools.integration.scripting.ScriptExecutable"),
              new ExecutableCommandProvider("graph",
                                            "schemacrawler.tools.integration.graph.GraphExecutable")));

    try
    {
      final ServiceLoader<CommandProvider> serviceLoader = ServiceLoader
        .load(CommandProvider.class);
      for (final CommandProvider commandRegistryEntry: serviceLoader)
      {
        final String executableCommand = commandRegistryEntry.getCommand();
        LOGGER.log(Level.FINER,
                   new FormattedStringSupplier("Loading executable, %s=%s",
                                               executableCommand,
                                               commandRegistryEntry.getClass()
                                                 .getName()));
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
      commandProvider = new ExecutableCommandProvider(command,
                                                      "schemacrawler.tools.text.operation.OperationExecutable");
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
