/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.test.utility.extensions.FileHasContent.contentsOf;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.DisconnectCommand;
import schemacrawler.tools.commandline.shell.SweepCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@CaptureSystemStreams
public class ConnectionShellCommandsTest {

  @Test
  @WithTestDatabase
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
  @WithTestDatabase
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
  @WithTestDatabase
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

    final Config config = ConfigUtility.newConfig();
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
