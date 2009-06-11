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


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
      LOGGER.log(Level.FINE, "Could not get columns list");
    }
    this.resultSetColumns = Collections.unmodifiableSet(resultSetColumns);

    readColumns = new HashSet<String>();
  }

  /**
   * Set fetch size for results.
   * 
   * @param rows
   *        Number of rows to fetch
   * @throws SQLException
   *         On an exception
   */
  public void setFetchSize(final int rows)
    throws SQLException
  {
    results.setFetchSize(rows);
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

  /**
   * Gets unread (and therefore unmapped) columns from the database
   * metadata resultset, and makes them available as addiiotnal
   * attributes.
   * 
   * @return Map of additional attributes to the database object
   */
  Map<String, Object> getAttributes()
  {
    final Set<String> resultSetColumns = new HashSet<String>(this.resultSetColumns);
    // Get unused columns
    for (final Iterator<String> iterator = resultSetColumns.iterator(); iterator
      .hasNext();)
    {
      final String columnName = iterator.next();
      if (readColumns.contains(columnName))
      {
        iterator.remove();
      }
    }
    // Set attributes
    final Map<String, Object> attributes = new HashMap<String, Object>();
    for (final String columnName: resultSetColumns)
    {
      try
      {
        final Object value = results.getObject(columnName);
        attributes.put(columnName, value);
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read value for column "
                                  + columnName, e);
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
    String stringValue = null;
    if (useColumn(columnName))
    {
      try
      {
        stringValue = results.getString(columnName);
        if (!isBlank(stringValue))
        {
          try
          {
            final int booleanInt = Integer.parseInt(stringValue);
            value = booleanInt != 0;
          }
          catch (final NumberFormatException e)
          {
            value = stringValue.equalsIgnoreCase("YES")
                    || Boolean.valueOf(stringValue).booleanValue();
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
   * Reads the value of a column from the result set as an integer. If
   * the value was null, returns the default.
   * 
   * @param columnName
   *        Column name
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
   * @return Short value of the column, or the default if not available
   */
  String getString(final String columnName)
  {
    String value = null;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getString(columnName);
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

  private boolean isBlank(final String text)
  {
    return text == null || text.trim().length() == 0;
  }

  private boolean useColumn(final String columnName)
  {
    final boolean useColumn = !isBlank(columnName);
    if (useColumn)
    {
      readColumns.add(columnName);
    }
    return useColumn;
  }

}
