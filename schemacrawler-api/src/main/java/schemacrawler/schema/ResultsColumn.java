/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/** Represents a column in a result set. */
public interface ResultsColumn extends BaseColumn<Table> {

  /**
   * Gets the normal maximum number of characters allowed as the width of the designated column.
   *
   * @return The column's normal maximum width in characters
   */
  int getDisplaySize();

  /**
   * Gets the column's suggested title for use in printouts and displays.
   *
   * @return Suggested column title
   */
  String getLabel();

  /**
   * True if this column is auto-incremented.
   *
   * @return If the column is auto-incremented
   */
  boolean isAutoIncrement();

  /**
   * True if this column is case-sensitive.
   *
   * @return If the column is case-sensitive
   */
  boolean isCaseSensitive();

  /**
   * True if this column is a cash value.
   *
   * @return If the column is a cash value
   */
  boolean isCurrency();

  /**
   * Indicates whether a write on the designated column will definitely succeed.
   *
   * @return Whether a write on the designated column will definitely succeed
   */
  boolean isDefinitelyWritable();

  /**
   * Indicates whether the designated column is definitely not writable.
   *
   * @return Whether the designated column is definitely not writable
   */
  boolean isReadOnly();

  /**
   * Indicates whether the designated column can be used in a where clause.
   *
   * @return Whether the designated column can be used in a where clause
   */
  boolean isSearchable();

  /**
   * Indicates whether values in the designated column are signed numbers.
   *
   * @return Whether values in the designated column are signed numbers
   */
  boolean isSigned();

  /**
   * Indicates whether it is possible for a write on the designated column to succeed.
   *
   * @return Whether it is possible for a write on the designated column to succeed
   */
  boolean isWritable();
}
