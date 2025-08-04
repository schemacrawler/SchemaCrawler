/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class SchemaCrawlerShellCommandsTest {

  @Test
  public void noArgs() {
    final String[] args = new String[] {"bad-command"};

    final SchemaCrawlerShellCommands optionsParser = new SchemaCrawlerShellCommands();
    final ShellState state = new ShellState();
    final CommandLine commandLine = new CommandLine(optionsParser, new StateFactory(state));

    assertThrows(CommandLine.UnmatchedArgumentException.class, () -> commandLine.parseArgs(args));
  }
}
