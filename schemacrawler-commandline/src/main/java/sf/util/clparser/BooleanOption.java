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


/**
 * An option that expects a boolean value.
 */
public final class BooleanOption
  extends BaseOption<Boolean>
{

  public BooleanOption(final Character shortForm)
  {
    super(shortForm, false);
  }

  public BooleanOption(final Character shortForm, final String longForm)
  {
    super(shortForm, longForm, false);
  }

  public BooleanOption(final String longForm)
  {
    super(longForm, false);
  }

  @Override
  protected OptionValue<Boolean> parseValue(final String valueString)
  {
    Boolean value;
    if (valueString == null || valueString.length() == 0)
    {
      value = false;
    }
    value = Boolean.valueOf(valueString);
    return new OptionValue<Boolean>(this, value);
  }

}
