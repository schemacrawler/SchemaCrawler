/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InclusionCounts {

  private static final Logger LOGGER = Logger.getLogger(InclusionCounts.class.getName());

  private final String name;
  private int count;
  private int includedCount;

  public InclusionCounts(final String name) {
    this.name = requireNotBlank(name, "No name provided");
    count = 0;
    includedCount = 0;
  }

  public void count() {
    count = count + 1;
  }

  public void countIfIncluded(final boolean included) {
    if (included) {
      includedCount = includedCount + 1;
    }
  }

  public void countIncluded() {
    includedCount = includedCount + 1;
  }

  public int getCount() {
    return count;
  }

  public int getIncludedCount() {
    return includedCount;
  }

  public String getName() {
    return name;
  }

  public void log() {
    final Level level = Level.INFO;
    if (LOGGER.isLoggable(level)) {
      LOGGER.log(level, "Processed %d/%d %s".formatted(includedCount, count, name));
    }
  }

  @Override
  public String toString() {
    return "%d/%d %s".formatted(includedCount, count, name);
  }
}
