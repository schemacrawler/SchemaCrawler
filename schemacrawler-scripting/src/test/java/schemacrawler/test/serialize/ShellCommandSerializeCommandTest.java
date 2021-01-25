/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.serialize;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.oneOf;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class ShellCommandSerializeCommandTest {

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  @Test
  public void shellSerializeJson(final Connection connection)
      throws SchemaCrawlerException, IOException {

    final SerializationFormat serializationFormat = SerializationFormat.json;

    final ShellState state = createLoadedSchemaCrawlerShellState(connection);

    final Path testOutputFile =
        IOUtility.createTempFilePath("test", "." + serializationFormat.name());

    final String[] args = new String[] {"-c", "serialize", "-o", testOutputFile.toString()};

    final ExecuteCommand serializeCommand = new ExecuteCommand(state);
    final CommandLine commandLine = newCommandLine(serializeCommand, null);
    commandLine.parseArgs(args);

    serializeCommand.run();

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out), hasNoContent());

    assertThatOutputIsCorrect(testOutputFile, is(oneOf("7B0D", "7B0A")));
  }

  private void assertThatOutputIsCorrect(
      final Path testOutputFile, final Matcher<String> fileHeaderMatcher) throws IOException {
    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out), hasNoContent());
    assertThat(Files.size(testOutputFile), greaterThan(0L));
    assertThat(fileHeaderOf(testOutputFile), fileHeaderMatcher);
  }
}
