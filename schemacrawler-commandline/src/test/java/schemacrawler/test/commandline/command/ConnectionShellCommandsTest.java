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
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.DisconnectCommand;
import schemacrawler.tools.commandline.shell.SweepCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@CaptureSystemStreams
public class ConnectionShellCommandsTest {

  @Test
  public void disconnect(final DatabaseConnectionSource dataSource) {
    final ShellState state = new ShellState();
    state.setDataSource(dataSource); // is-connected

    final String[] args = {};

    assertThat(state.getDataSource(), is(not(nullValue())));

    final DisconnectCommand optionsParser = new DisconnectCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getDataSource(), is(nullValue()));
  }

  @Test
  public void disconnectWhenNotConnected() {
    final ShellState state = new ShellState();

    final String[] args = {};

    assertThat(state.getDataSource(), is(nullValue()));

    final DisconnectCommand optionsParser = new DisconnectCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getDataSource(), is(nullValue()));
  }

  @Test
  public void isConnected(
      final DatabaseConnectionSource dataSource, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();
    state.setDataSource(dataSource); // is-connected

    final String[] args = {"--is-connected"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("HSQL Database Engine"));
  }

  @Test
  public void isNotConnected(
      final DatabaseConnectionInfo connectionInfo, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();

    final String[] args = {"--is-connected"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Not connected to a database"));
  }

  @Test
  public void sweep() {

    final Config config = new Config();
    config.put("key", 1);

    final ShellState state = new ShellState();
    state.setBaseConfig(config);

    assertThat(state.getConfig().size(), is(1));

    final String[] args = {};
    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getConfig().size(), is(0));
  }

  @Test
  public void sweepWithNoState() {

    final ShellState state = new ShellState();

    assertThat(state.getConfig().size(), is(0));

    final String[] args = {};
    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getConfig().size(), is(0));
  }
}
