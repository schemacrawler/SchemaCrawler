/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@CaptureSystemStreams
public class CommandLineHelpTest {

  private static final String COMMANDLINE_HELP_OUTPUT = "commandline_help_output/";

  @Test
  public void help(final TestContext testContext, final CapturedSystemStreams streams) {
    new CommandLineHelpCommand().run();

    assertThat(outputOf(streams.err()), hasNoContent());
    final String expectedResource =
        COMMANDLINE_HELP_OUTPUT + testContext.testMethodName() + ".stdout.txt";
    assertThat(outputOf(streams.out()), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void helpBadCommand(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "bad-command"};

    assertHelpMessage(testContext, args, false, streams);
  }

  @Test
  public void helpCommand(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "command:test-command"};

    assertHelpMessage(testContext, args, true, streams);
  }

  @Test
  public void helpConnect(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "connect"};

    assertHelpMessage(testContext, args, true, streams);
  }

  @Test
  public void helpDatabaseServer(
      final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "server:test-db"};

    assertHelpMessage(testContext, args, true, streams);
  }

  private void assertHelpMessage(
      final TestContext testContext,
      final String[] args,
      final boolean hasHelpMessage,
      final CapturedSystemStreams streams) {
    final CommandLineHelpCommand optionsParser = new CommandLineHelpCommand();
    new CommandLine(optionsParser).parseArgs(args);
    optionsParser.run();

    assertThat(outputOf(streams.err()), hasNoContent());
    if (hasHelpMessage) {
      final String expectedResource =
          COMMANDLINE_HELP_OUTPUT + testContext.testMethodName() + ".stdout.txt";
      assertThat(outputOf(streams.out()), hasSameContentAs(classpathResource(expectedResource)));
    } else {
      assertThat(outputOf(streams.out()), hasNoContent());
    }
  }
}
