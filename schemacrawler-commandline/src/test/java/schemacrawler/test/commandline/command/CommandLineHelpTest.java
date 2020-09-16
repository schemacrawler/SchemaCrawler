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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;

@ExtendWith(TestContextParameterResolver.class)
public class CommandLineHelpTest {

  private static final String COMMANDLINE_HELP_OUTPUT = "commandline_help_output/";

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void help(final TestContext testContext) {
    new CommandLineHelpCommand().run();

    assertThat(outputOf(err), hasNoContent());
    assertThat(
        outputOf(out),
        hasSameContentAs(
            classpathResource(
                COMMANDLINE_HELP_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
  }

  @Test
  public void helpBadCommand(final TestContext testContext) {
    final String[] args = {"--help", "bad-command"};

    assertHelpMessage(testContext, args, false);
  }

  @Test
  public void helpCommand(final TestContext testContext) {
    final String[] args = {"--help", "command:test-command"};

    assertHelpMessage(testContext, args, true);
  }

  @Test
  public void helpConnect(final TestContext testContext) {
    final String[] args = {"--help", "connect"};

    assertHelpMessage(testContext, args, true);
  }

  @Test
  public void helpDatabaseServer(final TestContext testContext) {
    final String[] args = {"--help", "server:test-db"};

    assertHelpMessage(testContext, args, true);
  }

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  private void assertHelpMessage(
      final TestContext testContext, final String[] args, final boolean hasHelpMessage) {
    final CommandLineHelpCommand optionsParser = new CommandLineHelpCommand();
    new CommandLine(optionsParser).parseArgs(args);
    optionsParser.run();

    assertThat(outputOf(err), hasNoContent());
    if (hasHelpMessage) {
      assertThat(
          outputOf(out),
          hasSameContentAs(
              classpathResource(
                  COMMANDLINE_HELP_OUTPUT + testContext.testMethodName() + ".stdout.txt")));
    } else {
      assertThat(outputOf(out), hasNoContent());
    }
  }
}
