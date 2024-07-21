package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.testcommand.TestCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class CommandRegistryTest {

  @Test
  public void configureNewCommand() {

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();

    assertThrows(
        ExecutionRuntimeException.class,
        () -> commandRegistry.configureNewCommand("bad-command", null, null, null));
    assertThrows(
        InternalRuntimeException.class,
        () -> commandRegistry.configureNewCommand("test-command", null, null, null));

    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputFormatValue("unknown-output-format").toOptions();
    assertThrows(
        ConfigurationException.class,
        () -> commandRegistry.configureNewCommand("test-command", null, null, outputOptions));
  }

  @Test
  public void getCommandLineCommands() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<PluginCommand> commandLineCommands = commandRegistry.getCommandLineCommands();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, hasItem(testCommandProvider.getCommandLineCommand()));
  }

  @Test
  public void getHelpCommands() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<PluginCommand> commandLineCommands = commandRegistry.getHelpCommands();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, hasItem(testCommandProvider.getHelpCommand()));
  }

  @Test
  public void getSupportedCommands() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<CommandDescription> commandLineCommands =
        commandRegistry.getCommandDescriptions();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, is(testCommandProvider.getSupportedCommands()));
  }
}
