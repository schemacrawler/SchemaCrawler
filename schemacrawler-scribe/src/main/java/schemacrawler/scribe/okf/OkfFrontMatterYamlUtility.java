/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

import schemacrawler.scribe.okf.frontmatter.GitHubPagesFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.OkfFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerFrontMatterRecord;

final class OkfFrontMatterYamlUtility {

  String toYaml(
      final OkfFrontMatterRecord okfFrontMatter,
      final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter,
      final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter) {
    requireNonNull(okfFrontMatter, "No OKF front-matter provided");
    requireNonNull(gitHubPagesFrontMatter, "No GitHub front-matter provided");

    final Map<String, Object> frontMatter = new LinkedHashMap<>();

    putIfPresent(frontMatter, "type", okfFrontMatter.type());
    putIfPresent(frontMatter, "title", okfFrontMatter.title());
    putIfPresent(frontMatter, "description", okfFrontMatter.description());
    putIfPresent(frontMatter, "resource", okfFrontMatter.resource());
    putIfPresent(frontMatter, "tags", okfFrontMatter.tags());
    putIfPresent(frontMatter, "generated", okfFrontMatter.generated());
    putIfPresent(frontMatter, "verified", okfFrontMatter.verified());
    putIfPresent(frontMatter, "status", okfFrontMatter.status());

    if (schemaCrawlerFrontMatter != null) {
      putIfPresent(frontMatter, "schema", schemaCrawlerFrontMatter.schema());
      putIfPresent(frontMatter, "name", schemaCrawlerFrontMatter.name());
      putIfPresent(frontMatter, "completeType", schemaCrawlerFrontMatter.completeType());
      putIfPresent(frontMatter, "counts", schemaCrawlerFrontMatter.counts());
      putIfPresent(frontMatter, "entityType", schemaCrawlerFrontMatter.entityType());
    }

    putIfPresent(frontMatter, "shortTitle", gitHubPagesFrontMatter.shortTitle());
    putIfPresent(frontMatter, "intro", gitHubPagesFrontMatter.intro());
    frontMatter.put("showMiniToc", gitHubPagesFrontMatter.showMiniToc());
    frontMatter.put(
        "allowTitleToDifferFromFilename", gitHubPagesFrontMatter.allowTitleToDifferFromFilename());

    return yamlMapper.writeValueAsString(frontMatter);
  }

  private static void putIfPresent(
      final Map<String, Object> frontMatter, final String key, final Object value) {
    if ((value == null) || (value instanceof final String stringValue && stringValue.isBlank())) {
      return;
    }
    if ((value instanceof final Iterable<?> iterableValue && !iterableValue.iterator().hasNext()) || (value instanceof final Map<?, ?> mapValue && mapValue.isEmpty())) {
      return;
    }
    frontMatter.put(key, value);
  }
}
