/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
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

    final String[] args = new String[0];

    assertThat(state.getDataSource(), is(not(nullValue())));

    final DisconnectCommand optionsParser = new DisconnectCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getDataSource(), is(nullValue()));
  }

  @Test
  public void disconnectWhenNotConnected() {
    final ShellState state = new ShellState();

    final String[] args = new String[0];

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

    final String[] args = new String[] {"--is-connected"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Connected to "));
  }

  @Test
  public void isNotConnected(
      final DatabaseConnectionInfo connectionInfo, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();

    final String[] args = new String[] {"--is-connected"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Not connected to a database"));
  }

  @Test
  public void sweep() {

    final Config config = new Config();
    config.put("key", 1);

    final ShellState state = new ShellState();
    state.setBaseConfig(config);

    assertThat(state.getConfig().size(), is(1));

    final String[] args = new String[0];
    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getConfig().size(), is(0));
  }

  @Test
  public void sweepWithNoState() {

    final ShellState state = new ShellState();

    assertThat(state.getConfig().size(), is(0));

    final String[] args = new String[0];
    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getConfig().size(), is(0));
  }
}
