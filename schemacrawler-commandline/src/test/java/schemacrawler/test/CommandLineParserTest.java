/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import sf.util.CommandLineParser;

public class CommandLineParserTest
{

  private static class ShortDateOption
    extends CommandLineParser.BaseOption<Calendar>
  {

    ShortDateOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, null);
    }

    @Override
    protected Calendar parseValue(final String arg)
    {
      try
      {
        final Date date = DateFormat.getDateInstance(DateFormat.SHORT)
          .parse(arg);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
      }
      catch (final ParseException e)
      {
        return null;
      }
    }
  }

  @Test
  public void badFormat()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.NumberOption('s', "size", null));
    parser.parse(new String[] {
      "-size=blah"
    });
    assertTrue(!parser.getOption("size").isFound());
  }

  @Test
  public void booleanOptions()
  {
    final String verbose = "verbose";

    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.BooleanOption('v', verbose));
    parser.addOption(new CommandLineParser.StringOption('n', "name", null));

    parser.parse(new String[] {
        "-v", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v=true", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v", "true", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
      "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v=false", "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v", "false", "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanOptionValue(verbose));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));
  }

  @Test
  public void illegalCustomOption()
  {

    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParserTest.ShortDateOption('d', "date"));
    parser.parse(new String[] {
        "-d", "foobar"
    });
    assertNull(parser.getOption("date").getValue());

  }

  @Test
  public void resetBetweenParse()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.BooleanOption('v', "verbose"));
    parser.parse(new String[] {
      "-v"
    });
    assertEquals(Boolean.TRUE, parser.getOption("verbose").getValue());
    assertTrue(parser.getOption("verbose").isFound());
    parser.parse(new String[] {});
    assertEquals(Boolean.FALSE, parser.getOption("verbose").getValue());
    assertTrue(!parser.getOption("verbose").isFound());
  }

  @Test
  public void standardOptions()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.BooleanOption('v', "verbose"));
    parser.addOption(new CommandLineParser.NumberOption('s', "size", null));
    parser.addOption(new CommandLineParser.StringOption('n', "name", null));
    parser.addOption(new CommandLineParser.NumberOption('f', "fraction", null));
    parser.addOption(new CommandLineParser.BooleanOption('m', "missing"));
    assertNull(parser.getOption("size").getValue());
    parser.parse(new String[] {
        "-v", "-size=100", "-n", "foo", "-f", "0.1", "rest"
    });
    assertTrue(!parser.getOption("missing").isFound());
    assertEquals(Boolean.TRUE, parser.getOption("verbose").getValue());
    assertEquals(100, ((Number) parser.getOption("size").getValue()).intValue());
    assertEquals("foo", parser.getOption("name").getValue());
    assertEquals(0.1, ((Number) parser.getOption("fraction").getValue())
      .doubleValue(), 0.1e-6);
    final String[] otherArgs = parser.getRemainingArgs();
    assertEquals(1, otherArgs.length);
    assertEquals("rest", otherArgs[0]);
  }

  @Test
  public void stringOptions()
  {
    final String string = "string";

    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.StringOption('s', string, null));
    parser.addOption(new CommandLineParser.StringOption('n', "name", null));

    parser.parse(new String[] {
      "-name=schemacrawler"
    });
    assertFalse(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s", "-name=schemacrawler"
    });
    assertFalse(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string", "-name=schemacrawler"
    });
    assertFalse(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s=value", "-name=schemacrawler"
    });
    assertTrue(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue(string).equals("value"));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s", "value", "-name=schemacrawler"
    });
    assertTrue(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue(string).equals("value"));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string=value", "-name=schemacrawler"
    });
    assertTrue(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue(string).equals("value"));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string", "value", "-name=schemacrawler"
    });
    assertTrue(parser.getOption(string).isFound());
    assertTrue(parser.getStringOptionValue(string).equals("value"));
    assertTrue(parser.getStringOptionValue("name").equals("schemacrawler"));
  }

  @Test
  public void testCustomOption()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParserTest.ShortDateOption('d', "date"));
    parser.parse(new String[] {
        "-d", "11/03/2003"
    });
    final Calendar d = (Calendar) parser.getOption("date").getValue();
    assertEquals(11, d.get(Calendar.MONTH) + 1);
    assertEquals(3, d.get(Calendar.DATE));
    assertEquals(2003, d.get(Calendar.YEAR));
  }

}
