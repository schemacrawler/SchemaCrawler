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

package schemacrawler.test.utility;

import java.util.function.Function;
import java.util.regex.Pattern;

final class NeuteredExpressionsFilter implements Function<String, String> {

  private final Pattern[] neuters = {
    // ANSI escape sequences
    Pattern.compile("\u001B\\[[;\\d]*m"),
    Pattern.compile("\u2592"),
    // HSQLDB
    // -- system constraint names
    Pattern.compile("_\\d{5}"),
    // Oracle
    // -- constraint names
    Pattern.compile("SYS_C00\\d{4}"),
    // SQL Server
    // -- primary key names
    Pattern.compile("PK__.{8}__[0-9A-F]{16}"),
    // Multi-threading
    Pattern.compile("main|pool-\\d+-thread-\\d+"),
  };

  @Override
  public String apply(final String line) {
    String neuteredLine = line;
    for (final Pattern neuter : neuters) {
      neuteredLine = neuter.matcher(neuteredLine).replaceAll("");
    }
    return neuteredLine;
  }
}
