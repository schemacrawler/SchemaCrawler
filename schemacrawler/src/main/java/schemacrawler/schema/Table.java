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
 * Represents a table in the database.
 * 
 * @author Sualeh Fatehi
 */
public interface Table
  extends DatabaseObject
{

  /**
   * List of check constraints.
   * 
   * @return Check constraints for the table.
   */
  CheckConstraint[] getCheckConstraints();

  /**
   * Gets a column by name.
   * 
   * @param name
   *        Name
   * @return Column.
   */
  Column getColumn(String name);

  /**
   * List of columns in ordinal order.
   * 
   * @return Columns of the table.
   */
  Column[] getColumns();

  /**
   * Gets a comma-separated list of columns.
   * 
   * @return Comma-separated list of columns
   */
  String getColumnsListAsString();

  /**
   * Gets a foreign key by name.
   * 
   * @param name
   *        Name
   * @return ForeignKey.
   */
  ForeignKey getForeignKey(String name);

  /**
   * List of foreign keys.
   * 
   * @return Foreign keys of the table.
   */
  ForeignKey[] getForeignKeys();

  /**
   * Gets an index by name.
   * 
   * @param name
   *        Name
   * @return Index.
   */
  Index getIndex(String name);

  /**
   * List of indices.
   * 
   * @return Indices of the table.
   */
  Index[] getIndices();

  /**
   * Primary key.
   * 
   * @return Primary key
   */
  PrimaryKey getPrimaryKey();

  /**
   * List of privileges.
   * 
   * @return Privileges for the table.
   */
  Privilege[] getPrivileges();

  /**
   * Gets a trigger by name.
   * 
   * @param name
   *        Name
   * @return Trigger.
   */
  Trigger getTrigger(String name);

  /**
   * List of triggers.
   * 
   * @return Triggers for the table.
   */
  Trigger[] getTriggers();

  /**
   * Table type.
   * 
   * @return Table type.
   */
  TableType getType();

}
