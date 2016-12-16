/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
