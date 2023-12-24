/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
