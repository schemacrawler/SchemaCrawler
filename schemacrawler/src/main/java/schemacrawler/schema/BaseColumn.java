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
 * Column for tables and procedures.
 * 
 * @author Sualeh Fatehi
 */
public interface BaseColumn
  extends DependantObject
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
   * Get the data type of column.
   * 
   * @return Column data type
   */
  ColumnDataType getType();

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
