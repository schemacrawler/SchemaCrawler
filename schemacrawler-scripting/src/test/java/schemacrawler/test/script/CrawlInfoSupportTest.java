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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.tools.command.script.CrawlInfoSupport;
import us.fatehi.utility.property.BaseProductVersion;

public class CrawlInfoSupportTest {

  private CrawlInfo crawlInfo;

  @Test
  public void constructorRejectsNullCrawlInfo() {
    assertThrows(NullPointerException.class, () -> new CrawlInfoSupport(null));
  }

  @Test
  public void databaseVersion() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.databaseVersion(), is("TestDB 2.0"));
  }

  @Test
  public void databaseVersionStripsQuotes() {
    when(crawlInfo.getDatabaseVersion())
        .thenReturn(new BaseProductVersion("\"Quoted DB\"", "\"3.0\""));
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.databaseVersion(), is("Quoted DB 3.0"));
  }

  @Test
  public void schemacrawlerVersion() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.schemacrawlerVersion(), is("SchemaCrawler 17.10.1"));
  }

  @BeforeEach
  public void setUp() {
    crawlInfo = mock(CrawlInfo.class);
    when(crawlInfo.getDatabaseVersion()).thenReturn(new BaseProductVersion("TestDB", "2.0"));
    when(crawlInfo.getSchemaCrawlerVersion())
        .thenReturn(new BaseProductVersion("SchemaCrawler", "17.10.1"));
    when(crawlInfo.getCrawlTimestamp()).thenReturn("2026-04-18 21:00:00");
  }

  @Test
  public void timestamp() {
    final CrawlInfoSupport support = new CrawlInfoSupport(crawlInfo);
    assertThat(support.timestamp(), is("2026-04-18 21:00:00"));
  }
}
