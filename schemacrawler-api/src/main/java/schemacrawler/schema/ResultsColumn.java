/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
 * Represents a column in a result set.
 * 
 * @author Sualeh Fatehi
 */
public interface ResultsColumn
  extends BaseColumn
{

  /**
   * Gets the normal maximum number of characters allowed as the width
   * of the designated column.
   * 
   * @return The column's normal maximum width in characters
   */
  int getDisplaySize();

  /**
   * Gets the column's suggested title for use in printouts and
   * displays.
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
   * Indicates whether a write on the designated column will definitely
   * succeed.
   * 
   * @return Whether a write on the designated column will definitely
   *         succeed
   */
  boolean isDefinitelyWritable();

  /**
   * Indicates whether the designated column is definitely not writable.
   * 
   * @return Whether the designated column is definitely not writable
   */
  boolean isReadOnly();

  /**
   * Indicates whether the designated column can be used in a where
   * clause.
   * 
   * @return Whether the designated column can be used in a where clause
   */
  boolean isSearchable();

  /**
   * Indicates whether values in the designated column are signed
   * numbers.
   * 
   * @return Whether values in the designated column are signed numbers
   */
  boolean isSigned();

  /**
   * Indicates whether it is possible for a write on the designated
   * column to succeed.
   * 
   * @return Whether it is possible for a write on the designated column
   *         to succeed
   */
  boolean isWritable();

}
