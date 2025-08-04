/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandOptions;

public class CommandOptionsTest {

  @Test
  public void allArgs() {
    final String[] args = {"--command", "a_command", "additional", "--extra"};

    final CommandOptions optionsParser = new CommandOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final String options = optionsParser.getCommand();

    assertThat(options, is("a_command"));
  }

  @Test
  public void blankCommand() {
    final String[] args = {"--command", " "};

    final CommandOptions optionsParser = new CommandOptions();
    new CommandLine(optionsParser).parseArgs(args);
    assertThrows(CommandLine.ParameterException.class, () -> optionsParser.getCommand());
  }

  @Test
  public void commandNoValue() {
    final String[] args = {"--command"};

    final CommandOptions optionsParser = new CommandOptions();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final CommandOptions optionsParser = new CommandOptions();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final CommandOptions optionsParser = new CommandOptions();

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }
}
