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
public interface Column
  extends BaseColumn
{

  /**
   * Getter for property default value.
   * 
   * @return Value of property default value.
   */
  String getDefaultValue();

  /**
   * List of privileges.
   * 
   * @return Privileges for the table.
   */
  Privilege[] getPrivileges();

  /**
   * Referenced column if this column is part of a foreign key, null
   * otherwise.
   * 
   * @return Referenced column.
   */
  Column getReferencedColumn();

  /**
   * True if this column is part of a foreign key.
   * 
   * @return If the column is part of a foreign key.
   */
  boolean isPartOfForeignKey();

  /**
   * True if this column is a part of primary key.
   * 
   * @return If the column is a part of primary key
   */
  boolean isPartOfPrimaryKey();

  /**
   * True if this column is a unique index.
   * 
   * @return If the column is a unique index
   */
  boolean isPartOfUniqueIndex();

}
