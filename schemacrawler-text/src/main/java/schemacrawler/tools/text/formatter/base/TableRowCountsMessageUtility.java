/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.base;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Table;
import schemacrawler.utility.TableRowCountsUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class TableRowCountsMessageUtility {

  /**
   * Message format for the counts.
   *
   * @param number Number value in the message
   * @return Message format for the counts
   */
  public static String getRowCountMessage(final Number number) {
    requireNonNull(number, "No number provided");
    final long longValue = number.longValue();
    if (longValue <= 0) {
      return "empty";
    }
    return "%,d rows".formatted(longValue);
  }

  public static String getRowCountMessage(final Table table) {
    return getRowCountMessage(TableRowCountsUtility.getRowCount(table));
  }

  private TableRowCountsMessageUtility() {
    // Prevent instantiation
  }
}
