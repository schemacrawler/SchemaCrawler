/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
