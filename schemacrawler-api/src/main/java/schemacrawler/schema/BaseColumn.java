/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schema;


/**
 * Column for tables and routines.
 * 
 * @author Sualeh Fatehi
 */
public interface BaseColumn<P extends DatabaseObject>
  extends DependantObject<P>, TypedObject<ColumnDataType>
{

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
   * Get the column size. For char or date types this is the maximum
   * number of characters, for numeric or decimal types this is
   * precision.
   * 
   * @return Column size
   */
  int getSize();

  /**
   * Gets the width of the column, if the column width is required.
   * (Column width is not significant for column types such as TIME and
   * DATE.)
   * 
   * @return Column width as a String
   */
  String getWidth();

  /**
   * Whether the column is nullable.
   * 
   * @return Whether the column is nullable
   */
  boolean isNullable();

}
