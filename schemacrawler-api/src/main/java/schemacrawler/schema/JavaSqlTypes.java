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

package schemacrawler.schema;


import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.JavaSqlType.JavaSqlTypeGroup;
import sf.util.Utility;

/**
 * Utility to work with java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypes
  implements Map<Integer, JavaSqlType>
{

  private static final Logger LOGGER = Logger.getLogger(JavaSqlTypes.class
    .getName());

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

  private static Map<String, Integer> createJavaSqlTypesMap()
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
    return javaSqlTypesMap;
  }

  private static Map<Integer, JavaSqlType> mapJavaSqlTypes()
  {
    final Map<String, Integer> javaSqlTypesMap = createJavaSqlTypesMap();
    final Map<String, JavaSqlTypeGroup> javaSqlTypeGroupsMap = createJavaSqlTypesGroupsMap();

    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<>();

    for (final Entry<String, Integer> javaSqlTypesEntry: javaSqlTypesMap
      .entrySet())
    {
      if (javaSqlTypesEntry.getKey() != null
          && javaSqlTypesEntry.getValue() != null)
      {
        final Integer javaSqlTypeInt = javaSqlTypesEntry.getValue();
        final String javaSqlTypeName = javaSqlTypesEntry.getKey();
        final JavaSqlTypeGroup javaSqlTypeGroup = javaSqlTypeGroupsMap
          .get(javaSqlTypeName);

        final JavaSqlType javaSqlType = new JavaSqlType(javaSqlTypeInt,
                                                        javaSqlTypeName,
                                                        javaSqlTypeGroup);
        javaSqlTypes.put(javaSqlTypeInt, javaSqlType);
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

  private final Map<Integer, JavaSqlType> javaSqlTypeMap;

  public JavaSqlTypes()
  {
    javaSqlTypeMap = mapJavaSqlTypes();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(final Object key)
  {
    return javaSqlTypeMap.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value)
  {
    return javaSqlTypeMap.containsValue(value);
  }

  @Override
  public Set<java.util.Map.Entry<Integer, JavaSqlType>> entrySet()
  {
    return new HashSet<>(javaSqlTypeMap.entrySet());
  }

  @Override
  public boolean equals(final Object o)
  {
    return javaSqlTypeMap.equals(o);
  }

  @Override
  public JavaSqlType get(final Object key)
  {
    JavaSqlType sqlDataType = javaSqlTypeMap.get(key);
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
  public JavaSqlType getFromJavaSqlTypeName(final String typeName)
  {
    JavaSqlType sqlDataType = JavaSqlType.UNKNOWN;
    if (Utility.isBlank(typeName))
    {
      return sqlDataType;
    }

    for (final JavaSqlType javaSqlType: javaSqlTypeMap.values())
    {
      if (typeName.equals(javaSqlType.getJavaSqlTypeName()))
      {
        sqlDataType = javaSqlType;
        break;
      }
    }
    return sqlDataType;
  }

  @Override
  public int hashCode()
  {
    return javaSqlTypeMap.hashCode();
  }

  @Override
  public boolean isEmpty()
  {
    return javaSqlTypeMap.isEmpty();
  }

  @Override
  public Set<Integer> keySet()
  {
    return new HashSet<>(javaSqlTypeMap.keySet());
  }

  @Override
  public JavaSqlType put(final Integer key, final JavaSqlType value)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends Integer, ? extends JavaSqlType> m)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public JavaSqlType remove(final Object key)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size()
  {
    return javaSqlTypeMap.size();
  }

  @Override
  public Collection<JavaSqlType> values()
  {
    return new HashSet<>(javaSqlTypeMap.values());
  }

}
