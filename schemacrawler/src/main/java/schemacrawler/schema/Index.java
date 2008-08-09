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
 * Represents an index on a database table.
 * 
 * @author Sualeh Fatehi
 */
public interface Index
  extends DependantObject
{

  /**
   * Gets the cardinality. When the index type is statistic, then this
   * is the number of rows in the table; otherwise, it is the number of
   * unique values in the index.
   * 
   * @return Cardinality
   */
  int getCardinality();

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the table.
   */
  Column[] getColumns();

  /**
   * Gets the pages. When the index type is statistic, then this is the
   * number of pages used for the table, otherwise it is the number of
   * pages used for the current index.
   * 
   * @return Pages
   */
  int getPages();

  /**
   * Gets the sort sequence.
   * 
   * @return Sort sequence
   */
  IndexSortSequence getSortSequence();

  /**
   * Gets the index type.
   * 
   * @return Index type
   */
  IndexType getType();

  /**
   * If the index is unique.
   * 
   * @return If the index is unique
   */
  boolean isUnique();

}
