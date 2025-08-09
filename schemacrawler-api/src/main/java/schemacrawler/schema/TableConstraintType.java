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

/** Table constraint type. */
public enum TableConstraintType {
  unknown("unknown"),
  // database schema metadata
  primary_key("PRIMARY KEY"),
  foreign_key("FOREIGN KEY"),
  unique("UNIQUE"),
  check("CHECK"),
  // user-supplied metadata
  alternate_key("ALTERNATE KEY"),
  weak_association("WEAK ASSOCIATION");

  private static final Logger LOGGER = Logger.getLogger(TableConstraintType.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   *
   * @param value Sort sequence code.
   * @return Enumeration value
   */
  public static TableConstraintType valueOfFromValue(final String value) {
    for (final TableConstraintType type : TableConstraintType.values()) {
      if (type.getValue().equalsIgnoreCase(value)) {
        return type;
      }
    }
    LOGGER.log(Level.FINE, new StringFormat("Unknown value <%s>", value));
    return unknown;
  }

  private final String value;

  TableConstraintType(final String value) {
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
