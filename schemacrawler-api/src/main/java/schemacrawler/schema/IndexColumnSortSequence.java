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

package schemacrawler.schema;

import java.util.logging.Level;

import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

/** An enumeration wrapper around index sort sequences. */
public enum IndexColumnSortSequence {

  /** Unknown */
  unknown("unknown"),
  /** Ascending. */
  ascending("A"),
  /** Descending. */
  descending("D");

  private static final Logger LOGGER = Logger.getLogger(IndexColumnSortSequence.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   *
   * @param code Sort sequence code.
   * @return Enumeration value
   */
  public static IndexColumnSortSequence valueOfFromCode(final String code) {
    for (final IndexColumnSortSequence type : IndexColumnSortSequence.values()) {
      if (type.getCode().equalsIgnoreCase(code)) {
        return type;
      }
    }
    LOGGER.log(Level.FINE, new StringFormat("Unknown code <%s>", code));
    return unknown;
  }

  private final String code;

  IndexColumnSortSequence(final String code) {
    this.code = code;
  }

  /**
   * Index sort sequence code.
   *
   * @return Index sort sequence code
   */
  String getCode() {
    return code;
  }
}
