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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

final class TypeMap
  implements Map<String, Class<?>>
{

  private static final Logger LOGGER = Logger
    .getLogger(TypeMap.class.getName());

  private final SortedMap<String, Class<?>> sqlTypeMap = new TreeMap<>();

  TypeMap(final Connection connection)
  {
    createDefaultTypeMap();

    if (connection != null)
    {
      // Override and add mappings from the connection
      try
      {
        final Map<String, Class<?>> typeMap = connection.getTypeMap();
        if (typeMap != null && !typeMap.isEmpty())
        {
          sqlTypeMap.putAll(typeMap);
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   "Could not obtain data type map from connection",
                   e);
      }
    }
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(final Object key)
  {
    return sqlTypeMap.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value)
  {
    return sqlTypeMap.containsValue(value);
  }

  @Override
  public Set<Entry<String, Class<?>>> entrySet()
  {
    return new HashSet<>(sqlTypeMap.entrySet());
  }

  @Override
  public boolean equals(final Object o)
  {
    return sqlTypeMap.equals(o);
  }

  @Override
  public Class<?> get(final Object key)
  {
    return sqlTypeMap.get(key);
  }

  /**
   * Gets the Java type mapping for a data type. If no mapping exists,
   * returns null. If a class name is passed in, it overrides the
   * mapping in the type map.
   * 
   * @param typeName
   *        Type name to find a mapping for.
   * @param className
   *        Overridden class name
   * @return Mapped class
   */
  public Class<?> get(final String typeName, final String className)
  {
    if (Utility.isBlank(className))
    {
      return sqlTypeMap.get(typeName);
    }
    else
    {
      try
      {
        return Class.forName(className);
      }
      catch (final ClassNotFoundException e)
      {
        LOGGER.log(Level.WARNING,
                   "Could not obtain class mapping for data type " + typeName,
                   e);
        return null;
      }
    }
  }

  @Override
  public int hashCode()
  {
    return sqlTypeMap.hashCode();
  }

  @Override
  public boolean isEmpty()
  {
    return sqlTypeMap.isEmpty();
  }

  @Override
  public Set<String> keySet()
  {
    return new HashSet<>(sqlTypeMap.keySet());
  }

  @Override
  public Class<?> put(final String key, final Class<?> value)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends String, ? extends Class<?>> m)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Class<?> remove(final Object key)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size()
  {
    return sqlTypeMap.size();
  }

  @Override
  public Collection<Class<?>> values()
  {
    return new HashSet<>(sqlTypeMap.values());
  }

  private void createDefaultTypeMap()
  {
    // From the JDBC Specification 4.1,
    // Appendix B (Data Type Conversion Tables)
    sqlTypeMap.put("ARRAY", java.sql.Array.class);
    sqlTypeMap.put("BIGINT", Long.class);
    sqlTypeMap.put("BINARY", byte[].class);
    sqlTypeMap.put("BIT", Boolean.class);
    sqlTypeMap.put("BLOB", java.sql.Blob.class);
    sqlTypeMap.put("BOOLEAN", Boolean.class);
    sqlTypeMap.put("CHAR", String.class);
    sqlTypeMap.put("CLOB", java.sql.Clob.class);
    sqlTypeMap.put("DATALINK", java.net.URL.class);
    sqlTypeMap.put("DATE", java.sql.Date.class);
    sqlTypeMap.put("DECIMAL", java.math.BigDecimal.class);
    sqlTypeMap.put("DISTINCT", Object.class);
    sqlTypeMap.put("DOUBLE", Double.class);
    sqlTypeMap.put("FLOAT", Double.class);
    sqlTypeMap.put("INTEGER", Integer.class);
    sqlTypeMap.put("JAVA_OBJECT", Object.class);
    sqlTypeMap.put("LONGNVARCHAR", String.class);
    sqlTypeMap.put("LONGVARBINARY", byte[].class);
    sqlTypeMap.put("LONGVARCHAR", String.class);
    sqlTypeMap.put("NCHAR", String.class);
    sqlTypeMap.put("NCLOB", java.sql.NClob.class);
    sqlTypeMap.put("NULL", Void.class);
    sqlTypeMap.put("NUMERIC", java.math.BigDecimal.class);
    sqlTypeMap.put("NVARCHAR", String.class);
    sqlTypeMap.put("OTHER", Object.class);
    sqlTypeMap.put("REAL", Float.class);
    sqlTypeMap.put("REF", java.sql.Ref.class);
    sqlTypeMap.put("ROWID", java.sql.RowId.class);
    sqlTypeMap.put("SMALLINT", Short.class);
    sqlTypeMap.put("SQLXML", java.sql.SQLXML.class);
    sqlTypeMap.put("STRUCT", java.sql.Struct.class);
    sqlTypeMap.put("TIME", java.sql.Time.class);
    sqlTypeMap.put("TIMESTAMP", java.sql.Timestamp.class);
    sqlTypeMap.put("TINYINT", byte.class);
    sqlTypeMap.put("VARBINARY", byte[].class);
    sqlTypeMap.put("VARCHAR", String.class);
  }

}
