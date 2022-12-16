/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
