/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package sf.util;


import static sf.util.Utility.isBlank;

import java.util.Formatter;
import java.util.function.Supplier;

public final class StringFormat
  implements Supplier<String>
{

  private final String format;
  private final Object[] args;

  public StringFormat(final String format, final Object... args)
  {
    this.format = format;
    this.args = args;
  }

  @Override
  public String get()
  {
    if (isBlank(format) || args == null || args.length == 0)
    {
      return format;
    }

    try (final Formatter formatter = new Formatter();)
    {
      return formatter.format(format, args).toString();
    }
  }

  @Override
  public String toString()
  {
    return get();
  }

}
