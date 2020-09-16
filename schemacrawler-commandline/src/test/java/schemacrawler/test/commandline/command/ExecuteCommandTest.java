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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.retrievePluginOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class ExecuteCommandTest {

  @Test
  public void executeCommand(final Connection connection, final TestContext testContext)
      throws SchemaCrawlerException, IOException {

    final SchemaCrawlerShellState state = createLoadedSchemaCrawlerShellState(connection);

    final Path testOutputFile = IOUtility.createTempFilePath("test", ".txt");

    final String[] args = new String[] {"-c", "schema", "-o", testOutputFile.toString()};

    final ExecuteCommand serializeCommand = new ExecuteCommand(state);
    final CommandLine commandLine = newCommandLine(serializeCommand, null, false);

    commandLine.execute(args);

    assertThat(
        outputOf(testOutputFile),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void testPluginOptions() throws SchemaCrawlerException {

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();

    final String[] args =
        new String[] {
          "-c",
          "test",
          "--test-command-parameter",
          "parameter-value",
          "--unknown-parameter",
          "some-value"
        };

    final ExecuteCommand executeTestCommand = new ExecuteCommand(state);
    final CommandLine commandLine = newCommandLine(executeTestCommand, null, false);

    final CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
    final Config additionalConfig = retrievePluginOptions(parseResult);

    assertThat(additionalConfig, hasEntry(is("test-command-parameter"), is("parameter-value")));
    assertThat(additionalConfig, not(hasKey(is("unknown-parameter"))));

    assertThrows(CommandLine.ExecutionException.class, () -> executeTestCommand.run());
  }
}
