/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.function.Predicate;
import java.util.regex.Pattern;

final class SvgElementFilter implements Predicate<String> {

  private final Pattern start = Pattern.compile("<svg.*");
  private final Pattern end = Pattern.compile(".*svg>");
  private boolean isFiltering;

  @Override
  public boolean test(final String line) {
    if (!isFiltering && start.matcher(line).matches()) {
      isFiltering = true;
      // Filter out the start SVG tag
      return false;
    } else if (isFiltering && end.matcher(line).matches()) {
      isFiltering = false;
      // Filter out the end SVG tag
      return false;
    }

    return !isFiltering;
  }
}
