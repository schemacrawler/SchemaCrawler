/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.crawl;


import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.JavaSqlType.JavaSqlTypeGroup;

/**
 * Utility to work with java.sql.Types, and Java class name mappings.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypesUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(JavaSqlTypesUtility.class.getName());

  private static final Map<Integer, JavaSqlType> JAVA_SQL_TYPES_BY_TYPE;
  private static final Map<String, JavaSqlType> JAVA_SQL_TYPES_BY_TYPE_NAME;

  static
  {
    final List<JavaSqlType> javaSqlTypes = readJavaSqlTypes();
    JAVA_SQL_TYPES_BY_TYPE = mapJavaSqlTypesByType(javaSqlTypes);
    JAVA_SQL_TYPES_BY_TYPE_NAME = mapJavaSqlTypesByTypeName(javaSqlTypes);
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information,
   * including the mapped Java class.
   * 
   * @param type
   *        java.sql.Types type
   * @return JavaSqlType type
   */
  public static JavaSqlType lookupSqlDataType(final int type)
  {
    JavaSqlType sqlDataType = JAVA_SQL_TYPES_BY_TYPE.get(type);
    if (sqlDataType == null)
    {
      sqlDataType = JavaSqlType.UNKNOWN;
    }
    return sqlDataType;
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information,
   * including the mapped Java class.
   * 
   * @param typeName
   *        java.sql.Types type name
   * @return JavaSqlType type
   */
  public static JavaSqlType lookupSqlDataType(final String typeName)
  {
    JavaSqlType sqlDataType = JAVA_SQL_TYPES_BY_TYPE_NAME.get(typeName);
    if (sqlDataType == null)
    {
      sqlDataType = JavaSqlType.UNKNOWN;
    }
    return sqlDataType;
  }

  private static Map<Integer, JavaSqlType> mapJavaSqlTypesByType(final List<JavaSqlType> javaSqlTypes)
  {
    final Map<Integer, JavaSqlType> javaSqlTypesByTypeMap = new HashMap<>();
    if (javaSqlTypes != null)
    {
      for (final JavaSqlType javaSqlType: javaSqlTypes)
      {
        javaSqlTypesByTypeMap.put(javaSqlType.getJavaSqlType(), javaSqlType);
      }
    }
    return Collections.unmodifiableMap(javaSqlTypesByTypeMap);
  }

  private static Map<String, JavaSqlType> mapJavaSqlTypesByTypeName(final List<JavaSqlType> javaSqlTypes)
  {
    final Map<String, JavaSqlType> javaSqlTypesByTypeNameMap = new HashMap<>();
    if (javaSqlTypes != null)
    {
      for (final JavaSqlType javaSqlType: javaSqlTypes)
      {
        javaSqlTypesByTypeNameMap.put(javaSqlType.getJavaSqlTypeName(),
                                      javaSqlType);
      }
    }
    return Collections.unmodifiableMap(javaSqlTypesByTypeNameMap);
  }

  /**
   * Map java.sql.Types to Java classes. Since this information is not
   * available in the JDK, we need to hard-code it.
   * 
   * @return Map
   * @see <a
   *      href="http://docs.oracle.com/javase/6/docs/technotes/guides/jdbc/getstart/mapping.html#1051555">Mapping
   *      SQL and Java Types</a>
   */
  public static Map<String, Class<?>> getDefaultTypeMap()
  {
    return createDefaultTypeMap();
  }

  /**
   * Map java.sql.Types to Java classes. Since this information is not
   * available in the JDK, we need to hard-code it.
   * 
   * @return Map
   * @see <a
   *      href="http://docs.oracle.com/javase/6/docs/technotes/guides/jdbc/getstart/mapping.html#1051555">Mapping
   *      SQL and Java Types</a>
   */
  private static Map<String, Class<?>> createDefaultTypeMap()
  {
    final Map<String, Class<?>> defaultTypeMap = new TreeMap<>();

    // "Primitive" data type classes
    defaultTypeMap.put("BIGINT", Long.class);
    defaultTypeMap.put("BINARY", byte[].class);
    defaultTypeMap.put("BIT", Boolean.class);
    defaultTypeMap.put("BOOLEAN", Boolean.class);
    defaultTypeMap.put("CHAR", String.class);
    defaultTypeMap.put("DATALINK", java.net.URL.class);
    defaultTypeMap.put("DECIMAL", java.math.BigDecimal.class);
    defaultTypeMap.put("DISTINCT", Object.class);
    defaultTypeMap.put("DOUBLE", Double.class);
    defaultTypeMap.put("FLOAT", Double.class);
    defaultTypeMap.put("INTEGER", Integer.class);
    defaultTypeMap.put("JAVA_OBJECT", Object.class);
    defaultTypeMap.put("LONGNVARCHAR", String.class);
    defaultTypeMap.put("LONGVARBINARY", byte[].class);
    defaultTypeMap.put("LONGVARCHAR", String.class);
    defaultTypeMap.put("NCHAR", String.class);
    defaultTypeMap.put("NULL", Void.class);
    defaultTypeMap.put("NUMERIC", java.math.BigDecimal.class);
    defaultTypeMap.put("NVARCHAR", String.class);
    defaultTypeMap.put("OTHER", Object.class);
    defaultTypeMap.put("REAL", Float.class);
    defaultTypeMap.put("SMALLINT", Short.class);
    defaultTypeMap.put("TINYINT", byte.class);
    defaultTypeMap.put("VARBINARY", byte[].class);
    defaultTypeMap.put("VARCHAR", String.class);

    // SQL data type classes
    defaultTypeMap.put("ARRAY", java.sql.Array.class);
    defaultTypeMap.put("BLOB", java.sql.Blob.class);
    defaultTypeMap.put("CLOB", java.sql.Clob.class);
    defaultTypeMap.put("DATE", java.sql.Date.class);
    defaultTypeMap.put("NCLOB", java.sql.NClob.class);
    defaultTypeMap.put("REF", java.sql.Ref.class);
    defaultTypeMap.put("ROWID", java.sql.RowId.class);
    defaultTypeMap.put("SQLXML", java.sql.SQLXML.class);
    defaultTypeMap.put("STRUCT", java.sql.Struct.class);
    defaultTypeMap.put("TIME", java.sql.Time.class);
    defaultTypeMap.put("TIMESTAMP", java.sql.Timestamp.class);

    return Collections.unmodifiableMap(defaultTypeMap);
  }

  private static Map<String, JavaSqlTypeGroup> createJavaSqlTypesGroupsMap()
  {
    final Map<String, JavaSqlTypeGroup> javaSqlTypesGroupsMap = new HashMap<>();

    javaSqlTypesGroupsMap.put("ARRAY", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("BIGINT", JavaSqlTypeGroup.integer);
    javaSqlTypesGroupsMap.put("BINARY", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("BIT", JavaSqlTypeGroup.bit);
    javaSqlTypesGroupsMap.put("BLOB", JavaSqlTypeGroup.large_object);
    javaSqlTypesGroupsMap.put("BOOLEAN", JavaSqlTypeGroup.bit);
    javaSqlTypesGroupsMap.put("CHAR", JavaSqlTypeGroup.character);
    javaSqlTypesGroupsMap.put("CLOB", JavaSqlTypeGroup.large_object);
    javaSqlTypesGroupsMap.put("DATALINK", JavaSqlTypeGroup.url);
    javaSqlTypesGroupsMap.put("DATE", JavaSqlTypeGroup.temporal);
    javaSqlTypesGroupsMap.put("DECIMAL", JavaSqlTypeGroup.real);
    javaSqlTypesGroupsMap.put("DISTINCT", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("DOUBLE", JavaSqlTypeGroup.real);
    javaSqlTypesGroupsMap.put("FLOAT", JavaSqlTypeGroup.real);
    javaSqlTypesGroupsMap.put("INTEGER", JavaSqlTypeGroup.integer);
    javaSqlTypesGroupsMap.put("JAVA_OBJECT", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("LONGNVARCHAR", JavaSqlTypeGroup.character);
    javaSqlTypesGroupsMap.put("LONGVARBINARY", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("LONGVARCHAR", JavaSqlTypeGroup.character);
    javaSqlTypesGroupsMap.put("NCHAR", JavaSqlTypeGroup.character);
    javaSqlTypesGroupsMap.put("NCLOB", JavaSqlTypeGroup.large_object);
    javaSqlTypesGroupsMap.put("NUMERIC", JavaSqlTypeGroup.real);
    javaSqlTypesGroupsMap.put("NVARCHAR", JavaSqlTypeGroup.character);
    javaSqlTypesGroupsMap.put("OTHER", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("REAL", JavaSqlTypeGroup.real);
    javaSqlTypesGroupsMap.put("REF", JavaSqlTypeGroup.reference);
    javaSqlTypesGroupsMap.put("ROWID", JavaSqlTypeGroup.id);
    javaSqlTypesGroupsMap.put("SMALLINT", JavaSqlTypeGroup.integer);
    javaSqlTypesGroupsMap.put("SQLXML", JavaSqlTypeGroup.xml);
    javaSqlTypesGroupsMap.put("STRUCT", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("TIME", JavaSqlTypeGroup.temporal);
    javaSqlTypesGroupsMap.put("TIMESTAMP", JavaSqlTypeGroup.temporal);
    javaSqlTypesGroupsMap.put("TINYINT", JavaSqlTypeGroup.integer);
    javaSqlTypesGroupsMap.put("VARBINARY", JavaSqlTypeGroup.binary);
    javaSqlTypesGroupsMap.put("VARCHAR", JavaSqlTypeGroup.character);

    return Collections.unmodifiableMap(javaSqlTypesGroupsMap);
  }

  private static List<JavaSqlType> readJavaSqlTypes()
  {
    final Map<String, Integer> javaSqlTypesMap = new HashMap<>();
    for (final Field field: Types.class.getFields())
    {
      try
      {
        final String javaSqlTypeName = field.getName();
        final Integer javaSqlType = (Integer) field.get(null);
        javaSqlTypesMap.put(javaSqlTypeName, javaSqlType);
      }
      catch (final SecurityException | IllegalAccessException e)
      {
        LOGGER.log(Level.WARNING, "Could not access java.sql.Types, field "
                                  + field, e);
        // continue
      }
    }

    final Map<String, JavaSqlTypeGroup> javaSqlTypeGroupsMap = createJavaSqlTypesGroupsMap();
    final Map<String, Class<?>> javaSqlTypesClassNames = getDefaultTypeMap();

    final List<JavaSqlType> javaSqlTypes = new ArrayList<>();

    for (final Entry<String, Integer> javaSqlTypesEntry: javaSqlTypesMap
      .entrySet())
    {
      if (javaSqlTypesEntry.getKey() != null
          && javaSqlTypesEntry.getValue() != null)
      {
        final Integer javaSqlType = javaSqlTypesEntry.getValue();
        final String javaSqlTypeName = javaSqlTypesEntry.getKey();
        final Class<?> javaSqlTypesClass = javaSqlTypesClassNames
          .get(javaSqlTypeName);
        final JavaSqlTypeGroup javaSqlTypeGroup = javaSqlTypeGroupsMap
          .get(javaSqlTypeName);

        javaSqlTypes.add(new JavaSqlType(javaSqlType,
                                         javaSqlTypeName,
                                         javaSqlTypesClass,
                                         javaSqlTypeGroup));
      }
    }

    return Collections.unmodifiableList(javaSqlTypes);
  }

  private JavaSqlTypesUtility()
  {
  }

}
