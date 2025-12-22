/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.NamedObjectKey;
import us.fatehi.utility.InclusionCounts;

public final class RetrievalCounts extends InclusionCounts {

  private static class Counts {

    private final Map<NamedObjectKey, Integer> counts;

    public Counts() {
      counts = new HashMap<>();
    }

    int count(final NamedObjectKey key) {
      if (key == null) {
        return 0;
      }

      int count = 0;
      if (counts.containsKey(key)) {
        count = get(key);
      }
      count = count + 1;
      counts.put(key, count);
      return count;
    }

    int get(final NamedObjectKey key) {
      if (key == null) {
        return 0;
      }
      int count = 0;
      if (counts.containsKey(key)) {
        count = counts.get(key);
      }
      return count;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(RetrievalCounts.class.getName());

  private final Counts keyCount;
  private final Counts includedKeyCount;

  public RetrievalCounts(final String name) {
    super(name);
    keyCount = new Counts();
    includedKeyCount = new Counts();
  }

  public void count(final NamedObjectKey key) {
    if (key == null) {
      return;
    }

    keyCount.count(key);
    count();
  }

  void countIfIncluded(final NamedObjectKey key, final boolean included) {
    if (key == null) {
      return;
    }

    if (included) {
      includedKeyCount.count(key);
      countIncluded();
    }
  }

  void countIncluded(final NamedObjectKey key) {
    if (key == null) {
      return;
    }

    includedKeyCount.count(key);
    countIncluded();
  }

  public void log(final NamedObjectKey key) {
    if (key == null) {
      return;
    }

    final Level level = Level.INFO;
    if (LOGGER.isLoggable(level)) {}

    if (LOGGER.isLoggable(level)) {
      final int count = keyCount.get(key);
      final int includedCount = includedKeyCount.get(key);
      LOGGER.log(
          level,
          "Processed %d/%d %s %s"
              .formatted(includedCount, count, getName(), key == null ? "" : " for " + key));
    }
  }
}
