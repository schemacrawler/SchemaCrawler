/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schema;


/**
 * A column type. Provide the java.sql.Types type, the java.sql.Types
 * type name, and the database specific data type name.
 */
public interface ColumnDataType
  extends NamedObject
{

  /**
   * The base type of the data type.
   * 
   * @return Base type
   */
  ColumnDataType getBaseType();

  /**
   * Parameters needed when using this data type.
   * 
   * @return Parameters needed when using this data type
   */
  String getCreateParameters();

  /**
   * The database specific data type name.
   * 
   * @return Database specific data type name
   */
  String getDatabaseSpecificTypeName();

  /**
   * Literal prefix.
   * 
   * @return Literal prefix
   */
  String getLiteralPrefix();

  /**
   * Literal suffix.
   * 
   * @return Literal suffix
   */
  String getLiteralSuffix();

  /**
   * Local data type name.
   * 
   * @return Local data type name
   */
  String getLocalTypeName();

  /**
   * Maximum scale.
   * 
   * @return Maximum scale
   */
  int getMaximumScale();

  /**
   * Minimum scale.
   * 
   * @return Minimum scale
   */
  int getMinimumScale();

  /**
   * Precision of the radix.
   * 
   * @return Precision of the radix
   */
  int getNumPrecisionRadix();

  /**
   * Precision.
   * 
   * @return Precision
   */
  long getPrecision();

  /**
   * Search method.
   * 
   * @return Search method
   */
  SearchableType getSearchable();

  /**
   * The java.sql.Types type.
   * 
   * @return java.sql.Types type
   */
  int getType();

  /**
   * Get the Java class for the type.
   * 
   * @return The Java class for the type
   */
  String getTypeClassName();

  /**
   * The java.sql.Types type name.
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
