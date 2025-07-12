/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.List;

/**
 * Represents a column type. Provides the java.sql.Types type, the java.sql.Types type name, and the
 * database specific data type name.
 */
public interface ColumnDataType extends DatabaseObject, TypedObject<DataTypeType> {

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
  default String getDatabaseSpecificTypeName() {
    return getName();
  }

  /**
   * Gets the database specific data type name.
   *
   * @return Database specific data type name
   */
  default String getStandardTypeName() {
    return getJavaSqlType().getName();
  }

  /**
   * Get list of enum values if the data type is enumerated.
   *
   * @return List of enum values
   */
  List<String> getEnumValues();

  /**
   * Gets the java.sql.Types type.
   *
   * @return java.sql.Types type
   */
  JavaSqlType getJavaSqlType();

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
   * Gets the Java class mapped to the type.
   *
   * @return The Java class mapped to the type
   */
  Class<?> getTypeMappedClass();

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

  /**
   * Whether the data type is enumerated.
   *
   * @return Whether the data type is enumerated
   */
  boolean isEnumerated();

  /**
   * Whether the data type has a fixed precision scale.
   *
   * @return Whether the data type has a fixed precision scale
   */
  boolean isFixedPrecisionScale();

  /**
   * Whether the data type is nullable.
   *
   * @return Whether the data type is nullable
   */
  boolean isNullable();

  /**
   * Whether the data type is unsigned.
   *
   * @return Whether the data type is unsigned
   */
  boolean isUnsigned();
}
