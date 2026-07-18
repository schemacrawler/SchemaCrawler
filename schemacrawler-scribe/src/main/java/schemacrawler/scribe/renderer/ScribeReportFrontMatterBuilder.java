/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static schemacrawler.scribe.renderer.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;

final class ScribeReportFrontMatterBuilder {

  private ScribeReportFrontMatterBuilder() {}

  static String build(
      final String providedTitle,
      final String providedDescription,
      final Optional<Catalog> catalog) {
    final String title = isBlank(providedTitle) ? "Report" : providedTitle;
    final String description = isBlank(providedDescription) ? "Report" : providedDescription;

    final Map<String, Object> frontMatter = new LinkedHashMap<>();
    frontMatter.put("type", "report");
    frontMatter.put("title", title);
    frontMatter.put("description", description);

    if (catalog.isPresent()) {
      final CrawlInfo crawlInfo = catalog.get().getCrawlInfo();
      frontMatter.put("timestamp", crawlInfo.getCrawlTimestamp());
      frontMatter.put("run_id", crawlInfo.getRunId());
    }

    return mapper.writeValueAsString(frontMatter);
  }
}
