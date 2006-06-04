/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
 * A column type. Provide the java.sql.Types type, the java.sql.Types type name,
 * and the database specific data type name.
 */
public interface ColumnDataType
  extends NamedObject
{

  /**
   * Whether the data type is user-defined.
   * 
   * @return Whether the data type is user-defined
   */
  boolean isUserDefined();

  /**
   * The java.sql.Types type.
   * 
   * @return java.sql.Types type
   */
  int getType();

  /**
   * The java.sql.Types type name.
   * 
   * @return java.sql.Types type name
   */
  String getTypeName();

  /**
   * The database specific data type name.
   * 
   * @return Database specific data type name
   */
  String getDatabaseSpecificTypeName();

  /**
   * Whether the data type is auto-incrementable.
   * 
   * @return Whether the data type is auto-incrementable
   */
  boolean isAutoIncrementable();

  /**
   * Whether the data type is case-sensitive.
   * 
   * @return Whether the data type is case-sensitive
   */
  boolean isCaseSensitive();

  String getCreateParameters();

  /**
   * Whether the data type has a fixed precision scale.
   * 
   * @return Whether the data type has a fixed precision scale
   */
  boolean isFixedPrecisionScale();

  String getLiteralPrefix();

  String getLiteralSuffix();

  String getLocalTypeName();

  int getMaximumScale();

  int getMinimumScale();

  /**
   * Whether the data type is nullable.
   * 
   * @return Whether the data type is nullable
   */
  boolean isNullable();

  int getNumPrecisionRadix();

  int getPrecision();

  SearchableType getSearchable();

  boolean isUnsigned();

  boolean isRealType();

  boolean isBinaryType();

  boolean isIntegralType();

  boolean isDateType();

}
