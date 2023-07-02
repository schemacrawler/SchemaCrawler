/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.naturalOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.string.StringFormat;

/** Command registry for mapping command to executable. */
public final class CommandRegistry {

  private static final Logger LOGGER = Logger.getLogger(CommandRegistry.class.getName());

  private static CommandRegistry commandRegistrySingleton;

  public static final Comparator<? super CommandProvider> commandComparator =
      (commandProvider1, commandProvider2) -> {
        final String fallbackProviderTypeName = "OperationCommandProvider";
        if (commandProvider1 == null || commandProvider2 == null) {
          throw new IllegalArgumentException("Null command provider found");
        }
        final String typeName1 = commandProvider1.getClass().getSimpleName();
        final String typeName2 = commandProvider2.getClass().getSimpleName();
        if (typeName1.equals(typeName2)) {
          return 0;
        } else if (typeName1.equals(fallbackProviderTypeName)) {
          return 1;
        } else if (typeName2.equals(fallbackProviderTypeName)) {
          return -1;
        } else {
          return typeName1.compareTo(typeName2);
        }
      };

  public static CommandRegistry getCommandRegistry() {
    if (commandRegistrySingleton == null) {
      commandRegistrySingleton = new CommandRegistry();
    }
    return commandRegistrySingleton;
  }

  private static List<CommandProvider> loadCommandRegistry() {

    final List<CommandProvider> commandProviders = new ArrayList<>();

    try {
      final ServiceLoader<CommandProvider> serviceLoader =
          ServiceLoader.load(CommandProvider.class, CommandRegistry.class.getClassLoader());
      for (final CommandProvider commandProvider : serviceLoader) {
        LOGGER.log(
            Level.CONFIG,
            new StringFormat(
                "Loading command %s, provided by %s",
                commandProvider.getSupportedCommands(), commandProvider.getClass().getName()));
        commandProviders.add(commandProvider);
      }
    } catch (final Throwable e) {
      throw new InternalRuntimeException("Could not load extended command registry", e);
    }

    return commandProviders;
  }

  private final List<CommandProvider> commandRegistry;

  private CommandRegistry() {
    commandRegistry = loadCommandRegistry();
  }

  public SchemaCrawlerCommand<?> configureNewCommand(
      final String command,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig,
      final OutputOptions outputOptions) {
    final List<CommandProvider> executableCommandProviders = new ArrayList<>();
    findSupportedCommands(
        command, schemaCrawlerOptions, additionalConfig, outputOptions, executableCommandProviders);
    findSupportedOutputFormats(command, outputOptions, executableCommandProviders);

    Collections.sort(executableCommandProviders, commandComparator);

    final CommandProvider executableCommandProvider = executableCommandProviders.get(0);
    LOGGER.log(Level.INFO, new StringFormat("Matched provider <%s>", executableCommandProvider));

    final SchemaCrawlerCommand<?> scCommand;
    try {
      scCommand = executableCommandProvider.newSchemaCrawlerCommand(command, additionalConfig);
      if (scCommand == null) {
        throw new NullPointerException("No SchemaCrawler command instantiated");
      }
      scCommand.setSchemaCrawlerOptions(schemaCrawlerOptions);
      scCommand.setOutputOptions(outputOptions);
    } catch (final ExecutionRuntimeException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    } catch (final Throwable e) {
      // Mainly catch NoClassDefFoundError, which is a Throwable, for
      // missing third-party jars
      LOGGER.log(Level.CONFIG, e.getMessage(), e);
      throw new InternalRuntimeException(String.format("Cannot run command <%s>", command));
    }

    return scCommand;
  }

  public Collection<PluginCommand> getCommandLineCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CommandProvider commandProvider : commandRegistry) {
      commandLineCommands.add(commandProvider.getCommandLineCommand());
    }
    return commandLineCommands;
  }

  public Collection<PluginCommand> getHelpCommands() {
    final Collection<PluginCommand> commandLineCommands = new HashSet<>();
    for (final CommandProvider commandProvider : commandRegistry) {
      commandLineCommands.add(commandProvider.getHelpCommand());
    }
    return commandLineCommands;
  }

  public Collection<CommandDescription> getSupportedCommands() {
    final Collection<CommandDescription> supportedCommandDescriptions = new HashSet<>();
    for (final CommandProvider commandProvider : commandRegistry) {
      supportedCommandDescriptions.addAll(commandProvider.getSupportedCommands());
    }

    final List<CommandDescription> supportedCommandsOrdered =
        new ArrayList<>(supportedCommandDescriptions);
    supportedCommandsOrdered.sort(naturalOrder());
    return supportedCommandsOrdered;
  }

  private void findSupportedCommands(
      final String command,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig,
      final OutputOptions outputOptions,
      final List<CommandProvider> executableCommandProviders) {
    for (final CommandProvider commandProvider : commandRegistry) {
      if (commandProvider.supportsSchemaCrawlerCommand(
          command, schemaCrawlerOptions, additionalConfig, outputOptions)) {
        executableCommandProviders.add(commandProvider);
        LOGGER.log(Level.FINE, new StringFormat("Adding command-provider <%s>", commandProvider));
      }
    }
    if (executableCommandProviders.isEmpty()) {
      throw new ExecutionRuntimeException(String.format("Unknown command <%s>", command));
    }
  }

  private void findSupportedOutputFormats(
      final String command,
      final OutputOptions outputOptions,
      final List<CommandProvider> executableCommandProviders) {
    final Iterator<CommandProvider> iterator = executableCommandProviders.iterator();
    while (iterator.hasNext()) {
      final CommandProvider executableCommandProvider = iterator.next();
      if (!executableCommandProvider.supportsOutputFormat(command, outputOptions)) {
        LOGGER.log(
            Level.FINE,
            new StringFormat(
                "Removing command-provider, since output format is not supported <%s>",
                executableCommandProvider));
        iterator.remove();
      }
    }
    if (executableCommandProviders.isEmpty()) {
      throw new ConfigurationException(
          String.format(
              "Output format <%s> not supported for command <%s>",
              outputOptions.getOutputFormatValue(), command));
    }
  }
}
