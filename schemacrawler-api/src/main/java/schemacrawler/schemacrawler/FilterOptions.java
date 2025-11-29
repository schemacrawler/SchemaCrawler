/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

/** Options to control filtering depth for related tables. */
public record FilterOptions(int childTableFilterDepth, int parentTableFilterDepth)
    implements Options {

  /**
   * Canonical constructor with validation.
   *
   * @param childTableFilterDepth depth for child tables; must be >= 0
   * @param parentTableFilterDepth depth for parent tables; must be >= 0
   */
  public FilterOptions {
    if (childTableFilterDepth < 0) {
      throw new IllegalArgumentException(
          "Invalid child table filter depth, " + childTableFilterDepth);
    }
    if (parentTableFilterDepth < 0) {
      throw new IllegalArgumentException(
          "Invalid parent table filter depth, " + parentTableFilterDepth);
    }
  }
}
