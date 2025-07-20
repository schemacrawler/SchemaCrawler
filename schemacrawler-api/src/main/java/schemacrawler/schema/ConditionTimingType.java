/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.logging.Level;

import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

/** Condition timing type. */
public enum ConditionTimingType {

  /** Unknown */
  unknown("unknown"),
  /** Before */
  before("BEFORE"),
  /** Instead of */
  instead_of("INSTEAD OF"),
  /** After */
  after("AFTER");

  private static final Logger LOGGER = Logger.getLogger(ConditionTimingType.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   *
   * @param value Sort sequence code.
   * @return Enumeration value
   */
  public static ConditionTimingType valueOfFromValue(final String value) {
    for (final ConditionTimingType type : ConditionTimingType.values()) {
      if (type.getValue().equalsIgnoreCase(value)) {
        return type;
      }
    }
    LOGGER.log(Level.FINE, new StringFormat("Unknown value <%s>", value));
    return unknown;
  }

  private final String value;

  ConditionTimingType(final String value) {
    this.value = value;
  }

  /**
   * Gets the value.
   *
   * @return Value
   */
  public final String getValue() {
    return value;
  }
}
