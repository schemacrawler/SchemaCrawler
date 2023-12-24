/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.script;

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
public class CommandLineScriptHelpTest {

  private static final String COMMANDLINE_HELP_OUTPUT = "commandline_help_output/";

  @Test
  public void helpScript(final TestContext testContext, final CapturedSystemStreams streams) {
    final String[] args = {"--help", "command:script"};

    final CommandLineHelpCommand optionsParser = new CommandLineHelpCommand();
    new CommandLine(optionsParser).parseArgs(args);
    optionsParser.run();

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()),
        hasSameContentAs(classpathResource(COMMANDLINE_HELP_OUTPUT + "help.script.stdout.txt")));
  }
}
