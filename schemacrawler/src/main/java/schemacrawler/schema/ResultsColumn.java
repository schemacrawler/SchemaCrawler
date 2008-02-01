/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schema;


/**
 * A table or procedure column.
 * 
 * @author Sualeh Fatehi
 */
public interface ResultsColumn
  extends BaseColumn
{
  /**
   * The column's normal maximum width in characters.
   * 
   * @return Normal maximum number of characters allowed as the width of
   *         the designated column
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
   * @return Whether the designated column is definitely not writable.
   */
  boolean isReadOnly();

  /**
   * Indicates whether the designated column can be used in a where
   * clause.
   * 
   * @return Whether the designated column can be used in a where
   *         clause.
   */
  boolean isSearchable();

  /**
   * Indicates whether values in the designated column are signed
   * numbers.
   * 
   * @return Whether values in the designated column are signed numbers.
   */
  boolean isSigned();

  /**
   * Indicates whether it is possible for a write on the designated
   * column to succeed.
   * 
   * @return Whether it is possible for a write on the designated column
   *         to succeed.
   */
  boolean isWritable();

}
