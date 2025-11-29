/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

    childTableFilterDepth = options.childTableFilterDepth();
    parentTableFilterDepth = options.parentTableFilterDepth();

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
