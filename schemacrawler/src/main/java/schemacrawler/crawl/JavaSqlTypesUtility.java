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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility to work with java.sql.Types, and Java class name mappings.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypesUtility
{

  public static enum JavaSqlTypeGroup
  {
    unknown,
    binary,
    bit,
    character,
    temporal,
    id,
    integer,
    real,
    reference,
    url,
    xml;
  }

  private static final Logger LOGGER = Logger
    .getLogger(JavaSqlTypesUtility.class.getName());

  private static final Map<Integer, JavaSqlType> JAVA_SQL_TYPES = readJavaSqlTypes();
  private static final Map<String, JavaSqlTypeGroup> JAVA_SQL_TYPES_GROUPS = readJavaSqlTypesGroupsMap();

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
    JavaSqlType sqlDataType = JAVA_SQL_TYPES.get(type);
    if (sqlDataType == null)
    {
      sqlDataType = JavaSqlType.UNKNOWN;
    }
    return sqlDataType;
  }

  /**
   * Lookup java.sql.Types type, and return the type group.
   * 
   * @param type
   *        java.sql.Types type
   * @return JavaSqlTypeGroup group
   */
  public static JavaSqlTypeGroup lookupSqlDataTypeGroup(final int type)
  {
    final JavaSqlType sqlDataType = lookupSqlDataType(type);
    JavaSqlTypeGroup javaSqlTypeGroup = JAVA_SQL_TYPES_GROUPS.get(sqlDataType
      .getJavaSqlTypeName());
    if (javaSqlTypeGroup == null)
    {
      javaSqlTypeGroup = JavaSqlTypeGroup.unknown;
    }
    return javaSqlTypeGroup;
  }

  public static void main(final String[] args)
    throws IOException
  {
    for (final JavaSqlType javaSqlType: JAVA_SQL_TYPES.values())
    {
      System.out.println(javaSqlType);
    }

    final Writer writer;
    if (args.length > 0)
    {
      writer = new FileWriter(new File(args[0]));
    }
    else
    {
      writer = new OutputStreamWriter(System.out);
    }

    writer.write(String.format("# java.sql.Types from Java %s %s\n", System
      .getProperty("java.version"), System.getProperty("java.vendor")));
    final List<JavaSqlType> javaSqlTypes = new ArrayList<JavaSqlType>(getJavaSqlTypes()
      .values());
    Collections.sort(javaSqlTypes);
    for (final JavaSqlType sqlDataType: javaSqlTypes)
    {
      writer.write(String.format("%s=%d\n",
                                 sqlDataType.getJavaSqlTypeName(),
                                 sqlDataType.getJavaSqlType()));
    }
    writer.flush();
    writer.close();
  }

  private static Map<Integer, JavaSqlType> getJavaSqlTypes()
  {
    final Map<String, Class<?>> javaSqlTypesClassMap = getJavaSqlTypesPrimitivesClassMap();
    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<Integer, JavaSqlType>();
    final Field[] javaSqlTypesFields = Types.class.getFields();
    for (final Field field: javaSqlTypesFields)
    {
      try
      {
        final String javaSqlTypeName = field.getName();
        final Integer javaSqlType = (Integer) field.get(null);
        final Class<?> javaSqlTypeClass = javaSqlTypesClassMap
          .get(javaSqlTypeName);
        javaSqlTypes.put(javaSqlType, new JavaSqlType(javaSqlType,
                                                      javaSqlTypeName,
                                                      javaSqlTypeClass));
      }
      catch (final SecurityException e)
      {
        LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
        continue;
      }
      catch (final IllegalAccessException e)
      {
        LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
        continue;
      }
    }
    return javaSqlTypes;
  }

  /**
   * Map java.sql.Types to Java classes. Since this information is not
   * available in the JDK, we need to hard-code it.
   * 
   * @see <a
   *      href="http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/mapping.html#1004791">Mapping
   *      SQL and Java Types</a>
   */
  private static Map<String, Class<?>> getJavaSqlTypesPrimitivesClassMap()
  {
    final Map<String, Class<?>> javaSqlTypesClassMap = new TreeMap<String, Class<?>>();

    javaSqlTypesClassMap.put("BIGINT", long.class);
    javaSqlTypesClassMap.put("BINARY", byte[].class);
    javaSqlTypesClassMap.put("BIT", boolean.class);
    javaSqlTypesClassMap.put("BOOLEAN", boolean.class);
    javaSqlTypesClassMap.put("CHAR", String.class);
    javaSqlTypesClassMap.put("DATALINK", java.net.URL.class);
    javaSqlTypesClassMap.put("DECIMAL", java.math.BigDecimal.class);
    javaSqlTypesClassMap.put("DOUBLE", double.class);
    javaSqlTypesClassMap.put("FLOAT", double.class);
    javaSqlTypesClassMap.put("INTEGER", int.class);
    javaSqlTypesClassMap.put("JAVA_OBJECT", Object.class);
    javaSqlTypesClassMap.put("LONGNVARCHAR", String.class);
    javaSqlTypesClassMap.put("LONGVARBINARY", byte[].class);
    javaSqlTypesClassMap.put("LONGVARCHAR", String.class);
    javaSqlTypesClassMap.put("NCHAR", String.class);
    javaSqlTypesClassMap.put("NULL", void.class);
    javaSqlTypesClassMap.put("NUMERIC", java.math.BigDecimal.class);
    javaSqlTypesClassMap.put("NVARCHAR", String.class);
    javaSqlTypesClassMap.put("OTHER", Object.class);
    javaSqlTypesClassMap.put("REAL", float.class);
    javaSqlTypesClassMap.put("SMALLINT", short.class);
    javaSqlTypesClassMap.put("TINYINT", byte.class);
    javaSqlTypesClassMap.put("VARBINARY", byte[].class);
    javaSqlTypesClassMap.put("VARCHAR", String.class);

    return Collections.unmodifiableMap(javaSqlTypesClassMap);
  }

  private static Map<Integer, JavaSqlType> readJavaSqlTypes()
  {
    final Map<String, Class<?>> javaSqlTypesClassMap = getJavaSqlTypesPrimitivesClassMap();
    final Properties javaSqlTypesClassNames = readJavaSqlTypesClassNameMap();
    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<Integer, JavaSqlType>();
    final Properties javaSqlTypesProperties = readPropertiesResource("/java.sql.Types.properties");
    for (final Entry<Object, Object> javaSqlTypesEntry: javaSqlTypesProperties
      .entrySet())
    {
      if (javaSqlTypesEntry.getKey() != null
          && javaSqlTypesEntry.getValue() != null)
      {
        final Integer javaSqlType = Integer.parseInt(javaSqlTypesEntry
          .getValue().toString());
        final String javaSqlTypeName = javaSqlTypesEntry.getKey().toString();
        Class<?> javaSqlTypeClass;
        final String javaSqlTypesClassName = javaSqlTypesClassNames
          .getProperty(javaSqlTypeName);

        try
        {
          if (javaSqlTypesClassName != null)
          {
            javaSqlTypeClass = Class.forName(javaSqlTypesClassName);
          }
          else
          {
            javaSqlTypeClass = null;
          }
        }
        catch (final Throwable e) // A number of exceptions or errors
        // could occur
        {
          LOGGER.log(Level.WARNING, "Cannot load class, "
                                    + javaSqlTypesClassName, e);
          javaSqlTypeClass = null;
        }
        if (javaSqlTypeClass == null)
        {
          javaSqlTypeClass = javaSqlTypesClassMap.get(javaSqlTypeName);
        }

        if (javaSqlTypeClass != null)
        {
          javaSqlTypes.put(javaSqlType, new JavaSqlType(javaSqlType,
                                                        javaSqlTypeName,
                                                        javaSqlTypeClass));
        }
        else
        {
          javaSqlTypes.put(javaSqlType, new JavaSqlType(javaSqlType,
                                                        javaSqlTypeName,
                                                        javaSqlTypesClassName));
        }
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

  private static Properties readJavaSqlTypesClassNameMap()
  {
    return readPropertiesResource("/java.sql.Types.mappings.properties");
  }

  private static Map<String, JavaSqlTypeGroup> readJavaSqlTypesGroupsMap()
  {
    final Map<String, JavaSqlTypeGroup> javaSqlTypesGroupsMap = new HashMap<String, JavaSqlTypeGroup>();
    final Properties javaSqlTypesGroups = readPropertiesResource("/java.sql.Types.groups.properties");
    for (final Entry<Object, Object> javaSqlTypesGroupsEntry: javaSqlTypesGroups
      .entrySet())
    {
      try
      {
        final String javaSqlTypeName = javaSqlTypesGroupsEntry.getKey()
          .toString();
        final JavaSqlTypeGroup group = JavaSqlTypeGroup
          .valueOf(javaSqlTypesGroupsEntry.getValue().toString());
        javaSqlTypesGroupsMap.put(javaSqlTypeName, group);
      }
      catch (final Exception e)
      {
        continue;
      }
    }
    return Collections.unmodifiableMap(javaSqlTypesGroupsMap);
  }

  private static Properties readPropertiesResource(final String resource)
  {
    final Properties properties = new Properties();
    final InputStream inputStream = JavaSqlTypesUtility.class
      .getResourceAsStream(resource);
    if (inputStream != null)
    {
      try
      {
        properties.load(inputStream);
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not read internal resource, "
                                  + resource, e);
      }
      finally
      {
        try
        {
          inputStream.close();
        }
        catch (final IOException e)
        {
          // Ignore
        }
      }
    }
    return properties;
  }

  private JavaSqlTypesUtility()
  {
  }

}
