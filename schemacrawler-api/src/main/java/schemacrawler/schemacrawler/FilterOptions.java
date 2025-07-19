/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import us.fatehi.utility.ObjectToString;

public final class FilterOptions implements Options {

  private final int childTableFilterDepth;
  private final int parentTableFilterDepth;

  FilterOptions(final int childTableFilterDepth, final int parentTableFilterDepth) {

    if (childTableFilterDepth < 0) {
      throw new IllegalArgumentException(
          "Invalid child table filter depth, " + childTableFilterDepth);
    }
    this.childTableFilterDepth = childTableFilterDepth;

    if (parentTableFilterDepth < 0) {
      throw new IllegalArgumentException(
          "Invalid parent table filter depth, " + parentTableFilterDepth);
    }
    this.parentTableFilterDepth = parentTableFilterDepth;
  }

  public int getChildTableFilterDepth() {
    return childTableFilterDepth;
  }

  public int getParentTableFilterDepth() {
    return parentTableFilterDepth;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
