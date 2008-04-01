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
 * Represents a database procedure.
 * 
 * @author Sualeh Fatehi
 */
public interface Procedure
  extends DatabaseObject
{

  /**
   * Gets a column by name.
   * 
   * @param name
   *        Name
   * @return Column of the procedure
   */
  ProcedureColumn getColumn(String name);

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the procedure
   */
  ProcedureColumn[] getColumns();

  /**
   * Gets the definition.
   * 
   * @return Definition
   */
  String getDefinition();

  /**
   * Gets the type of the routine body.
   * 
   * @return Routine body type
   */
  RoutineBodyType getRoutineBodyType();

  /**
   * Gets the procedure type.
   * 
   * @return Procedure type
   */
  ProcedureType getType();

}
