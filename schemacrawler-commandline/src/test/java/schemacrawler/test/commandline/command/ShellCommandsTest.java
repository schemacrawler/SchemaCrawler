/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.commandline.shell.AvailableCommandsCommand;
import schemacrawler.tools.commandline.shell.AvailableServersCommand;
import schemacrawler.tools.commandline.shell.ExitCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;

@ExtendWith(TestContextParameterResolver.class)
public class ShellCommandsTest
{

  private static final String SHELL_COMMANDS_OUTPUT = "shell_commands_output/";
  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void availableServers(final TestContext testContext)
  {
    new AvailableServersCommand().run();

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out),
               hasSameContentAs(classpathResource(
                 SHELL_COMMANDS_OUTPUT + testContext.testMethodName()
                 + ".stdout.txt")));
  }

  @Test
  public void availableCommands(final TestContext testContext)
  {
    new AvailableCommandsCommand().run();

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out),
               hasSameContentAs(classpathResource(
                 SHELL_COMMANDS_OUTPUT + testContext.testMethodName()
                 + ".stdout.txt")));
  }

  @Test
  @ExpectSystemExitWithStatus(0)
  public void exit(final TestContext testContext)
  {
    new ExitCommand().run();

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out), hasNoContent());
  }

  @Test
  public void system(final TestContext testContext)
  {
    final String[] args = new String[] { "--version" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    newCommandLine(SystemCommand.class, new StateFactory(state), false).execute(
      args);

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out),
               hasSameContentAs(classpathResource(
                 SHELL_COMMANDS_OUTPUT + testContext.testMethodName()
                 + ".stdout.txt")));
  }

  @Test
  public void systemShowStackTrace(final TestContext testContext)
  {
    final String[] args = new String[] { "--show-stacktrace" };

    final RuntimeException exception =
      new RuntimeException("Test to display stacktrace");
    exception.setStackTrace(new StackTraceElement[0]);

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setLastException(exception);

    newCommandLine(SystemCommand.class, new StateFactory(state), false).execute(
      args);

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out),
               hasSameContentAs(classpathResource(
                 SHELL_COMMANDS_OUTPUT + testContext.testMethodName()
                 + ".stdout.txt")));
  }

  @Test
  public void systemShowStackTraceWithoutException(final TestContext testContext)
  {
    final String[] args = new String[] { "--show-stacktrace" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();

    newCommandLine(SystemCommand.class, new StateFactory(state), false).execute(
      args);

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out), hasNoContent());
  }

  @BeforeEach
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

}
