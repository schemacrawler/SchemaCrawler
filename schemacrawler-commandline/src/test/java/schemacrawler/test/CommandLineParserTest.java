/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

import sf.util.clparser.BaseOption;
import sf.util.clparser.BooleanOption;
import sf.util.clparser.CommandLineParser;
import sf.util.clparser.NumberOption;
import sf.util.clparser.OptionValue;
import sf.util.clparser.StringOption;

public class CommandLineParserTest
{

  private static class ShortDateOption
    extends BaseOption<Calendar>
  {

    ShortDateOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, null);
    }

    @Override
    protected OptionValue<Calendar> parseValue(final String arg)
    {
      try
      {
        final Date date = DateFormat.getDateInstance(DateFormat.SHORT)
          .parse(arg);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new OptionValue<Calendar>(this, calendar);
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
    final CommandLineParser parser = new CommandLineParser(new NumberOption('s',
                                                                            "size",
                                                                            null));
    parser.parse(new String[] {
      "-size=blah"
    });
    assertTrue(!parser.hasOptionValue("size"));
  }

  @Test
  public void booleanOptions()
  {
    final String verbose = "verbose";

    final CommandLineParser parser = new CommandLineParser(new BooleanOption('v',
                                                                             verbose),
                                                           new StringOption('n',
                                                                            "name",
                                                                            null));

    parser.parse(new String[] {
        "-v", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v=true", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v", "true", "-name=schemacrawler"
    });
    assertTrue(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
      "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v=false", "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-v", "false", "-name=schemacrawler"
    });
    assertFalse(parser.getBooleanValue(verbose));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));
  }

  @Test
  public void customOption()
  {
    final CommandLineParser parser = new CommandLineParser(new CommandLineParserTest.ShortDateOption('d',
                                                                                                     "date"));
    parser.parse(new String[] {
        "-d", "11/03/2003"
    });
    final Calendar d = (Calendar) parser.getValue("date");
    assertEquals(11, d.get(Calendar.MONTH) + 1);
    assertEquals(3, d.get(Calendar.DATE));
    assertEquals(2003, d.get(Calendar.YEAR));
  }

  @Test
  public void illegalCustomOption()
  {

    final CommandLineParser parser = new CommandLineParser(new CommandLineParserTest.ShortDateOption('d',
                                                                                                     "date"));
    parser.parse(new String[] {
        "-d", "foobar"
    });
    assertNull(parser.getValue("date"));

  }

  @Test
  public void missingValue1()
  {
    final CommandLineParser parser = new CommandLineParser(new NumberOption('s',
                                                                            "size",
                                                                            null));
    parser.parse(new String[] {
      "-size="
    });
    assertTrue(!parser.hasOptionValue("size"));
  }

  @Test
  public void missingValue2()
  {
    final CommandLineParser parser = new CommandLineParser(new NumberOption('s',
                                                                            "size",
                                                                            null));
    parser.parse(new String[] {
      "-size"
    });
    assertTrue(!parser.hasOptionValue("size"));
  }

  @Test
  public void repeatedOption()
  {
    final CommandLineParser parser = new CommandLineParser(new NumberOption('n',
                                                                            "number",
                                                                            null));
    final String[] remainingArgs = parser.parse(new String[] {
        "-number=4", "-number=5"
    });
    assertEquals(Integer.valueOf(4), parser.getIntegerValue("number"));

    assertEquals(1, remainingArgs.length);
    assertEquals("-number=5", remainingArgs[0]);
  }

  @Test
  public void resetBetweenParse()
  {
    final CommandLineParser parser = new CommandLineParser(new BooleanOption('v',
                                                                             "verbose"));
    parser.parse(new String[] {
      "-v"
    });
    assertEquals(true, parser.getBooleanValue("verbose"));
    assertTrue(parser.hasOptionValue("verbose"));
    parser.parse(new String[] {});
    assertEquals(false, parser.getBooleanValue("verbose"));
    assertTrue(!parser.hasOptionValue("verbose"));
  }

  @Test
  public void standardOptions()
  {
    final CommandLineParser parser = new CommandLineParser(new BooleanOption('v',
                                                                             "verbose"),
                                                           new NumberOption('s',
                                                                            "size",
                                                                            null),
                                                           new StringOption('n',
                                                                            "name",
                                                                            null),
                                                           new NumberOption('f',
                                                                            "fraction",
                                                                            null),
                                                           new BooleanOption('m',
                                                                             "missing"));
    assertNull(parser.getIntegerValue("size"));
    final String[] unparsedArgs = parser.parse(new String[] {
        "-v", "-size=100", "-n", "foo", "-f", "0.1", "rest"
    });
    assertTrue(!parser.hasOptionValue("missing"));
    assertEquals(true, parser.getBooleanValue("verbose"));
    assertEquals(Integer.valueOf(100), parser.getIntegerValue("size"));
    assertEquals("foo", parser.getStringValue("name"));
    assertEquals(0.1,
                 ((Number) parser.getValue("fraction")).doubleValue(),
                 0.1e-6);
    assertEquals(1, unparsedArgs.length);
    assertEquals("rest", unparsedArgs[0]);
  }

  @Test
  public void stringOptions()
  {
    final String string = "string";

    final CommandLineParser parser = new CommandLineParser(new StringOption('s',
                                                                            string,
                                                                            null),
                                                           new StringOption('n',
                                                                            "name",
                                                                            null));

    parser.parse(new String[] {
      "-name=schemacrawler"
    });
    assertFalse(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s", "-name=schemacrawler"
    });
    assertFalse(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string", "-name=schemacrawler"
    });
    assertFalse(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s=value", "-name=schemacrawler"
    });
    assertTrue(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue(string).equals("value"));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-s", "value", "-name=schemacrawler"
    });
    assertTrue(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue(string).equals("value"));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string=value", "-name=schemacrawler"
    });
    assertTrue(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue(string).equals("value"));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));

    parser.parse(new String[] {
        "-string", "value", "-name=schemacrawler"
    });
    assertTrue(parser.hasOptionValue(string));
    assertTrue(parser.getStringValue(string).equals("value"));
    assertTrue(parser.getStringValue("name").equals("schemacrawler"));
  }

}
