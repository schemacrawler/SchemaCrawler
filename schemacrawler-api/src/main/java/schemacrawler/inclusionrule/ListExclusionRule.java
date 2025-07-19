/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.inclusionrule;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.string.StringFormat;

public class ListExclusionRule implements InclusionRule {

  private static final long serialVersionUID = -6315037625922693976L;

  private static final Logger LOGGER = Logger.getLogger(ListExclusionRule.class.getName());

  private final List<String> exclusions;

  public ListExclusionRule(final List<String> exclusions) {
    if (exclusions == null || exclusions.isEmpty()) {
      throw new IllegalArgumentException("No exclusions provided");
    }
    this.exclusions = exclusions;
  }

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {

    if (isBlank(text)) {
      // Exclude blanks
      return false;
    }

    if (exclusions.contains(text)) {
      LOGGER.log(
          Level.FINE, new StringFormat("Excluding <%s> since it is on the exclude list", text));
      return false;
    }
    LOGGER.log(Level.FINE, new StringFormat("Including <%s>", text));
    return true;
  }
}
