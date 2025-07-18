/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import schemacrawler.test.utility.DisableLogging;
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
@DisableLogging
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
