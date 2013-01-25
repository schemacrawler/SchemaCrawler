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

package schemacrawler.utility;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import sf.util.Utility;

/**
 * Utility to work with java.sql.Types, and Java class name mappings.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypesGenerationUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(JavaSqlTypesGenerationUtility.class.getName());

  public static void main(final String[] args)
    throws IOException
  {
    if (args.length > 0)
    {
      writeJavaSqlTypes(new File(args[0]));
    }
    else
    {
      writeJavaSqlTypes(new File("."));
    }
  }

  /**
   * Map java.sql.Types to Java classes. Since this information is not
   * available in the JDK, we need to hard-code it.
   * 
   * @return Map
   * @see <a
   *      href="http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/mapping.html#1004791">Mapping
   *      SQL and Java Types</a>
   */
  private static Map<String, String> getJavaSqlTypesClassNameMap()
  {
    final Map<String, Class<?>> javaSqlTypesPrimitivesClassMap = new TreeMap<String, Class<?>>();
    javaSqlTypesPrimitivesClassMap.put("BIGINT", Long.class);
    javaSqlTypesPrimitivesClassMap.put("BINARY", byte[].class);
    javaSqlTypesPrimitivesClassMap.put("BIT", Boolean.class);
    javaSqlTypesPrimitivesClassMap.put("BOOLEAN", Boolean.class);
    javaSqlTypesPrimitivesClassMap.put("CHAR", String.class);
    javaSqlTypesPrimitivesClassMap.put("DATALINK", java.net.URL.class);
    javaSqlTypesPrimitivesClassMap.put("DECIMAL", java.math.BigDecimal.class);
    javaSqlTypesPrimitivesClassMap.put("DISTINCT", Object.class);
    javaSqlTypesPrimitivesClassMap.put("DOUBLE", Double.class);
    javaSqlTypesPrimitivesClassMap.put("FLOAT", Double.class);
    javaSqlTypesPrimitivesClassMap.put("INTEGER", Integer.class);
    javaSqlTypesPrimitivesClassMap.put("JAVA_OBJECT", Object.class);
    javaSqlTypesPrimitivesClassMap.put("LONGNVARCHAR", String.class);
    javaSqlTypesPrimitivesClassMap.put("LONGVARBINARY", byte[].class);
    javaSqlTypesPrimitivesClassMap.put("LONGVARCHAR", String.class);
    javaSqlTypesPrimitivesClassMap.put("NCHAR", String.class);
    javaSqlTypesPrimitivesClassMap.put("NULL", Void.class);
    javaSqlTypesPrimitivesClassMap.put("NUMERIC", java.math.BigDecimal.class);
    javaSqlTypesPrimitivesClassMap.put("NVARCHAR", String.class);
    javaSqlTypesPrimitivesClassMap.put("OTHER", Object.class);
    javaSqlTypesPrimitivesClassMap.put("REAL", Float.class);
    javaSqlTypesPrimitivesClassMap.put("SMALLINT", Short.class);
    javaSqlTypesPrimitivesClassMap.put("TINYINT", byte.class);
    javaSqlTypesPrimitivesClassMap.put("VARBINARY", byte[].class);
    javaSqlTypesPrimitivesClassMap.put("VARCHAR", String.class);

    final Map<String, String> javaSqlTypesClassNamesMap = new TreeMap<String, String>();
    for (final Entry<String, Class<?>> javaSqlTypesPrimitivesClassMapping: javaSqlTypesPrimitivesClassMap
      .entrySet())
    {
      javaSqlTypesClassNamesMap
        .put(javaSqlTypesPrimitivesClassMapping.getKey(),
             javaSqlTypesPrimitivesClassMapping.getValue().getCanonicalName());
    }
    javaSqlTypesClassNamesMap.put("ARRAY", "java.sql.Array");
    javaSqlTypesClassNamesMap.put("BLOB", "java.sql.Blob");
    javaSqlTypesClassNamesMap.put("CLOB", "java.sql.Clob");
    javaSqlTypesClassNamesMap.put("DATE", "java.sql.Date");
    javaSqlTypesClassNamesMap.put("NCLOB", "java.sql.NClob");
    javaSqlTypesClassNamesMap.put("REF", "java.sql.Ref");
    javaSqlTypesClassNamesMap.put("ROWID", "java.sql.RowId");
    javaSqlTypesClassNamesMap.put("SQLXML", "java.sql.SQLXML");
    javaSqlTypesClassNamesMap.put("STRUCT", "java.sql.Struct");
    javaSqlTypesClassNamesMap.put("TIME", "java.sql.Time");
    javaSqlTypesClassNamesMap.put("TIMESTAMP", "java.sql.Timestamp");

    return Collections.unmodifiableMap(javaSqlTypesClassNamesMap);
  }

  private static Map<String, JavaSqlTypeGroup> getJavaSqlTypesGroupsMap()
  {
    final Map<String, JavaSqlTypeGroup> javaSqlTypesGroupsMap = new HashMap<String, JavaSqlTypeGroup>();
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

  private static Writer startWriting(final File directory, final String fileName)
    throws IOException
  {
    final Writer writer;
    writer = new FileWriter(new File(directory, fileName));
    writer.write(String.format("# java.sql.Types from Java %s %s%n",
                               System.getProperty("java.version"),
                               System.getProperty("java.vendor")));
    return writer;
  }

  private static void writeJavaSqlTypes(final File directory)
    throws IOException
  {
    if (directory == null || !directory.canWrite() || !directory.isDirectory())
    {
      LOGGER.log(Level.WARNING, "Cannot write to directory, " + directory);
      return;
    }

    final Map<String, Integer> javaSqlTypesMap = new HashMap<String, Integer>();
    final Map<String, JavaSqlTypeGroup> javaSqlTypeGroupsMap = getJavaSqlTypesGroupsMap();
    final Map<String, String> javaSqlTypesClassMap = getJavaSqlTypesClassNameMap();
    for (final Field field: Types.class.getFields())
    {
      try
      {
        final String javaSqlTypeName = field.getName();
        final Integer javaSqlType = (Integer) field.get(null);
        javaSqlTypesMap.put(javaSqlTypeName, javaSqlType);
      }
      catch (final SecurityException e)
      {
        LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
        // continue
      }
      catch (final IllegalAccessException e)
      {
        LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
        // continue
      }
    }

    final Writer[] writers = new Writer[3];
    writers[0] = startWriting(directory, "java.sql.Types.properties");
    writers[1] = startWriting(directory, "java.sql.Types.mappings.properties");
    writers[2] = startWriting(directory, "java.sql.Types.groups.properties");

    final List<String> javaSqlTypeNames = new ArrayList<String>(javaSqlTypesMap.keySet());
    Collections.sort(javaSqlTypeNames);
    for (final String javaSqlTypeName: javaSqlTypeNames)
    {
      writers[0].write(String.format("%s=%d%n",
                                     javaSqlTypeName,
                                     javaSqlTypesMap.get(javaSqlTypeName)));
      //
      final String javaSqlTypeMappedClassName = javaSqlTypesClassMap
        .get(javaSqlTypeName);
      if (!Utility.isBlank(javaSqlTypeMappedClassName))
      {
        writers[1].write(String.format("%s=%s%n",
                                       javaSqlTypeName,
                                       javaSqlTypeMappedClassName));
      }
      //
      final JavaSqlTypeGroup javaSqlTypeGroup = javaSqlTypeGroupsMap
        .get(javaSqlTypeName);
      if (javaSqlTypeGroup != null)
      {
        writers[2].write(String.format("%s=%s%n",
                                       javaSqlTypeName,
                                       javaSqlTypeGroup.name()));
      }
    }
    for (final Writer writer: writers)
    {
      writer.flush();
      writer.close();
    }
  }

  private JavaSqlTypesGenerationUtility()
  {
  }

}
