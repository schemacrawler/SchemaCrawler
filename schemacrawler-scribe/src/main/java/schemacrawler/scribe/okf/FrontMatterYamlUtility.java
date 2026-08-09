/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static java.util.Objects.requireNonNull;
import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import schemacrawler.scribe.okf.frontmatter.github.GitHubPagesFrontMatter;
import schemacrawler.scribe.okf.frontmatter.okf.OkfFrontMatter;
import schemacrawler.scribe.okf.frontmatter.schemacrawler.SchemaCrawlerFrontMatter;

final class FrontMatterYamlUtility {

  @SuppressWarnings("unchecked")
  private static void mergeInto(
      final Map<String, Object> target, final Map<String, Object> source) {
    for (final Map.Entry<String, Object> entry : source.entrySet()) {
      final Object normalizedValue = normalize(entry.getValue());
      if (normalizedValue == null) {
        continue;
      }

      final String key = entry.getKey();
      final Object existingValue = target.get(key);
      if (existingValue instanceof final Map<?, ?> existingMap
          && normalizedValue instanceof final Map<?, ?> incomingMap) {
        final LinkedHashMap<String, Object> mergedMap = new LinkedHashMap<>();
        mergeInto(mergedMap, (Map<String, Object>) existingMap);
        mergeInto(mergedMap, (Map<String, Object>) incomingMap);
        if (!mergedMap.isEmpty()) {
          target.put(key, mergedMap);
        }
      } else {
        target.put(key, normalizedValue);
      }
    }
  }

  private static Object normalize(final Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof final String stringValue) {
      return stringValue.isBlank() ? null : stringValue;
    }
    if (value instanceof final Map<?, ?> mapValue) {
      final LinkedHashMap<String, Object> normalizedMap = new LinkedHashMap<>();
      for (final Map.Entry<?, ?> entry : mapValue.entrySet()) {
        if (entry.getKey() == null) {
          continue;
        }
        final Object normalizedEntryValue = normalize(entry.getValue());
        if (normalizedEntryValue != null) {
          normalizedMap.put(String.valueOf(entry.getKey()), normalizedEntryValue);
        }
      }
      return normalizedMap.isEmpty() ? null : normalizedMap;
    }
    if (value instanceof final Iterable<?> iterableValue) {
      final List<Object> normalizedValues = new java.util.ArrayList<>();
      for (final Object item : iterableValue) {
        final Object normalizedItem = normalize(item);
        if (normalizedItem != null) {
          normalizedValues.add(normalizedItem);
        }
      }
      return normalizedValues.isEmpty() ? null : normalizedValues;
    }
    return value;
  }

  private static LinkedHashMap<String, Object> toMap(final Object value) {
    return yamlMapper.convertValue(value, new tools.jackson.core.type.TypeReference<>() {});
  }

  List<String> toTags(final Object value) {
    requireNonNull(value, "No value provided");

    final List<String> tags = new ArrayList<>();
    final LinkedHashMap<String, Object> map = toMap(value);
    for (final Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof final Boolean booleanValue && booleanValue) {
        tags.add(toSnakeCase(entry.getKey()));
      }
    }
    return List.copyOf(tags);
  }

  String toYamlString(
      final OkfFrontMatter okfFrontMatter,
      final GitHubPagesFrontMatter gitHubPagesFrontMatter,
      final SchemaCrawlerFrontMatter schemaCrawlerFrontMatter) {
    requireNonNull(okfFrontMatter, "No OKF front-matter provided");
    requireNonNull(gitHubPagesFrontMatter, "No GitHub front-matter provided");

    final LinkedHashMap<String, Object> frontMatter = new LinkedHashMap<>();

    mergeInto(frontMatter, toMap(okfFrontMatter));
    mergeInto(frontMatter, toMap(gitHubPagesFrontMatter));
    if (schemaCrawlerFrontMatter != null) {
      mergeInto(frontMatter, toMap(schemaCrawlerFrontMatter));
    }

    return yamlMapper.writeValueAsString(frontMatter);
  }
}
