/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
        if (lint1 == lint2) {
          return 0;
        }
        if (lint1 == null) {
          return -1;
        }
        if (lint2 == null) {
          return 1;
        }

        int comparison = 0;
        if (comparison == 0) {
          comparison = lint1.getObjectType().compareTo(lint2.getObjectType());
        }
        if (comparison == 0) {
          comparison = lint1.getObjectName().compareTo(lint2.getObjectName());
        }
        if (comparison == 0) {
          comparison = lint1.getSeverity().compareTo(lint2.getSeverity());
          comparison *= -1; // Reverse
        }
        if (comparison == 0) {
          comparison = lint1.getLinterId().compareTo(lint2.getLinterId());
        }
        if (comparison == 0) {
          comparison = lint1.getMessage().compareTo(lint2.getMessage());
        }
        if (comparison == 0) {
          comparison = lint1.getValueAsString().compareTo(lint2.getValueAsString());
        }
        return comparison;
      };

  public static final Comparator<Linter> LINTER_COMPARATOR =
      (linter1, linter2) -> {
        if (linter1 == linter2) {
          return 0;
        }
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
    if (main == null || sub == null || main.size() < sub.size()) {
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
    final String descriptionResource = "/help/%s.txt".formatted(linterId);
    final String descriptionText = readResourceFully(descriptionResource);
    return descriptionText;
  }

  private LintUtility() {}
}
