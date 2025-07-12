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
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.command.LogLevel;

public class LogCommandTest {

  @Test
  public void loglevel() {
    final String[] args = {"--log-level", "FINE"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.FINE));
  }

  @Test
  public void loglevelBadValue() {
    final String[] args = {"--log-level", "BAD"};

    final LogCommand optionsParser = new LogCommand();
    assertThrows(
        CommandLine.ParameterException.class,
        () -> newCommandLine(optionsParser, null).parseArgs(args));
  }

  @Test
  public void loglevelMixedCase() {
    final String[] args = {"--log-level", "FinE"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.FINE));
  }

  @Test
  public void loglevelNoValue() {
    final String[] args = {"--log-level"};

    final LogCommand optionsParser = new LogCommand();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> newCommandLine(optionsParser, null).parseArgs(args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.OFF));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.OFF));
  }
}
