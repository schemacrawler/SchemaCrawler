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

package schemacrawler.schema;


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
public final class SqlDataTypeUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(SqlDataTypeUtility.class.getName());

  /** Unknown SQL data type. */
  public static final SqlDataType UNKNOWN = new SqlDataType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>");

  private static final Map<Integer, SqlDataType> JAVA_SQL_TYPES = getJavaSqlTypes();
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
    final SqlDataType sqlDataType = lookupSqlDataType(type);
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
  public static SqlDataType lookupSqlDataType(final int type)
  {
    SqlDataType sqlDataType = JAVA_SQL_TYPES.get(Integer.valueOf(type));
    if (sqlDataType == null)
    {
      sqlDataType = UNKNOWN;
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
    final List<SqlDataType> javaSqlTypes = new ArrayList<SqlDataType>(obtainJavaSqlTypes()
      .values());
    Collections.sort(javaSqlTypes);
    for (final SqlDataType sqlDataType: javaSqlTypes)
    {
      writer.write(String.format("%s\n", sqlDataType));
    }
    writer.flush();
    writer.close();
  }

  private static Map<Integer, SqlDataType> getJavaSqlTypes()
  {

    final Map<Integer, SqlDataType> javaSqlTypes = new HashMap<Integer, SqlDataType>();

    final InputStream javaSqlTypesStream = SqlDataTypeUtility.class
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
          javaSqlTypes.put(type, new SqlDataType(type, typeName));
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

  private static Map<Integer, SqlDataType> obtainJavaSqlTypes()
  {
    final Map<Integer, SqlDataType> javaSqlTypes = new HashMap<Integer, SqlDataType>();
    final Field[] javaSqlTypesFields = Types.class.getFields();
    for (final Field field: javaSqlTypesFields)
    {
      try
      {
        final String fieldName = field.getName();
        final Integer fieldValue = (Integer) field.get(null);
        javaSqlTypes.put(fieldValue, new SqlDataType(fieldValue, fieldName));
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

  private SqlDataTypeUtility()
  {
  }

}
