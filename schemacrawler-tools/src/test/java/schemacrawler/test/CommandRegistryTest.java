/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.PluginRegistryTestUtility.reload;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.testcommand.TestCommandProvider;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.property.PropertyName;

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
        ExecutionRuntimeException.class,
        () -> commandRegistry.configureNewCommand("test-command", null, null, outputOptions));
  }

  @Test
  public void commandLineCommands() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<PluginCommand> commandLineCommands = commandRegistry.getCommandLineCommands();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, hasItem(testCommandProvider.getCommandLineCommand()));
  }

  @Test
  public void helpCommands() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<PluginCommand> commandLineCommands = commandRegistry.getHelpCommands();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, hasItem(testCommandProvider.getHelpCommand()));
  }

  @Test
  public void registeredPlugins() {

    final TestCommandProvider testCommandProvider = new TestCommandProvider();

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Collection<PropertyName> commandLineCommands = commandRegistry.getRegisteredPlugins();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, is(testCommandProvider.getSupportedCommands()));
  }

  @Test
  public void name() {
    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    assertThat(commandRegistry.getName(), is("SchemaCrawler Commands"));
  }

  @Test
  public void loadError() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCommandProvider.class.getName() + ".force-instantiation-failure", "throw");

          assertThrows(InternalRuntimeException.class, () -> reload(CommandRegistry.class));
        });
    // Reset
    reload(CommandRegistry.class);
  }
}
