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

package sf.util.clparser;


import java.text.NumberFormat;
import java.text.ParseException;

/**
 * An option that expects a floating-point value.
 */
public final class NumberOption
  extends BaseOption<Number>
{

  public NumberOption(final Character shortForm, final Number defaultValue)
  {
    super(shortForm, defaultValue);
  }

  public NumberOption(final Character shortForm,
                      final String longForm,
                      final Number defaultValue)
  {
    super(shortForm, longForm, defaultValue);
  }

  public NumberOption(final String longForm, final Number defaultValue)
  {
    super(longForm, defaultValue);
  }

  @Override
  protected OptionValue<Number> parseValue(final String arg)
  {
    try
    {
      final Number number = NumberFormat.getNumberInstance().parse(arg);
      return new OptionValue<Number>(this, number);
    }
    catch (final ParseException e)
    {
      return null;
    }
    catch (final NullPointerException e)
    {
      return null;
    }
  }
}
