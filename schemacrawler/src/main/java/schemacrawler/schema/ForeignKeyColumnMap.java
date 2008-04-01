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
 * Represents a single column mapping from a primary key column to a
 * foreign key column.
 * 
 * @author Sualeh Fatehi
 */
public interface ForeignKeyColumnMap
  extends DependantNamedObject
{

  /**
   * Gets the foreign key column.
   * 
   * @return Foreign key column
   */
  Column getForeignKeyColumn();

  /**
   * Gets the sequence in the foreign key.
   * 
   * @return Foreign key sequence
   */
  int getKeySequence();

  /**
   * Gets the primary key column.
   * 
   * @return Primary key column
   */
  Column getPrimaryKeyColumn();

}
