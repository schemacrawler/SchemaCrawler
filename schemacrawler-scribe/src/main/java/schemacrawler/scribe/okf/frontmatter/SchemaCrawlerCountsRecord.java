/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

public record SchemaCrawlerCountsRecord(
    Integer columnCount,
    Integer foreignKeyCount,
    Integer indexCount,
    Integer triggerCount,
    Long rowCount,
    Integer parameterCount) {

  public SchemaCrawlerCountsRecord {
    validateNonNegative(columnCount, "columnCount");
    validateNonNegative(foreignKeyCount, "foreignKeyCount");
    validateNonNegative(indexCount, "indexCount");
    validateNonNegative(triggerCount, "triggerCount");
    validateNonNegative(rowCount, "rowCount");
    validateNonNegative(parameterCount, "parameterCount");
  }

  private static void validateNonNegative(final Number value, final String fieldName) {
    if (value != null && value.longValue() < 0) {
      throw new IllegalArgumentException(fieldName + " should not be negative");
    }
  }
}
