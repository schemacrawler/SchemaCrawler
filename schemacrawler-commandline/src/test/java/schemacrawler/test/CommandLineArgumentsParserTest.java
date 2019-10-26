/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.utility.CommandLineArgumentsParser;

public class CommandLineArgumentsParserTest
{

  @Test
  public void mixed1()
  {
    final List<String> args = Arrays
      .asList("--blah", "value with spaces", "othernonoption");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is("value with spaces"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(1));
    assertThat((nonOptionArguments.contains("random")), is(false));
    assertThat((nonOptionArguments.contains("othernonoption")), is(true));
  }

  @Test
  public void mixed2()
  {
    final List<String> args = Arrays.asList("--blah",
                                            "value with spaces",
                                            "othernonoption",
                                            "--other option");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(2));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is("value with spaces"));
    assertThat((optionsMap.containsKey("other option")), is(true));
    assertThat(optionsMap.get("other option"), is(nullValue()));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(1));
    assertThat((nonOptionArguments.contains("random")), is(false));
    assertThat((nonOptionArguments.contains("othernonoption")), is(true));
  }

  @Test
  public void nonOptionArguments1()
  {
    final List<String> args = Arrays.asList("nonoption with spaces");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(0));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(1));
    assertThat((nonOptionArguments.contains("random")), is(false));
    assertThat((nonOptionArguments.contains("nonoption with spaces")),
               is(true));
  }

  @Test
  public void nonOptionArguments2()
  {
    final List<String> args = Arrays
      .asList("nonoption with spaces", "othernonoption");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(0));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(2));
    assertThat((nonOptionArguments.contains("random")), is(false));
    assertThat((nonOptionArguments.contains("nonoption with spaces")),
               is(true));
    assertThat((nonOptionArguments.contains("othernonoption")), is(true));
  }

  @Test
  public void noOption()
  {
    final List<String> args = Arrays.asList("--=blah");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("")), is(true));
    assertThat(optionsMap.get(""), is("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void noOptionOrValue()
  {
    final List<String> args = Arrays.asList("--");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("")), is(true));
    assertThat(optionsMap.get(""), is(nullValue()));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void noOptionOrValueButImplied()
  {
    final List<String> args = Arrays.asList("--=");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("")), is(true));
    assertThat(optionsMap.get(""), is(""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void noValue()
  {
    final List<String> args = Arrays.asList("--blah");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is(nullValue()));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void noValue2()
  {
    final List<String> args = Arrays.asList("--blah", "--foo");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(2));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is(nullValue()));
    assertThat((optionsMap.containsKey("foo")), is(true));
    assertThat(optionsMap.get("foo"), is(nullValue()));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void noValueButImplied()
  {
    final List<String> args = Arrays.asList("--blah=");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is(""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

  @Test
  public void optionAndValue()
  {
    final List<String> args = Arrays.asList("--blah=3");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();

    assertThat(optionsMap.size(), is(1));

    assertThat((optionsMap.containsKey("random")),

               is(false));

    assertThat((optionsMap.containsKey("blah")),

               is(true));

    assertThat(optionsMap.get("blah"), is("3"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();

    assertThat(nonOptionArguments.size(), is(0));

    assertThat((nonOptionArguments.contains("random")),

               is(false));
  }

  @Test
  public void repeatedOption()
  {
    final List<String> args = Arrays.asList("--blah=4", "--blah=3");

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(
      args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap.size(), is(1));
    assertThat((optionsMap.containsKey("random")), is(false));
    assertThat((optionsMap.containsKey("blah")), is(true));
    assertThat(optionsMap.get("blah"), is("3"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments.size(), is(0));
    assertThat((nonOptionArguments.contains("random")), is(false));
  }

}
