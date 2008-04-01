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


import java.io.Serializable;
import java.util.Map;

/**
 * Represents a named object.
 * 
 * @author Sualeh Fatehi
 */
public interface NamedObject
  extends Serializable, Comparable<NamedObject>
{

  /**
   * Adds attributes from a map.
   * 
   * @param values
   *        Attribute values to add.
   */
  void addAttributes(Map<String, Object> values);

  /**
   * Gets an attribute.
   * 
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  Object getAttribute(String name);

  /**
   * Getter for name of object.
   * 
   * @return Name of the object
   */
  String getName();

  /**
   * Getter for remarks.
   * 
   * @return Remarks
   */
  String getRemarks();

  /**
   * Sets an attribute.
   * 
   * @param name
   *        Attribute name
   * @param value
   *        Attribute value
   */
  void setAttribute(String name, Object value);

}
