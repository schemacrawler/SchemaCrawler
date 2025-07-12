/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.NamedObjectKey;

public final class RetrievalCounts {

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

  private final String name;
  private int count;
  private int includedCount;
  private final Counts keyCount;
  private final Counts includedKeyCount;

  RetrievalCounts(final String name) {
    this.name = requireNotBlank(name, "No name provided");
    count = 0;
    includedCount = 0;
    keyCount = new Counts();
    includedKeyCount = new Counts();
  }

  public int getCount() {
    return count;
  }

  public int getIncludedCount() {
    return includedCount;
  }

  @Override
  public String toString() {
    return String.format("%d/%d %s", includedCount, count, name);
  }

  void count() {
    count = count + 1;
  }

  void count(final NamedObjectKey key) {
    if (key == null) {
      return;
    }

    keyCount.count(key);
    count = count + 1;
  }

  void countIfIncluded(final boolean included) {
    if (included) {
      includedCount = includedCount + 1;
    }
  }

  void countIfIncluded(final NamedObjectKey key, final boolean included) {
    if (key == null) {
      return;
    }

    if (included) {
      includedKeyCount.count(key);
      includedCount = includedCount + 1;
    }
  }

  void countIncluded() {
    includedCount = includedCount + 1;
  }

  void countIncluded(final NamedObjectKey key) {
    if (key == null) {
      return;
    }

    includedKeyCount.count(key);
    includedCount = includedCount + 1;
  }

  void log() {
    log(Level.INFO, count, includedCount, null);
  }

  void log(final NamedObjectKey key) {
    if (key == null) {
      return;
    }
    log(Level.INFO, keyCount.get(key), includedKeyCount.get(key), key);
  }

  private void log(
      final Level level, final int count, final int includedCount, final NamedObjectKey key) {
    if (level == null) {
      return;
    }
    if (LOGGER.isLoggable(level)) {
      LOGGER.log(
          level,
          String.format(
              "Processed %d/%d %s %s",
              includedCount, count, name, key == null ? "" : " for " + key));
    }
  }
}
