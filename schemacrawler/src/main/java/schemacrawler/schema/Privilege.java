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
 * Represents a privilege of a table or column.
 * 
 * @author Sualeh Fatehi
 */
public interface Privilege
  extends DependantObject
{

  /**
   * Gets the grantee.
   * 
   * @return Grantee
   */
  String getGrantee();

  /**
   * Gets the grantor.
   * 
   * @return Grantor
   */
  String getGrantor();

  /**
   * If the privilege is grantable.
   * 
   * @return Is grantable
   */
  boolean isGrantable();

}
