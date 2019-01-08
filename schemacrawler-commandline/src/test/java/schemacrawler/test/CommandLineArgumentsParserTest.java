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
package schemacrawler.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.test.utility.IsEmptyMap.isEmptyMap;
import static schemacrawler.test.utility.IsMapWithSize.isMapWithSize;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import us.fatehi.commandlineparser.CommandLineArgumentsParser;

public class CommandLineArgumentsParserTest
{

  @Test
  public void mixed1()
  {
    final String[] args = new String[] {
                                         "-blah",
                                         "value with spaces",
                                         "othernonoption" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, not(hasKey("othernonoption")));
    assertThat(optionsMap, hasEntry("blah", "value with spaces"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, hasSize(1));
    assertThat(nonOptionArguments, not(contains("random")));
    assertThat(nonOptionArguments, contains("othernonoption"));
  }

  @Test
  public void mixed2()
  {
    final String[] args = new String[] {
                                         "-blah",
                                         "value with spaces",
                                         "othernonoption",
                                         "-other option" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(2));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", "value with spaces"));
    assertThat(optionsMap, not(hasKey("othernonoption")));
    assertThat(optionsMap, hasEntry("other option", null));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, hasSize(1));
    assertThat(nonOptionArguments, not(contains("random")));
    assertThat(nonOptionArguments, contains("othernonoption"));
  }

  @Test
  public void nonOptionArguments1()
  {
    final String[] args = new String[] { "nonoption with spaces" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isEmptyMap());

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, hasSize(1));
    assertThat(nonOptionArguments, not(contains("random")));
    assertThat(nonOptionArguments, contains("nonoption with spaces"));
  }

  @Test
  public void nonOptionArguments2()
  {
    final String[] args = new String[] {
                                         "nonoption with spaces",
                                         "othernonoption" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isEmptyMap());

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, hasSize(2));
    assertThat(nonOptionArguments, not(contains("random")));
    assertThat(nonOptionArguments,
               containsInAnyOrder("nonoption with spaces", "othernonoption"));
  }

  @Test
  public void noOption()
  {
    final String[] args = new String[] { "-=blah" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("", "blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
    assertThat(nonOptionArguments, not(contains("random")));
  }

  @Test
  public void noOptionOrValue()
  {
    final String[] args = new String[] { "-" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("", null));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void noOptionOrValueButImplied()
  {
    final String[] args = new String[] { "-=" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("", ""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void noValue()
  {
    final String[] args = new String[] { "-blah" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", null));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void noValue2()
  {
    final String[] args = new String[] { "-blah", "-foo" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(2));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", null));
    assertThat(optionsMap, hasEntry("foo", null));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void noValueButImplied()
  {
    final String[] args = new String[] { "-blah=" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", ""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void optionAndValue()
  {
    final String[] args = new String[] { "-blah=3" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", "3"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

  @Test
  public void repeatedOption()
  {
    final String[] args = new String[] { "-blah=4", "-blah=3" };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertThat(optionsMap, isMapWithSize(1));
    assertThat(optionsMap, not(hasKey("random")));
    assertThat(optionsMap, hasEntry("blah", "3"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertThat(nonOptionArguments, is(empty()));
  }

}
