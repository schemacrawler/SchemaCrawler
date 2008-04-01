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
 * Represents a JDBC driver property, and it's value.
 * 
 * @author sfatehi
 */
public interface JdbcDriverProperty
  extends NamedObject
{

  /**
   * Gets the array of possible values if the value for the field
   * <code>DriverPropertyInfo.value</code> may be selected from a
   * particular set of values.
   * 
   * @return Available choices for the value of a property
   */
  String[] getChoices();

  /**
   * Gets the description of the property.
   * 
   * @return Description
   */
  String getDescription();

  /**
   * Gets the the current value of the property, based on a combination
   * of the information supplied to the method
   * <code>getPropertyInfo</code>, the Java environment, and the
   * driver-supplied default values. This field may be null if no value
   * is known.
   * 
   * @return Value of the property
   */
  String getValue();

  /**
   * The <code>required</code> field is <code>true</code> if a value
   * must be supplied for this property during
   * <code>Driver.connect</code> and <code>false</code> otherwise.
   * 
   * @return Whether the property is required
   */
  boolean isRequired();

}
