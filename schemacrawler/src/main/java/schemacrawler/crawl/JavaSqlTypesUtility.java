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
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.Struct;
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
 * A wrapper around java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypesUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(JavaSqlTypesUtility.class.getName());

  private static final Map<Integer, JavaSqlType> JAVA_SQL_TYPES = getJavaSqlTypes();
  private static final Map<String, Class<?>> TYPE_CLASS_NAME_MAP = getTypeClassNameMap();

  /**
   * The java.sql.Types type.
   * 
   * @param type
   *        java.sql.Types type
   * @return Mapped Java class.
   */
  public static Class<?> lookupMappedJavaClass(final int type)
  {
    final JavaSqlType sqlDataType = lookupSqlDataType(type);
    return TYPE_CLASS_NAME_MAP.get(sqlDataType.getTypeName());
  }

  /**
   * The java.sql.Types type.
   * 
   * @param type
   *        java.sql.Types type
   * @return Mapped Java class.
   */
  public static String lookupMappedJavaClassName(final int type)
  {
    final Class<?> mappedJavaClass = lookupMappedJavaClass(type);
    if (mappedJavaClass != null)
    {
      return mappedJavaClass.getCanonicalName();
    }
    else
    {
      return null;
    }
  }

  /**
   * The java.sql.Types type.
   * 
   * @param type
   *        java.sql.Types type
   * @return java.sql.Types type
   */
  public static JavaSqlType lookupSqlDataType(final int type)
  {
    JavaSqlType sqlDataType = JAVA_SQL_TYPES.get(Integer.valueOf(type));
    if (sqlDataType == null)
    {
      sqlDataType = JavaSqlType.UNKNOWN;
    }
    return sqlDataType;
  }

  public static void main(final String[] args)
    throws IOException
  {
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
    final List<JavaSqlType> javaSqlTypes = new ArrayList<JavaSqlType>(obtainJavaSqlTypes()
      .values());
    Collections.sort(javaSqlTypes);
    for (final JavaSqlType sqlDataType: javaSqlTypes)
    {
      writer.write(String.format("%s\n", sqlDataType));
    }
    writer.flush();
    writer.close();
  }

  private static Map<Integer, JavaSqlType> getJavaSqlTypes()
  {

    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<Integer, JavaSqlType>();

    final InputStream javaSqlTypesStream = JavaSqlTypesUtility.class
      .getResourceAsStream("/java.sql.Types.properties");
    if (javaSqlTypesStream != null)
    {
      final Properties javaSqlTypesProperties = new Properties();
      try
      {
        javaSqlTypesProperties.load(javaSqlTypesStream);
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not read internal resource", e);
      }
      finally
      {
        try
        {
          javaSqlTypesStream.close();
        }
        catch (final IOException e)
        {
          // Ignore
        }
      }
      for (final Entry<Object, Object> entry: javaSqlTypesProperties.entrySet())
      {
        if (entry.getKey() != null && entry.getValue() != null)
        {
          final Integer type = Integer.parseInt(entry.getValue().toString());
          final String typeName = entry.getKey().toString();
          javaSqlTypes.put(type, new JavaSqlType(type, typeName));
        }
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

  /**
   * @see <a
   *      href="http://java.sun.com/j2se/1.5.0/docs/guide/jdbc/getstart/mapping.html">Mapping
   *      SQL and Java Types</a>
   */
  private static Map<String, Class<?>> getTypeClassNameMap()
  {
    final Map<String, Class<?>> typeClassNameMap = new TreeMap<String, Class<?>>();

    typeClassNameMap.put("CHAR", String.class);
    typeClassNameMap.put("VARCHAR", String.class);
    typeClassNameMap.put("LONGVARCHAR", String.class);
    typeClassNameMap.put("NUMERIC", java.math.BigDecimal.class);
    typeClassNameMap.put("DECIMAL", java.math.BigDecimal.class);
    typeClassNameMap.put("BIT", boolean.class);
    typeClassNameMap.put("TINYINT", byte.class);
    typeClassNameMap.put("SMALLINT", short.class);
    typeClassNameMap.put("INTEGER", int.class);
    typeClassNameMap.put("BIGINT", long.class);
    typeClassNameMap.put("REAL", float.class);
    typeClassNameMap.put("FLOAT", double.class);
    typeClassNameMap.put("DOUBLE", double.class);
    typeClassNameMap.put("BINARY", byte[].class);
    typeClassNameMap.put("VARBINARY", byte[].class);
    typeClassNameMap.put("LONGVARBINARY", byte[].class);
    typeClassNameMap.put("DATE", java.sql.Date.class);
    typeClassNameMap.put("TIME", java.sql.Time.class);
    typeClassNameMap.put("TIMESTAMP", java.sql.Timestamp.class);
    typeClassNameMap.put("CLOB", Clob.class);
    typeClassNameMap.put("BLOB", Blob.class);
    typeClassNameMap.put("ARRAY", Array.class);
    typeClassNameMap.put("STRUCT", Struct.class);
    typeClassNameMap.put("REF", Ref.class);

    return typeClassNameMap;
  }

  private static Map<Integer, JavaSqlType> obtainJavaSqlTypes()
  {
    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<Integer, JavaSqlType>();
    final Field[] javaSqlTypesFields = Types.class.getFields();
    for (final Field field: javaSqlTypesFields)
    {
      try
      {
        final String fieldName = field.getName();
        final Integer fieldValue = (Integer) field.get(null);
        javaSqlTypes.put(fieldValue, new JavaSqlType(fieldValue, fieldName));
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

  private JavaSqlTypesUtility()
  {
  }

}
