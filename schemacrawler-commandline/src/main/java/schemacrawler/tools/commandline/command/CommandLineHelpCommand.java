/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_DESCRIPTION;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_FOOTER;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_HEADER;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_OPTION_LIST;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_PARAMETER_LIST;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommand;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.configureCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandType;

@Command(
    name = "help",
    header = "Display SchemaCrawler command-line help",
    helpCommand = true,
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"help"},
    optionListHeading = "Options:%n")
public final class CommandLineHelpCommand implements Runnable {

  @Parameters(index = "0", arity = "0..1")
  private String command;

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Displays SchemaCrawler command-line help")
  private boolean helpRequested;

  public boolean isHelpRequested() {
    return helpRequested;
  }

  @Override
  public void run() {
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine parent =
        newCommandLine(new SchemaCrawlerShellCommands(), new StateFactory(state), false);

    if (!isBlank(command)) {
      configureHelpForSubcommand(parent);
      showHelpForSubcommand(parent, command);
    } else {
      showCompleteHelp(parent);
    }
  }

  private void configureHelpForSubcommand(final CommandLine commandLine) {
    if (commandLine == null) {
      return;
    }

    commandLine.setHelpSectionKeys(
        Arrays.asList( // SECTION_KEY_HEADER_HEADING,
            SECTION_KEY_HEADER,
            // SECTION_KEY_SYNOPSIS_HEADING,
            // SECTION_KEY_SYNOPSIS,
            // SECTION_KEY_DESCRIPTION_HEADING,
            SECTION_KEY_DESCRIPTION,
            // SECTION_KEY_PARAMETER_LIST_HEADING,
            SECTION_KEY_PARAMETER_LIST,
            // SECTION_KEY_OPTION_LIST_HEADING,
            SECTION_KEY_OPTION_LIST,
            // SECTION_KEY_COMMAND_LIST_HEADING,
            SECTION_KEY_COMMAND_LIST,
            // SECTION_KEY_FOOTER_HEADING,
            SECTION_KEY_FOOTER));
  }

  private Optional<CommandLine> lookupCommand(final CommandLine parent, final String command) {
    return Optional.ofNullable(parent.getSubcommands().get(command));
  }

  private Optional<CommandLine> lookupServerCommand(final String command) {
    final String databaseSystemIdentifier;
    if (command.contains(":")) {
      databaseSystemIdentifier = command.split(":")[1];
    } else {
      databaseSystemIdentifier = command;
    }

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    if (!databaseConnectorRegistry.hasDatabaseSystemIdentifier(databaseSystemIdentifier)) {
      return Optional.empty();
    }

    final DatabaseConnector databaseConnector =
        databaseConnectorRegistry.lookupDatabaseConnector(databaseSystemIdentifier);
    final PluginCommand helpCommand = databaseConnector.getHelpCommand();

    @Command
    class EmptyCommand {}

    final CommandLine commandLine = new CommandLine(new EmptyCommand());
    addPluginCommand(commandLine, helpCommand, false);

    final CommandLine subcommandLine =
        lookupCommand(commandLine, helpCommand.getName()).orElse(null);
    if (subcommandLine == null) {
      return Optional.empty();
    }
    configureCommandLine(subcommandLine);
    configureHelpForSubcommand(subcommandLine);

    return Optional.of(subcommandLine);
  }

  private void showCompleteHelp(final CommandLine parent) {
    new SystemCommand(new SchemaCrawlerShellState()).printVersion();

    System.out.printf("%n%n");

    Stream.of(
            Stream.of("log", "config-file", "connect", "limit", "grep", "filter", "load"),
            StreamSupport.stream(new AvailableCommands().spliterator(), false)
                .map(PluginCommandType.command::toPluginCommandName),
            Stream.of("execute"))
        .flatMap(i -> i)
        .forEach(command -> showHelpForSubcommand(parent, command));
  }

  private void showHelpForSubcommand(final CommandLine parent, final String command) {
    if (parent == null) {
      return;
    }
    if (isBlank(command)) {
      return;
    }

    final CommandLine subCommand =
        lookupServerCommand(command).orElse(lookupCommand(parent, command).orElse(null));
    if (subCommand != null) {
      subCommand.usage(System.out, Help.Ansi.AUTO);
      System.out.printf("%n%n");
    }
  }
}
