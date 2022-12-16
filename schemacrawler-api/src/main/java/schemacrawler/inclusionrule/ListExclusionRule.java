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
