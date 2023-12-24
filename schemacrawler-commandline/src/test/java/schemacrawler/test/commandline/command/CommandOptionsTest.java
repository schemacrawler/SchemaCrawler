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

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandOptions;

public class CommandOptionsTest {

  @Test
  public void allArgs() {
    final String[] args = {"--command", "a_command", "additional", "--extra"};

    final CommandOptions optionsParser = new CommandOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final String options = optionsParser.getCommand();

    assertThat(options, is("a_command"));
  }

  @Test
  public void blankCommand() {
    final String[] args = {"--command", " "};

    final CommandOptions optionsParser = new CommandOptions();
    new CommandLine(optionsParser).parseArgs(args);
    assertThrows(CommandLine.ParameterException.class, () -> optionsParser.getCommand());
  }

  @Test
  public void commandNoValue() {
    final String[] args = {"--command"};

    final CommandOptions optionsParser = new CommandOptions();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final CommandOptions optionsParser = new CommandOptions();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final CommandOptions optionsParser = new CommandOptions();

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> new CommandLine(optionsParser).parseArgs(args));
  }
}
