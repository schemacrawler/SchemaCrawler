/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import java.util.Objects;
import schemacrawler.schema.CrawlInfo;

public final class CrawlInfoSupport {

  private final CrawlInfo crawlInfo;

  public CrawlInfoSupport(final CrawlInfo crawlInfo) {
    this.crawlInfo = Objects.requireNonNull(crawlInfo, "No crawl info provided");
  }

  public String databaseVersion() {
    return crawlInfo.getDatabaseVersion().toString().replace("\"", "");
  }

  public String schemacrawlerVersion() {
    return crawlInfo.getSchemaCrawlerVersion().toString();
  }

  public String timestamp() {
    return crawlInfo.getCrawlTimestamp();
  }
}
