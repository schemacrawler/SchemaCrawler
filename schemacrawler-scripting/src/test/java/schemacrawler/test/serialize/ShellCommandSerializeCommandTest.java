/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.serialize;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
public class ShellCommandSerializeCommandTest {

  @Test
  public void shellSerializeJson(final DatabaseConnectionSource dataSource) throws IOException {

    final SerializationFormat serializationFormat = SerializationFormat.json;

    final ShellState state = createLoadedSchemaCrawlerShellState(dataSource);

    final Path testOutputFile =
        IOUtility.createTempFilePath("test", "." + serializationFormat.name());

    final String[] args = {"-c", "serialize", "-o", testOutputFile.toString()};

    final ExecuteCommand serializeCommand = new ExecuteCommand(state);
    final CommandLine commandLine = newCommandLine(serializeCommand, null);
    commandLine.parseArgs(args);

    serializeCommand.run();

    assertThatOutputIsCorrect(testOutputFile, is(oneOf("7B0D", "7B0A")));
  }

  private void assertThatOutputIsCorrect(
      final Path testOutputFile, final Matcher<String> fileHeaderMatcher) {
    try {
      assertThat(IOUtility.isFileReadable(testOutputFile), is(true));
      assertThat(fileHeaderOf(testOutputFile), fileHeaderMatcher);
    } catch (final IOException e) {
      fail("Failed asserts", e);
    }
  }
}
