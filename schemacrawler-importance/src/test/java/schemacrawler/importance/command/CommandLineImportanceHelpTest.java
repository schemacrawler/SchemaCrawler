/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;

@CaptureSystemStreams
class CommandLineImportanceHelpTest {

  @Test
  void displaysImportanceCommandHelp(final CapturedSystemStreams streams) {
    final CommandLineHelpCommand helpCommand = new CommandLineHelpCommand();
    new CommandLine(helpCommand).parseArgs("--help", "command:importance");
    helpCommand.run();

    assertThat(contentOf(streams), containsString("importance"));
    assertThat(contentOf(streams), containsString("table-filter"));
  }

  private static String contentOf(final CapturedSystemStreams streams) {
    try (final BufferedReader reader = outputOf(streams.out()).openNewReader()) {
      return reader.lines().reduce("", (content, line) -> content + line + System.lineSeparator());
    } catch (final IOException e) {
      throw new IllegalStateException("Could not read command help output", e);
    }
  }
}
