/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;

import us.fatehi.utility.ObjectToString;

/** SchemaCrawler options. */
public final class SchemaCrawlerOptions implements Options {

  private final LimitOptions limitOptions;
  private final FilterOptions filterOptions;
  private final GrepOptions grepOptions;
  private final LoadOptions loadOptions;

  public SchemaCrawlerOptions(
      final LimitOptions limitOptions,
      final FilterOptions filterOptions,
      final GrepOptions grepOptions,
      final LoadOptions loadOptions) {
    this.limitOptions = requireNonNull(limitOptions, "No limit options provided");
    this.filterOptions = requireNonNull(filterOptions, "No filter options provided");
    this.grepOptions = requireNonNull(grepOptions, "No grep options provided");
    this.loadOptions = requireNonNull(loadOptions, "No load options provided");
  }

  public FilterOptions getFilterOptions() {
    return filterOptions;
  }

  public GrepOptions getGrepOptions() {
    return grepOptions;
  }

  public LimitOptions getLimitOptions() {
    return limitOptions;
  }

  public LoadOptions getLoadOptions() {
    return loadOptions;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  public SchemaCrawlerOptions withFilterOptions(final FilterOptions filterOptions) {
    if (filterOptions == null) {
      return this;
    } else {
      return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
    }
  }

  public SchemaCrawlerOptions withGrepOptions(final GrepOptions grepOptions) {
    if (grepOptions == null) {
      return this;
    } else {
      return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
    }
  }

  public SchemaCrawlerOptions withLimitOptions(final LimitOptions limitOptions) {
    if (limitOptions == null) {
      return this;
    } else {
      return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
    }
  }

  public SchemaCrawlerOptions withLoadOptions(final LoadOptions loadOptions) {
    if (loadOptions == null) {
      return this;
    } else {
      return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
    }
  }
}
