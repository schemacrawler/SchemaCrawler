/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.JavaSqlType.JavaSqlTypeGroup;
import schemacrawler.schemacrawler.Config;

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

  public static void main(final String[] args)
    throws IOException
  {
    for (final JavaSqlType javaSqlType: readJavaSqlTypes())
    {
      System.out.println(javaSqlType);
    }
  }

  private static Map<Integer, JavaSqlType> mapJavaSqlTypesByType(final List<JavaSqlType> javaSqlTypes)
  {
    final Map<Integer, JavaSqlType> javaSqlTypesByTypeMap = new HashMap<Integer, JavaSqlType>();
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
    final Map<String, JavaSqlType> javaSqlTypesByTypeNameMap = new HashMap<String, JavaSqlType>();
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

  private static List<JavaSqlType> readJavaSqlTypes()
  {
    final Map<String, JavaSqlTypeGroup> javaSqlTypeGroupsMap = readJavaSqlTypesGroupsMap();
    final Map<String, String> javaSqlTypesClassNames = readJavaSqlTypesClassNameMap();
    final Map<String, String> javaSqlTypesProperties = readPropertiesResource("/java.sql.Types.properties");

    final List<JavaSqlType> javaSqlTypes = new ArrayList<JavaSqlType>();

    for (final Entry<String, String> javaSqlTypesEntry: javaSqlTypesProperties
      .entrySet())
    {
      if (javaSqlTypesEntry.getKey() != null
          && javaSqlTypesEntry.getValue() != null)
      {
        final Integer javaSqlType = Integer.parseInt(javaSqlTypesEntry
          .getValue());
        final String javaSqlTypeName = javaSqlTypesEntry.getKey();
        final String javaSqlTypesClassName = javaSqlTypesClassNames
          .get(javaSqlTypeName);
        final JavaSqlTypeGroup javaSqlTypeGroup = javaSqlTypeGroupsMap
          .get(javaSqlTypeName);

        javaSqlTypes.add(new JavaSqlType(javaSqlType,
                                         javaSqlTypeName,
                                         javaSqlTypesClassName,
                                         javaSqlTypeGroup));
      }
    }

    return Collections.unmodifiableList(javaSqlTypes);
  }

  private static Map<String, String> readJavaSqlTypesClassNameMap()
  {
    return readPropertiesResource("/java.sql.Types.mappings.properties");
  }

  private static Map<String, JavaSqlTypeGroup> readJavaSqlTypesGroupsMap()
  {
    final Map<String, JavaSqlTypeGroup> javaSqlTypesGroupsMap = new HashMap<String, JavaSqlTypeGroup>();
    final Map<String, String> javaSqlTypesGroups = readPropertiesResource("/java.sql.Types.groups.properties");
    for (final Entry<String, String> javaSqlTypesGroupsEntry: javaSqlTypesGroups
      .entrySet())
    {
      try
      {
        final String javaSqlTypeName = javaSqlTypesGroupsEntry.getKey();
        final JavaSqlTypeGroup group = JavaSqlTypeGroup
          .valueOf(javaSqlTypesGroupsEntry.getValue());
        javaSqlTypesGroupsMap.put(javaSqlTypeName, group);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING, "Could not read java.sql.Types groups", e);
        continue;
      }
    }
    return Collections.unmodifiableMap(javaSqlTypesGroupsMap);
  }

  private static Map<String, String> readPropertiesResource(final String resource)
  {
    final Map<String, String> properties;
    final InputStream inputStream = JavaSqlTypesUtility.class
      .getResourceAsStream(resource);
    if (inputStream != null)
    {
      properties = Config.load(inputStream);
    }
    else
    {
      properties = Collections.EMPTY_MAP;
    }
    return properties;
  }

  private JavaSqlTypesUtility()
  {
  }

}
