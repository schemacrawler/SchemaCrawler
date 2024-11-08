/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.string;

import java.util.Formatter;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.Utility;

public final class StringFormat implements Supplier<String> {

  private static final Logger LOGGER = Logger.getLogger(StringFormat.class.getName());

  private final Object[] args;
  private final String format;

  public StringFormat(final String format, final Object... args) {
    // Be tolerant - allow null or blank format strings
    this.format = format;
    this.args = args;
  }

  @Override
  public String get() {
    if (Utility.isBlank(format) || args == null || args.length == 0) {
      return format;
    }

    try (final Formatter formatter = new Formatter()) {
      return formatter.format(format, args).toString();
    } catch (final Throwable e) {
      // NOTE: Do not output arguments, since the toString on argument may throw an exception
      // obscuring this one
      LOGGER.log(Level.FINEST, String.format("Error logging message <%s>", format));
      return "";
    }
  }

  @Override
  public String toString() {
    return get();
  }
}
