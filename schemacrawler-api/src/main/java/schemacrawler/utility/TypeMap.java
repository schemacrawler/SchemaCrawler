/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.utility;


import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;

/**
 * The default mappings are from the JDBC Specification 4.1, Appendix B
 * - Data Type Conversion Tables, Table B-3 - Mapping from JDBC Types to
 * Java Object Types. A JDBC driver may override these default mappings.
 */
public final class TypeMap
  implements Map<String, Class<?>>
{

  private static final Logger LOGGER = Logger
    .getLogger(TypeMap.class.getName());

  private static Map<String, Class<?>> createDefaultTypeMap()
  {
    final Map<String, Class<?>> sqlTypeMap = new HashMap<>();

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
    sqlTypeMap.put("SMALLINT", Integer.class);
    sqlTypeMap.put("SQLXML", java.sql.SQLXML.class);
    sqlTypeMap.put("STRUCT", java.sql.Struct.class);
    sqlTypeMap.put("TIME", java.sql.Time.class);
    sqlTypeMap.put("TIMESTAMP", java.sql.Timestamp.class);
    sqlTypeMap.put("TINYINT", Integer.class);
    sqlTypeMap.put("VARBINARY", byte[].class);
    sqlTypeMap.put("VARCHAR", String.class);

    return sqlTypeMap;
  }

  private final Map<String, Class<?>> sqlTypeMap;

  public TypeMap()
  {
    this(null);
  }

  public TypeMap(final Connection connection)
  {
    final Map<String, Class<?>> sqlTypeMap = createDefaultTypeMap();

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
      catch (final Exception e)
      {
        // Catch all exceptions, since even though most JDBC drivers
        // would throw SQLException, but the Sybase Adaptive
        // Server driver throws UnimplementedOperationException
        LOGGER.log(Level.WARNING,
                   "Could not obtain data type map from connection",
                   e);
      }
    }

    this.sqlTypeMap = sqlTypeMap;
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
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(final Object o)
  {
    return sqlTypeMap.equals(o);
  }

  @Override
  public Class<?> get(final Object key)
  {
    if (containsKey(key))
    {
      return sqlTypeMap.get(key);
    }
    else
    {
      return Object.class;
    }
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
    if (isBlank(className))
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
                   e,
                   new StringFormat("Could not obtain class mapping for data type, %s",
                                    typeName));
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
  public String toString()
  {
    return sqlTypeMap.toString();
  }

  @Override
  public Collection<Class<?>> values()
  {
    return new HashSet<>(sqlTypeMap.values());
  }
}
