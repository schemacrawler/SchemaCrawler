/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.SweepCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@CaptureSystemStreams
public class LoadedShellCommandsTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  @WithTestDatabase
  public void isLoaded(
      final DatabaseConnectionSource dataSource, final CapturedSystemStreams streams) {
    final ShellState state = createLoadedSchemaCrawlerShellState(dataSource);

    final String[] args = {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is loaded"));
  }

  @Test
  @WithTestDatabase
  public void isNotConnected(
      final DatabaseConnectionSource dataSource, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();
    state.setDataSource(dataSource); // is-connected

    final String[] args = {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is not loaded"));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  @WithTestDatabase
  public void sweepCatalog(final DatabaseConnectionSource dataSource) {
    final ShellState state = createLoadedSchemaCrawlerShellState(dataSource);

    final String[] args = {};

    assertThat(state.getCatalog(), is(not(nullValue())));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }

  @Test
  @WithTestDatabase
  public void sweepCatalogWithNoState() {
    final ShellState state = new ShellState();

    final String[] args = {};

    assertThat(state.getCatalog(), is(nullValue()));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }
}
