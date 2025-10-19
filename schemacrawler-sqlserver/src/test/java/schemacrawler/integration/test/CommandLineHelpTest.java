/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@CaptureSystemStreams
public class CommandLineHelpTest {

  private static final String COMMAND_LINE_HELP_OUTPUT = "command_line_help_output/";

  @Test
  public void commandLineHelp(final TestContext testContext, final CapturedSystemStreams streams)
      throws Exception {
    final String server = "sqlserver";
    Main.main("--help", "server:" + server);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(classpathResource(COMMAND_LINE_HELP_OUTPUT + server + ".help.txt")));
  }
}
