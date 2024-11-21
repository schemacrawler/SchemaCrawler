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
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class LintUtility {

  public static final Logger LOGGER = Logger.getLogger(LintUtility.class.getName());

  // This comparator is not compatible with the equals logic
  // Comparison may be expensive, since it converts values to strings
  public static final Comparator<Lint<? extends Serializable>> LINT_COMPARATOR =
      (lint1, lint2) -> {
        if (lint1 == null) {
          return 1;
        }
        if (lint2 == null) {
          return -1;
        }

        int compareTo;
        compareTo = lint1.getObjectType().compareTo(lint2.getObjectType());
        if (compareTo != 0) {
          return compareTo;
        }
        compareTo = lint1.getObjectName().compareTo(lint2.getObjectName());
        if (compareTo != 0) {
          return compareTo;
        }
        compareTo = lint1.getSeverity().compareTo(lint2.getSeverity());
        compareTo *= -1; // Reverse
        if (compareTo != 0) {
          return compareTo;
        }
        compareTo = lint1.getLinterId().compareTo(lint2.getLinterId());
        if (compareTo != 0) {
          return compareTo;
        }
        compareTo = lint1.getMessage().compareTo(lint2.getMessage());
        if (compareTo != 0) {
          return compareTo;
        }
        compareTo = lint1.getValueAsString().compareTo(lint2.getValueAsString());
        return compareTo;
      };

  public static final Comparator<Linter> LINTER_COMPARATOR =
      (linter1, linter2) -> {
        if (linter1 == null) {
          return -1;
        }

        if (linter2 == null) {
          return 1;
        }

        int comparison = 0;

        if (comparison == 0) {
          comparison = linter1.getSeverity().compareTo(linter2.getSeverity());
        }

        if (comparison == 0) {
          comparison = linter1.getLintCount() - linter2.getLintCount();
        }

        if (comparison == 0) {
          comparison = linter1.getLinterId().compareTo(linter2.getLinterId());
        }

        return comparison;
      };

  public static <E> boolean listStartsWith(final List<E> main, final List<E> sub) {
    if (main == null || sub == null || (main.size() < sub.size())) {
      return false;
    }
    if (main.isEmpty()) {
      return true;
    }

    return main.subList(0, sub.size()).equals(sub);
  }

  /**
   * Gets a lengthy description of the linter. By default, reads a resource file called
   * /help/{linter-id}.txt and if that is not present, returns the summary. Can be overridden.
   *
   * @return Lengthy description of the linter
   */
  public static String readDescription(final String linterId) {
    final String descriptionResource = String.format("/help/%s.txt", linterId);
    final String descriptionText = readResourceFully(descriptionResource);
    return descriptionText;
  }

  private LintUtility() {}
}
