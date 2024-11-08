/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.command.LogLevel;

public class LogCommandTest {

  @Test
  public void loglevel() {
    final String[] args = {"--log-level", "FINE"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.FINE));
  }

  @Test
  public void loglevelBadValue() {
    final String[] args = {"--log-level", "BAD"};

    final LogCommand optionsParser = new LogCommand();
    assertThrows(
        CommandLine.ParameterException.class,
        () -> newCommandLine(optionsParser, null).parseArgs(args));
  }

  @Test
  public void loglevelMixedCase() {
    final String[] args = {"--log-level", "FinE"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.FINE));
  }

  @Test
  public void loglevelNoValue() {
    final String[] args = {"--log-level"};

    final LogCommand optionsParser = new LogCommand();
    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> newCommandLine(optionsParser, null).parseArgs(args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.OFF));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final LogCommand optionsParser = new LogCommand();
    newCommandLine(optionsParser, null).parseArgs(args);

    assertThat(optionsParser.getLogLevel(), is(LogLevel.OFF));
  }
}
