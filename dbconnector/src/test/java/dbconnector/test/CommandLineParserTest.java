/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package dbconnector.test;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import sf.util.CommandLineParser;

public class CommandLineParserTest
  extends TestCase
{

  public CommandLineParserTest(final String name)
  {
    super(name);
  }

  public void testStandardOptions()
    throws Exception
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.BooleanOption('v', "verbose"));
    parser.addOption(new CommandLineParser.NumberOption('s', "size"));
    parser.addOption(new CommandLineParser.StringOption('n', "name"));
    parser.addOption(new CommandLineParser.NumberOption('f', "fraction"));
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

  public void testBadFormat()
    throws Exception
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParser.NumberOption('s', "size"));
    parser.parse(new String[] {
      "-size=blah"
    });
    assertTrue(!parser.getOption("size").isFound());
  }

  public void testResetBetweenParse()
    throws Exception
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

  public void testCustomOption()
    throws Exception
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

  public void testIllegalCustomOption()
    throws Exception
  {

    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new CommandLineParserTest.ShortDateOption('d', "date"));
    parser.parse(new String[] {
      "-d", "foobar"
    });
    assertNull(parser.getOption("date").getValue());

  }

  public static class ShortDateOption
    extends CommandLineParser.BaseOption
  {

    public ShortDateOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, true);
    }

    protected Object parseValue(final String arg)
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

}
