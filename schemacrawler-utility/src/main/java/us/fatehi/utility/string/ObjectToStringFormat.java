/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.function.Supplier;
import static us.fatehi.utility.Utility.isBlank;
import us.fatehi.utility.ObjectToString;

public final class ObjectToStringFormat implements Supplier<String> {

  private final String context;
  private final Object args;

  public ObjectToStringFormat(final Object args) {
    this(null, args);
  }

  public ObjectToStringFormat(final String context, final Object args) {
    this.context = context;
    this.args = args;
  }

  @Override
  public String get() {
    final StringBuilder buffer = new StringBuilder();
    if (!isBlank(context)) {
      buffer.append(context).append(System.lineSeparator());
    }
    if (args != null) {
      buffer.append(ObjectToString.toString(args));
    }
    return buffer.toString();
  }

  @Override
  public String toString() {
    return get();
  }
}
