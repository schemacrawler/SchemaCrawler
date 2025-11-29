/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/** Column for tables and routines. */
public interface BaseColumn<D extends DatabaseObject>
    extends DependantObject<D>, TypedObject<ColumnDataType> {

  /**
   * Gets the column data type.
   *
   * @return Column data type
   */
  ColumnDataType getColumnDataType();

  /**
   * Get the number of fractional digits.
   *
   * @return Number of fractional digits
   */
  int getDecimalDigits();

  /**
   * Get the ordinal position of the column in the table, starting at 1.
   *
   * @return Ordinal position of the column in the table, starting at 1
   */
  int getOrdinalPosition();

  /**
   * Get the column size. For char or date types this is the maximum number of characters, for
   * numeric or decimal types this is precision.
   *
   * @return Column size
   */
  int getSize();

  /**
   * Gets the width of the column, if the column width is required. (Column width is not significant
   * for column types such as TIME and DATE.)
   *
   * @return Column width as a String
   */
  String getWidth();

  /**
   * Whether the column data type is known or not.
   *
   * @return True if the column data type is known
   */
  boolean isColumnDataTypeKnown();

  /**
   * Whether the column is nullable.
   *
   * @return Whether the column is nullable
   */
  boolean isNullable();
}
