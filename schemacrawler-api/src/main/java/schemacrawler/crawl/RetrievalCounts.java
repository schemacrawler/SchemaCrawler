package schemacrawler.crawl;

import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.requireNotBlank;

final class RetrievalCounts {

  private static final Logger LOGGER = Logger.getLogger(RetrievalCounts.class.getName());

  private final String name;
  private int count;
  private int includedCount;

  RetrievalCounts(final String name) {
    this.name = requireNotBlank(name, "No name provided");
    count = 0;
    includedCount = 0;
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

  void countIfIncluded(final boolean included) {
    if (included) {
      includedCount = includedCount + 1;
    }
  }

  void countIncluded() {
    includedCount = includedCount + 1;
  }

  void log() {
    log(Level.INFO);
  }

  void log(final Level level) {
    if (level == null) {
      return;
    }
    if (LOGGER.isLoggable(level)) {
      LOGGER.log(level, "Processed " + toString());
    }
  }
}
