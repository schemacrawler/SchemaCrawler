/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.utility.datasource.TestConnectionDatabaseSources.newTestDatabaseConnectionSource;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.SweepCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;

@WithTestDatabase
@CaptureSystemStreams
public class LoadedShellCommandsTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void isLoaded(final Connection connection, final CapturedSystemStreams streams) {
    final ShellState state = createLoadedSchemaCrawlerShellState(connection);

    final String[] args = new String[] {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Database metadata is loaded"));
  }

  @Test
  public void isNotConnected(final Connection connection, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();
    state.setDataSource(newTestDatabaseConnectionSource(connection)); // is-connected

    final String[] args = new String[] {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Database metadata is not loaded"));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sweepCatalog(final Connection connection) {
    final ShellState state = createLoadedSchemaCrawlerShellState(connection);

    final String[] args = new String[0];

    assertThat(state.getCatalog(), is(not(nullValue())));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }

  @Test
  public void sweepCatalogWithNoState() {
    final ShellState state = new ShellState();

    final String[] args = new String[0];

    assertThat(state.getCatalog(), is(nullValue()));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }
}
