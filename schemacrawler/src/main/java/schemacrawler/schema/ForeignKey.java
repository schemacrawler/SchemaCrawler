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
 * Represents a foreign key in a table.
 * 
 * @author Sualeh Fatehi
 */
public interface ForeignKey
  extends DatabaseObject
{

  /**
   * List of column pairs.
   * 
   * @return Column pairs
   */
  ForeignKeyColumnMap[] getColumnPairs();

  /**
   * Deferrability.
   * 
   * @return Deferrability
   */
  ForeignKeyDeferrability getDeferrability();

  /**
   * Delete rule.
   * 
   * @return Delete rule
   */
  ForeignKeyUpdateRule getDeleteRule();

  /**
   * Update rule.
   * 
   * @return Update rule
   */
  ForeignKeyUpdateRule getUpdateRule();

}
