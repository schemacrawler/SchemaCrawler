/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TypeMap implements Map<String, Class<?>> {

  private static final Logger LOGGER = Logger.getLogger(TypeMap.class.getName());

  /**
   * The default mappings are from the JDBC Specification 4.2, Appendix B - Data Type Conversion
   * Tables, Table B-3 - Mapping from JDBC Types to Java Object Types. A JDBC driver may override
   * these default mappings.
   */
  private static Map<SQLType, Class<?>> createDefaultTypeMap() {
    final Map<SQLType, Class<?>> defaultTypeMap = new HashMap<>();

    defaultTypeMap.put(JDBCType.ARRAY, java.sql.Array.class);
    defaultTypeMap.put(JDBCType.BIGINT, Long.class);
    defaultTypeMap.put(JDBCType.BINARY, byte[].class);
    defaultTypeMap.put(JDBCType.BIT, Boolean.class);
    defaultTypeMap.put(JDBCType.BLOB, java.sql.Blob.class);
    defaultTypeMap.put(JDBCType.BOOLEAN, Boolean.class);
    defaultTypeMap.put(JDBCType.CHAR, String.class);
    defaultTypeMap.put(JDBCType.CLOB, java.sql.Clob.class);
    defaultTypeMap.put(JDBCType.DATALINK, java.net.URL.class);
    defaultTypeMap.put(JDBCType.DATE, java.sql.Date.class);
    defaultTypeMap.put(JDBCType.DECIMAL, java.math.BigDecimal.class);
    defaultTypeMap.put(JDBCType.DISTINCT, Object.class);
    defaultTypeMap.put(JDBCType.DOUBLE, Double.class);
    defaultTypeMap.put(JDBCType.FLOAT, Double.class);
    defaultTypeMap.put(JDBCType.INTEGER, Integer.class);
    defaultTypeMap.put(JDBCType.JAVA_OBJECT, Object.class);
    defaultTypeMap.put(JDBCType.LONGNVARCHAR, String.class);
    defaultTypeMap.put(JDBCType.LONGVARBINARY, byte[].class);
    defaultTypeMap.put(JDBCType.LONGVARCHAR, String.class);
    defaultTypeMap.put(JDBCType.NCHAR, String.class);
    defaultTypeMap.put(JDBCType.NCLOB, java.sql.NClob.class);
    defaultTypeMap.put(JDBCType.NULL, Void.class);
    defaultTypeMap.put(JDBCType.NUMERIC, java.math.BigDecimal.class);
    defaultTypeMap.put(JDBCType.NVARCHAR, String.class);
    defaultTypeMap.put(JDBCType.OTHER, Object.class);
    defaultTypeMap.put(JDBCType.REAL, Float.class);
    defaultTypeMap.put(JDBCType.REF, java.sql.Ref.class);
    defaultTypeMap.put(JDBCType.REF_CURSOR, Object.class);
    defaultTypeMap.put(JDBCType.ROWID, java.sql.RowId.class);
    defaultTypeMap.put(JDBCType.SMALLINT, Integer.class);
    defaultTypeMap.put(JDBCType.SQLXML, java.sql.SQLXML.class);
    defaultTypeMap.put(JDBCType.STRUCT, java.sql.Struct.class);
    defaultTypeMap.put(JDBCType.TIME, java.sql.Time.class);
    defaultTypeMap.put(JDBCType.TIMESTAMP, java.sql.Timestamp.class);
    defaultTypeMap.put(JDBCType.TIMESTAMP_WITH_TIMEZONE, java.time.OffsetDateTime.class);
    defaultTypeMap.put(JDBCType.TIME_WITH_TIMEZONE, java.time.OffsetTime.class);
    defaultTypeMap.put(JDBCType.TINYINT, Integer.class);
    defaultTypeMap.put(JDBCType.VARBINARY, byte[].class);
    defaultTypeMap.put(JDBCType.VARCHAR, String.class);

    return defaultTypeMap;
  }

  private final Map<String, Class<?>> sqlTypeMap;

  public TypeMap() {
    sqlTypeMap = new HashMap<>();

    final Map<SQLType, Class<?>> defaultTypeMap = createDefaultTypeMap();
    for (final Entry<SQLType, Class<?>> sqlTypeMapping : defaultTypeMap.entrySet()) {
      sqlTypeMap.put(sqlTypeMapping.getKey().getName(), sqlTypeMapping.getValue());
    }
  }

  public TypeMap(final Connection connection) {

    this();

    if (connection == null) {
      LOGGER.log(
          Level.WARNING, "No connection provided, so not getting connection specific type map");
      return;
    }

    // Override and add mappings from the connection
    try {
      final Map<String, Class<?>> typeMap = connection.getTypeMap();
      if (typeMap != null && !typeMap.isEmpty()) {
        sqlTypeMap.putAll(typeMap);
      } else {
        LOGGER.log(Level.CONFIG, "No type map available from database connection");
      }
    } catch (final Exception e) {
      // Catch all exceptions, since even though most JDBC drivers would throw SQLException, but
      // the Sybase Adaptive Server driver throws UnimplementedOperationException
      LOGGER.log(Level.WARNING, "Could not obtain data type map from connection", e);
    }
  }

  public TypeMap(final Map<String, Class<?>> sqlTypeMap) {
    if (sqlTypeMap == null) {
      this.sqlTypeMap = new HashMap<>();
    } else {
      this.sqlTypeMap = new HashMap<>(sqlTypeMap);
    }
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(final Object key) {
    return sqlTypeMap.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return sqlTypeMap.containsValue(value);
  }

  @Override
  public Set<Entry<String, Class<?>>> entrySet() {
    return new HashMap<>(sqlTypeMap).entrySet();
  }

  @Override
  public boolean equals(final Object o) {
    return sqlTypeMap.equals(o);
  }

  @Override
  public Class<?> get(final Object key) {
    if (containsKey(key)) {
      return sqlTypeMap.get(key);
    } else {
      return Object.class;
    }
  }

  @Override
  public int hashCode() {
    return sqlTypeMap.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return sqlTypeMap.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return new HashSet<>(sqlTypeMap.keySet());
  }

  @Override
  public Class<?> put(final String key, final Class<?> value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends String, ? extends Class<?>> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Class<?> remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return sqlTypeMap.size();
  }

  @Override
  public String toString() {
    final Map<String, String> typeClassNameMap =
        sqlTypeMap.entrySet().stream()
            .collect(toMap(Entry::getKey, e -> e.getValue().getCanonicalName()));
    return typeClassNameMap.toString();
  }

  @Override
  public Collection<Class<?>> values() {
    return new HashSet<>(sqlTypeMap.values());
  }
}
