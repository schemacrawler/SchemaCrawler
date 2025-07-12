/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

/** SchemaCrawler options builder, to build the immutable options to crawl a schema. */
public final class SchemaCrawlerOptionsBuilder {

  public static SchemaCrawlerOptions newSchemaCrawlerOptions() {
    final LimitOptions limitOptions = LimitOptionsBuilder.newLimitOptions();
    final FilterOptions filterOptions = FilterOptionsBuilder.newFilterOptions();
    final GrepOptions grepOptions = GrepOptionsBuilder.newGrepOptions();
    final LoadOptions loadOptions = LoadOptionsBuilder.newLoadOptions();
    return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
  }

  private SchemaCrawlerOptionsBuilder() {
    throw new UnsupportedOperationException();
  }
}
