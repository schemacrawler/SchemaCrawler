/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;

@ResolveTestContext
@CaptureSystemStreams
public class CommandLineHelpTest {

  private static final String COMMAND_LINE_HELP_OUTPUT = "command_line_help_output/";

  @Test
  public void commandLineHelp(final TestContext testContext, final CapturedSystemStreams streams)
      throws Exception {
    final String server = "oracle";
    Main.main("-h", "server:" + server);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(classpathResource(COMMAND_LINE_HELP_OUTPUT + server + ".help.txt")));
  }
}
