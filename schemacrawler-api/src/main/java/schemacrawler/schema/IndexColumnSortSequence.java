/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
