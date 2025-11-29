/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.executeCommandInTest;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.FilterOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.FilterCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class FilterCommandTest {

  @Test
  public void allArgs() {
    final String[] args = {
      "--parents", "2", "--children", "2", "--no-empty-tables=true", "additional", "-extra"
    };

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final CommandLine commandLine = newCommandLine(FilterCommand.class, new StateFactory(state));
    commandLine.execute(args);

    final FilterOptions filterOptions = state.getSchemaCrawlerOptions().filterOptions();

    assertThat(filterOptions.parentTableFilterDepth(), is(2));
    assertThat(filterOptions.childTableFilterDepth(), is(2));
  }

  @Test
  public void childrenBadValue() {
    final String[] args = {"--children", "-1"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void childrenNoValue() {
    final String[] args = {"--children"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final CommandLine commandLine = newCommandLine(FilterCommand.class, new StateFactory(state));
    commandLine.parseArgs(args);
    final FilterOptions filterOptions = schemaCrawlerOptions.filterOptions();

    assertThat(filterOptions.parentTableFilterDepth(), is(0));
    assertThat(filterOptions.childTableFilterDepth(), is(0));
  }

  @Test
  public void noValidArgs() throws Throwable {
    final String[] args = {"--some-option"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executeCommandInTest(new FilterCommand(state), args);
    final FilterOptions filterOptions = schemaCrawlerOptions.filterOptions();

    assertThat(filterOptions.parentTableFilterDepth(), is(0));
    assertThat(filterOptions.childTableFilterDepth(), is(0));
  }

  @Test
  public void parentsBadValue() {
    final String[] args = {"--parents", "-1"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void parentsNoValue() {
    final String[] args = {"--parents"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new FilterCommand(state), args));
  }
}
