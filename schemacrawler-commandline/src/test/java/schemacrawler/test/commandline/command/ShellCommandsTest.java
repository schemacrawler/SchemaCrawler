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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.commandline.shell.AvailableCatalogLoadersCommand;
import schemacrawler.tools.commandline.shell.AvailableCommandsCommand;
import schemacrawler.tools.commandline.shell.AvailableServersCommand;
import schemacrawler.tools.commandline.shell.ExitCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

@ResolveTestContext
@CaptureSystemStreams
public class ShellCommandsTest {

  private static final String SHELL_COMMANDS_OUTPUT = "shell_commands_output/";

  @Test
  public void availableCommands(
      final TestContext testContext, final CapturedSystemStreams streams) {
    new AvailableCommandsCommand().run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(
                SHELL_COMMANDS_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void availableLoaders(final TestContext testContext, final CapturedSystemStreams streams) {
    new AvailableCatalogLoadersCommand().run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(
                SHELL_COMMANDS_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void availableServers(final TestContext testContext, final CapturedSystemStreams streams) {
    new AvailableServersCommand().run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(
                SHELL_COMMANDS_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void exit(final TestContext testContext, final CapturedSystemStreams streams) {
    // The exit command is a no-op
    new ExitCommand().run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(outputOf(streams.out()), hasNoContent());
  }

  @Test
  public void system(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--version"};

    final ShellState state = new ShellState();
    newCommandLine(SystemCommand.class, new StateFactory(state)).execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(
                SHELL_COMMANDS_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void systemShowStackTrace(
      final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--show-stacktrace"};

    final RuntimeException exception = new RuntimeException("Test to display stacktrace");
    exception.setStackTrace(new StackTraceElement[0]);

    final ShellState state = new ShellState();
    state.setLastException(exception);

    newCommandLine(SystemCommand.class, new StateFactory(state)).execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(
                SHELL_COMMANDS_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void systemShowStackTraceWithoutException(
      final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--show-stacktrace"};

    final ShellState state = new ShellState();

    newCommandLine(SystemCommand.class, new StateFactory(state)).execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
