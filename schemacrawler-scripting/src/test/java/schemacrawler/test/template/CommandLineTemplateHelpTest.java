/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;

@ResolveTestContext
@CaptureSystemStreams
public class CommandLineTemplateHelpTest {

  private static final String COMMANDLINE_HELP_OUTPUT = "commandline_help_output/";

  @Test
  public void helpTemplate(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "command:template"};

    final CommandLineHelpCommand optionsParser = new CommandLineHelpCommand();
    new CommandLine(optionsParser).parseArgs(args);
    optionsParser.run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(classpathResource(COMMANDLINE_HELP_OUTPUT + "help.template.stdout.txt")));
  }
}
