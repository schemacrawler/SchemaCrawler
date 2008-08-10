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
 * Represents a column type. Provides the java.sql.Types type, the
 * java.sql.Types type name, and the database specific data type name.
 * 
 * @author Sualeh Fatehi
 */
public interface ColumnDataType
  extends DatabaseObject
{

  /**
   * Gets the base type of the data type.
   * 
   * @return Base type
   */
  ColumnDataType getBaseType();

  /**
   * Gets the parameters needed when using this data type.
   * 
   * @return Parameters needed when using this data type
   */
  String getCreateParameters();

  /**
   * Gets the database specific data type name.
   * 
   * @return Database specific data type name
   */
  String getDatabaseSpecificTypeName();

  /**
   * Gets the literal prefix.
   * 
   * @return Literal prefix
   */
  String getLiteralPrefix();

  /**
   * Gets the literal suffix.
   * 
   * @return Literal suffix
   */
  String getLiteralSuffix();

  /**
   * Gets the local data type name.
   * 
   * @return Local data type name
   */
  String getLocalTypeName();

  /**
   * Gets the maximum scale.
   * 
   * @return Maximum scale
   */
  int getMaximumScale();

  /**
   * Gets the minimum scale.
   * 
   * @return Minimum scale
   */
  int getMinimumScale();

  /**
   * Gets the precision of the radix.
   * 
   * @return Precision of the radix
   */
  int getNumPrecisionRadix();

  /**
   * Gets the precision.
   * 
   * @return Precision
   */
  long getPrecision();

  /**
   * Gets the search method.
   * 
   * @return Search method
   */
  SearchableType getSearchable();

  /**
   * Gets the java.sql.Types type.
   * 
   * @return java.sql.Types type
   */
  int getType();

  /**
   * Gets the Java class for the type.
   * 
   * @return The Java class for the type
   */
  String getTypeClassName();

  /**
   * Gets the java.sql.Types type name.
   * 
   * @return java.sql.Types type name
   */
  String getTypeName();

  /**
   * Whether the data type is auto-incrementable.
   * 
   * @return Whether the data type is auto-incrementable
   */
  boolean isAutoIncrementable();

  /**
   * If the type is a binary type.
   * 
   * @return If the type is a binary type
   */
  boolean isBinaryType();

  /**
   * Whether the data type is case-sensitive.
   * 
   * @return Whether the data type is case-sensitive
   */
  boolean isCaseSensitive();

  /**
   * If the type is a character type.
   * 
   * @return If the type is a character type
   */
  boolean isCharacterType();

  /**
   * If the type is a date type.
   * 
   * @return If the type is a date type
   */
  boolean isDateType();

  /**
   * Whether the data type has a fixed precision scale.
   * 
   * @return Whether the data type has a fixed precision scale
   */
  boolean isFixedPrecisionScale();

  /**
   * If the type is an integer type.
   * 
   * @return If the type is an integer type
   */
  boolean isIntegralType();

  /**
   * Whether the data type is nullable.
   * 
   * @return Whether the data type is nullable
   */
  boolean isNullable();

  /**
   * If the type is a real number type.
   * 
   * @return If the type is a real number type
   */
  boolean isRealType();

  /**
   * Whether the data type is unsigned.
   * 
   * @return Whether the data type is unsigned
   */
  boolean isUnsigned();

  /**
   * Whether the data type is user-defined.
   * 
   * @return Whether the data type is user-defined
   */
  boolean isUserDefined();

}
