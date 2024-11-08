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

package schemacrawler.tools.lint;

import static us.fatehi.utility.IOUtility.readResourceFully;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class LintUtility {

  public static final Logger LOGGER = Logger.getLogger(LintUtility.class.getName());

  public static <E> boolean listStartsWith(final List<E> main, final List<E> sub) {
    if (main == null || sub == null || (main.size() < sub.size())) {
      return false;
    }
    if (main.isEmpty()) {
      return true;
    }

    return main.subList(0, sub.size()).equals(sub);
  }

  private LintUtility() {}

  /**
   * Gets a lengthy description of the linter. By default, reads a resource file called
   * /help/{linter-id}.txt and if that is not present, returns the summary. Can be overridden.
   *
   * @return Lengthy description of the linter
   */
  public static String readDescription(final String linterId) {
    final String descriptionResource = String.format("/help/%s.txt", linterId);

    final String descriptionText;
    if (BaseLinterProvider.class.getResource(descriptionResource) == null) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Could not find description resource for linter {0}, at {1}", linterId));
      return "";
    }
    descriptionText = readResourceFully(descriptionResource);
    return descriptionText;
  }
}
