/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schemacrawler.Version;
import schemacrawler.test.utility.crawl.LightCrawlInfo;
import schemacrawler.tools.command.script.CrawlInfoSupport;

public class CrawlInfoSupportTest {

  private CrawlInfo crawlInfo;

  @Test
  public void constructorRejectsNullCrawlInfo() {
    assertThrows(NullPointerException.class, () -> new CrawlInfoSupport(null));
  }

  @Test
  public void databaseVersion() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.databaseVersion(), is("TestDB v1.0"));
  }

  @Test
  public void databaseVersionStripsQuotes() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.databaseVersion(), is("TestDB v1.0"));
  }

  @Test
  public void schemacrawlerVersion() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.schemacrawlerVersion(), is(Version.version().toString()));
  }

  @BeforeEach
  public void setUp() {
    crawlInfo = new LightCrawlInfo();
  }

  @Test
  public void timestamp() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.timestamp(), is("1970-01-01 00:00:00"));
  }
}
