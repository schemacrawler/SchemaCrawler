/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.commandline.parser;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.ApplicationOptions;
import schemacrawler.tools.commandline.parser.ApplicationOptionsParser;

public class ApplicationOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void help()
  {
    final String[] args = { "--help" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(true));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void moreHelp()
  {
    final String[] args = { "--help", "-h" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.OverwrittenOptionException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevelNoValue()
  {
    final String[] args = { "--log-level" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevelBadValue()
  {
    final String[] args = { "--log-level", "BAD" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevel()
  {
    final String[] args = { "--log-level", "FINE" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.FINE));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void loglevelMixedCase()
  {
    final String[] args = { "--log-level", "FinE" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.FINE));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--log-level", "ALL", "-h", "--version", "additional", "-extra" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    optionsParser.parse(args);
    final ApplicationOptions options = optionsParser.getApplicationOptions();

    assertThat(options.getApplicationLogLevel(), is(Level.ALL));
    assertThat(options.isShowHelp(), is(true));
    assertThat(options.isShowVersionOnly(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
