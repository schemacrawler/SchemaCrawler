/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

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
class CommandLineImportanceHelpTest {

  private static final String COMMANDLINE_HELP_OUTPUT = "commandline_help_output/";

  @Test
  void helpImportance(final TestContext testContext, final CapturedSystemStreams streams) {

    final String[] args = {"--help", "command:importance"};

    final CommandLineHelpCommand helpCommand = new CommandLineHelpCommand();
    new CommandLine(helpCommand).parseArgs(args);
    helpCommand.run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(
            classpathResource(COMMANDLINE_HELP_OUTPUT + "help.importance.stdout.txt")));
  }
}
