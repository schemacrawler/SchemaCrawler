/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

/**
 * A wrapper around a JDBC resultset obtained from a database metadata
 * call. This allows type-safe methods to obtain boolean, integer and
 * string data, while abstracting away the quirks of the JDBC metadata
 * API.
 * 
 * @author Sualeh Fatehi
 */
final class MetadataResultSet
{

  private static final Logger LOGGER = Logger.getLogger(MetadataResultSet.class
    .getName());

  private static final int FETCHSIZE = 20;

  private final ResultSet results;
  private final Set<String> resultSetColumns;
  private Set<String> readColumns;

  MetadataResultSet(final ResultSet resultSet)
  {
    if (resultSet == null)
    {
      throw new IllegalArgumentException("Cannot use null results");
    }
    results = resultSet;
    try
    {
      results.setFetchSize(FETCHSIZE);
    }
    catch (final NullPointerException e)
    {
      // Need this catch for the JDBC/ ODBC driver
      LOGGER.log(Level.WARNING, "Could not set fetch size", e);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not set fetch size", e);
    }

    final Set<String> resultSetColumns = new HashSet<String>();
    try
    {
      final ResultSetMetaData rsMetaData = resultSet.getMetaData();
      for (int i = 0; i < rsMetaData.getColumnCount(); i++)
      {
        resultSetColumns.add(rsMetaData.getColumnName(i + 1));
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not get columns list");
    }
    this.resultSetColumns = Collections.unmodifiableSet(resultSetColumns);

    readColumns = new HashSet<String>();
  }

  private boolean useColumn(final String columnName)
  {
    final boolean useColumn = columnName != null;
    if (useColumn)
    {
      readColumns.add(columnName);
    }
    return useColumn;
  }

  /**
   * Releases this <code>ResultSet</code> object's database and JDBC
   * resources immediately instead of waiting for this to happen when it
   * is automatically closed.
   * 
   * @throws SQLException
   *         On an exception
   */
  void close()
    throws SQLException
  {
    results.close();
  }

  String currentRowToString()
  {
    final Map<String, String> currentRow = new HashMap<String, String>();
    for (final String columnName: resultSetColumns)
    {
      Object columnData;
      try
      {
        columnData = results.getObject(columnName);
      }
      catch (final SQLException e)
      {
        columnData = null;
      }
      currentRow.put(columnName, String.valueOf(columnData));
    }
    return currentRow.toString();
  }

  /**
   * Gets unread (and therefore unmapped) columns from the database
   * metadata resultset, and makes them available as addiiotnal
   * attributes.
   * 
   * @return Map of additional attributes to the database object
   */
  Map<String, Object> getAttributes()
  {
    final Set<String> unusedResultSetColumns = new HashSet<String>(resultSetColumns);
    // Retain unused columns
    for (final String readColumn: readColumns)
    {
      unusedResultSetColumns.remove(readColumn);
    }
    // Set attributes
    final Map<String, Object> attributes = new HashMap<String, Object>();
    for (final String unusedColumnName: unusedResultSetColumns)
    {
      try
      {
        final Object value = results.getObject(unusedColumnName);
        attributes.put(unusedColumnName, value);
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read value for column "
                                  + unusedColumnName, e);
      }
    }
    return attributes;
  }

  /**
   * Checks if the value of a column from the result set evaluates to
   * true.
   * 
   * @param columnName
   *        Column name to check
   * @return Whether the string evaluates to true
   */
  boolean getBoolean(final String columnName)
  {
    boolean value = false;
    if (useColumn(columnName))
    {
      try
      {
        final Object booleanValue = results.getObject(columnName);
        final String stringBooleanValue;
        if (results.wasNull() || booleanValue == null)
        {
          stringBooleanValue = null;
        }
        else
        {
          stringBooleanValue = String.valueOf(booleanValue);
        }
        if (!Utility.isBlank(stringBooleanValue))
        {
          try
          {
            final int booleanInt = Integer.parseInt(stringBooleanValue);
            value = booleanInt != 0;
          }
          catch (final NumberFormatException e)
          {
            value = stringBooleanValue.equalsIgnoreCase("YES")
                    || Boolean.valueOf(stringBooleanValue).booleanValue();
          }
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read boolean value for column "
                                  + columnName, e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as an enum.
   * 
   * @param columnName
   *        Column name
   * @param defaultValue
   *        Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  <T extends Enum<T>> T getEnum(final String columnName, final T defaultValue)
  {
    final String value = getString(columnName);
    T enumValue;
    if (value == null || defaultValue == null)
    {
      enumValue = defaultValue;
    }
    else
    {
      try
      {
        enumValue = (T) defaultValue.valueOf(defaultValue.getClass(), value
          .toLowerCase(Locale.ENGLISH));
      }
      catch (final Exception e)
      {
        enumValue = defaultValue;
      }
    }
    return enumValue;
  }

  /**
   * Reads the value of a column from the result set as an integer. If
   * the value was null, returns the default.
   * 
   * @param columnName
   *        Column name
   * @param defaultValue
   *        Default value
   * @return Integer value of the column, or the default if not
   *         available
   */
  int getInt(final String columnName, final int defaultValue)
  {
    int value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getInt(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read integer value for column "
                                  + columnName, e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a long. If the
   * value was null, returns the default.
   * 
   * @param columnName
   *        Column name
   * @param defaultValue
   *        Default value
   * @return Long value of the column, or the default if not available
   */
  long getLong(final String columnName, final long defaultValue)
  {
    long value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getLong(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read long value for column "
                                  + columnName, e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a short. If the
   * value was null, returns the default.
   * 
   * @param columnName
   *        Column name
   * @param defaultValue
   *        Default value
   * @return Short value of the column, or the default if not available
   */
  short getShort(final String columnName, final short defaultValue)
  {
    short value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getShort(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read short value for column "
                                  + columnName, e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a string.
   * 
   * @param columnName
   *        Column name
   * @return String value of the column, or null if not available
   */
  String getString(final String columnName)
  {
    String value = null;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getString(columnName);
        if (results.wasNull())
        {
          value = null;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read string value for column "
                                  + columnName, e);
      }
    }
    return value;
  }

  /**
   * Moves the cursor down one row from its current position. A
   * <code>ResultSet</code> cursor is initially positioned before the
   * first row; the first call to the method <code>next</code> makes the
   * first row the current row; the second call makes the second row the
   * current row, and so on.
   * 
   * @return <code>true</code> if the new current row is valid;
   *         <code>false</code> if there are no more rows
   * @throws SQLException
   *         On a database access error
   */
  boolean next()
    throws SQLException
  {
    readColumns = new HashSet<String>();
    return results.next();
  }

}
