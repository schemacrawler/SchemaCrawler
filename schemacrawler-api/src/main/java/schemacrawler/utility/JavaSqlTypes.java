/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import static us.fatehi.utility.Utility.isBlank;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.JavaSqlTypeGroup;

/** Utility to work with java.sql.Types. */
public final class JavaSqlTypes implements Iterable<JavaSqlType> {

  private final Map<Integer, JavaSqlType> javaSqlTypeMap;

  public JavaSqlTypes() {
    // Load default type mappings
    final TypeMap typeMap = new TypeMap();
    javaSqlTypeMap = new HashMap<>();
    for (final SQLType sqlType : JDBCType.values()) {
      final Integer sqlTypeInt = sqlType.getVendorTypeNumber();
      final JavaSqlTypeGroup sqlTypeGroup = JavaSqlTypeGroup.valueOf(sqlTypeInt);
      final Class<?> mappedClass = typeMap.get(sqlType.getName());
      final JavaSqlType javaSqlType = new JavaSqlType(sqlType, mappedClass, sqlTypeGroup);
      javaSqlTypeMap.put(sqlTypeInt, javaSqlType);
    }
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information, including the mapped Java
   * class.
   *
   * @param typeName java.sql.Types type name
   * @return JavaSqlType type
   */
  public JavaSqlType getFromJavaSqlTypeName(final String typeName) {
    JavaSqlType sqlDataType = JavaSqlType.UNKNOWN;
    if (isBlank(typeName)) {
      return sqlDataType;
    }

    for (final JavaSqlType javaSqlType : javaSqlTypeMap.values()) {
      if (typeName.equals(javaSqlType.getName())) {
        sqlDataType = javaSqlType;
        break;
      }
    }
    return sqlDataType;
  }

  @Override
  public Iterator<JavaSqlType> iterator() {
    return javaSqlTypeMap.values().iterator();
  }

  public int size() {
    return javaSqlTypeMap.size();
  }

  @Override
  public String toString() {
    return javaSqlTypeMap.toString();
  }

  public JavaSqlType valueOf(final int key) {
    if (javaSqlTypeMap.containsKey(key)) {
      return javaSqlTypeMap.get(key);
    } else {
      return JavaSqlType.UNKNOWN;
    }
  }
}
