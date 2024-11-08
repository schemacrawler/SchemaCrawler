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

package schemacrawler.schemacrawler;

/** SchemaCrawler filter options builder, to build the immutable options to crawl a schema. */
public final class FilterOptionsBuilder
    implements OptionsBuilder<FilterOptionsBuilder, FilterOptions> {

  public static FilterOptionsBuilder builder() {
    return new FilterOptionsBuilder();
  }

  public static FilterOptions newFilterOptions() {
    return builder().toOptions();
  }

  private int childTableFilterDepth;
  private int parentTableFilterDepth;

  /** Default options. */
  private FilterOptionsBuilder() {}

  public FilterOptionsBuilder childTableFilterDepth(final int childTableFilterDepth) {
    this.childTableFilterDepth = Math.max(childTableFilterDepth, 0);
    return this;
  }

  @Override
  public FilterOptionsBuilder fromOptions(final FilterOptions options) {
    if (options == null) {
      return this;
    }

    childTableFilterDepth = options.getChildTableFilterDepth();
    parentTableFilterDepth = options.getParentTableFilterDepth();

    return this;
  }

  public FilterOptionsBuilder parentTableFilterDepth(final int parentTableFilterDepth) {
    this.parentTableFilterDepth = Math.max(parentTableFilterDepth, 0);
    return this;
  }

  @Override
  public FilterOptions toOptions() {
    return new FilterOptions(childTableFilterDepth, parentTableFilterDepth);
  }
}
