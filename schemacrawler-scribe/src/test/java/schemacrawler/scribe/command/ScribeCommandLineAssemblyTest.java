/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import schemacrawler.tools.commandline.command.CommandOutputOptions;
import schemacrawler.tools.commandline.utility.CommandLineUtility;
import schemacrawler.tools.executable.commandline.PluginCommand;

/**
 * Reproduces the actual picocli command-line assembly (not just {@code getCommandLineCommand()} in
 * isolation): the real SchemaCrawler CLI mixes every command's {@link PluginCommand} into the same
 * picocli {@link CommandLine} that already carries the global {@code CommandOutputOptions} mixin
 * ({@code -o}/{@code -F}/{@code -m}). Declaring a Scribe-specific option with the same name (for
 * example, a duplicate {@code --output-format} or {@code --title}) would fail this assembly with a
 * picocli {@code InitializationException}, exactly as reported when running the real CLI, even
 * though {@code SchemaScribeCommandProviderTest} (which only calls {@code getCommandLineCommand()}
 * directly) would not catch it.
 */
public class ScribeCommandLineAssemblyTest {

  @Command(name = "execute")
  private static final class DummyExecuteCommand {
    @Mixin private CommandOutputOptions commandOutputOptions;
  }

  @Test
  public void scribeCommandMixesInWithoutCollidingWithGlobalOutputOptions() {
    final CommandLine commandLine = new CommandLine(new DummyExecuteCommand());
    final PluginCommand scribePluginCommand = new ScribeCommandProvider().getCommandLineCommand();

    CommandLineUtility.addPluginCommands(commandLine, () -> List.of(scribePluginCommand));

    assertThat(commandLine.getCommandSpec(), notNullValue());
  }
}
