/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;


import static java.sql.Types.BLOB;
import static java.sql.Types.CLOB;
import static java.sql.Types.LONGNVARCHAR;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCLOB;
import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.QueryUtility.executeAgainstSchema;
import static sf.util.DatabaseUtility.logSQLWarnings;
import static sf.util.IOUtility.readFully;
import static sf.util.Utility.enumValue;
import static sf.util.Utility.enumValueFromId;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.isIntegral;

import java.io.Reader;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.Query;
import schemacrawler.utility.BinaryData;
import sf.util.IdentifiedEnum;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A wrapper around a JDBC resultset obtained from a database metadata call.
 * This allows type-safe methods to obtain boolean, integer and string data,
 * while abstracting away the quirks of the JDBC metadata API.
 *
 * @author Sualeh Fatehi
 */
public final class MetadataResultSet
  implements AutoCloseable
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(MetadataResultSet.class.getName());

  private static final int FETCHSIZE = 20;

  private final ResultsColumns resultsColumns;
  private final ResultSet results;
  private String description;
  private Set<ResultsColumn> readColumns;
  private int rowCount;
  private boolean showLobs;

  public MetadataResultSet(final Query query,
                           final Statement statement,
                           final InclusionRule schemaInclusionRule)
    throws SQLException
  {
    this(executeAgainstSchema(query, statement, schemaInclusionRule));
    description = query.getName();
  }

  public MetadataResultSet(final ResultSet resultSet)
    throws SQLException
  {
    results = requireNonNull(resultSet, "Cannot use null results");
    try
    {
      results.setFetchSize(FETCHSIZE);
    }
    catch (final NullPointerException | SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not set fetch size", e);
    }

    resultsColumns = new ResultsCrawler(results).crawl();
    readColumns = new HashSet<>();
    showLobs = true;
  }

  public void setShowLobs(final boolean showLobs)
  {
    this.showLobs = showLobs;
  }

  public String[] getColumnNames()
  {
    final List<String> columnNames = new ArrayList<>();
    resultsColumns.forEach(resultsColumn -> columnNames.add(resultsColumn.getName()));
    return columnNames.toArray(new String[columnNames.size()]);
  }

  public List<Object> row()
    throws SQLException
  {
    final List<Object> currentRow = new ArrayList<>();
    for (final ResultsColumn resultsColumn : resultsColumns)
    {
      currentRow.add(getColumnData(resultsColumn));
    }

    return currentRow;
  }

  /**
   * Releases this <code>ResultSet</code> object's database and JDBC resources
   * immediately instead of waiting for this to happen when it is automatically
   * closed.
   *
   * @throws SQLException
   *   On an exception
   */
  @Override
  public void close()
    throws SQLException
  {
    results.close();

    if (LOGGER.isLoggable(Level.INFO) && !isBlank(description))
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Processed %d rows for <%s>",
                                  rowCount,
                                  description));
    }
  }

  /**
   * Gets unread (and therefore unmapped) columns from the database metadata
   * resultset, and makes them available as addiiotnal attributes.
   *
   * @return Map of additional attributes to the database object
   */
  public Map<String, Object> getAttributes()
  {
    final Map<String, Object> attributes = new HashMap<>();
    for (final ResultsColumn resultsColumn : resultsColumns)
    {
      if (!readColumns.contains(resultsColumn))
      {
        try
        {
          final Object value = getColumnData(resultsColumn);
          attributes.put(resultsColumn.getName(), value);
        }
        catch (final SQLException | ArrayIndexOutOfBoundsException e)
        {
          /*
           * MySQL connector is broken and can cause
           * ArrayIndexOutOfBoundsExceptions for no good reason (tested
           * with connector 5.1.26 and server version 5.0.95). Ignoring
           * the exception, we can still get some useful data out of the
           * database.
           */
          LOGGER.log(Level.WARNING,
                     new StringFormat("Could not read value for column <%s>",
                                      resultsColumn),
                     e);
        }
      }
    }
    return attributes;
  }

  public BigInteger getBigInteger(final String columnName)
  {
    String stringBigInteger = getString(columnName);
    if (isBlank(stringBigInteger))
    {
      return null;
    }
    stringBigInteger = stringBigInteger.replaceAll("[, ]", stringBigInteger);
    BigInteger value;
    try
    {
      value = new BigInteger(stringBigInteger);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not get big integer value", e);
      return null;
    }
    return value;
  }

  /**
   * Checks if the value of a column from the result set evaluates to true.
   *
   * @param columnName
   *   Column name to check
   * @return Whether the string evaluates to true
   */
  public boolean getBoolean(final String columnName)
  {
    if (useColumn(columnName))
    {
      try
      {
        final Object booleanValue = results.getObject(columnName);
        final String stringBooleanValue;
        if (results.wasNull() || booleanValue == null)
        {
          LOGGER.log(Level.FINER,
                     new StringFormat(
                       "NULL value for column <%s>, so evaluating to 'false'",
                       columnName));
          return false;
        }
        else
        {
          stringBooleanValue = String
            .valueOf(booleanValue)
            .trim();
        }

        if (isIntegral(stringBooleanValue))
        {
          return !stringBooleanValue.equals("0");
        }
        else
        {
          return stringBooleanValue.equalsIgnoreCase("yes")
                 || stringBooleanValue.equalsIgnoreCase("true");
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat(
                     "Could not read boolean value for column <%s>",
                     columnName),
                   e);
      }
    }
    return false;
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E>> E getEnum(final String columnName,
                                       final E defaultValue)
  {
    final String value = getString(columnName);
    if (isBlank(value))
    {
      return defaultValue;
    }
    return enumValue(value.toLowerCase(Locale.ENGLISH), defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E> & IdentifiedEnum> E getEnumFromId(final String columnName,
                                                              final E defaultValue)
  {
    final int value = getInt(columnName, 0);
    return enumValueFromId(value, defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E> & IdentifiedEnum> E getEnumFromShortId(final String columnName,
                                                                   final E defaultValue)
  {
    final int value = getShort(columnName, (short) 0);
    return enumValueFromId(value, defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an integer. If the value
   * was null, returns the default.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default value
   * @return Integer value of the column, or the default if not available
   */
  public int getInt(final String columnName, final int defaultValue)
  {
    int value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getInt(columnName);
        if (results.wasNull())
        {
          LOGGER.log(Level.FINER,
                     new StringFormat(
                       "NULL int value for column <%s>, so using default %d",
                       columnName,
                       defaultValue));
          value = defaultValue;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat(
                     "Could not read integer value for column <%s>",
                     columnName),
                   e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a long. If the value was
   * null, returns the default.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default value
   * @return Long value of the column, or the default if not available
   */
  public long getLong(final String columnName, final long defaultValue)
  {
    long value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getLong(columnName);
        if (results.wasNull())
        {
          LOGGER.log(Level.FINER,
                     new StringFormat(
                       "NULL long value for column <%s>, so using default %d",
                       columnName,
                       defaultValue));
          value = defaultValue;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat("Could not read long value for column <%s>",
                                    columnName),
                   e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a short. If the value
   * was null, returns the default.
   *
   * @param columnName
   *   Column name
   * @param defaultValue
   *   Default value
   * @return Short value of the column, or the default if not available
   */
  public short getShort(final String columnName, final short defaultValue)
  {
    short value = defaultValue;
    if (useColumn(columnName))
    {
      try
      {
        value = results.getShort(columnName);
        if (results.wasNull())
        {
          LOGGER.log(Level.FINER,
                     new StringFormat(
                       "NULL short value for column <%s>, so using default %d",
                       columnName,
                       defaultValue));
          value = defaultValue;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat("Could not read short value for column <%s>",
                                    columnName),
                   e);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a string.
   *
   * @param columnName
   *   Column name
   * @return String value of the column, or null if not available
   */
  public String getString(final String columnName)
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

        if (value != null)
        {
          value = value.trim();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   new StringFormat(
                     "Could not read string value for column <%s>",
                     columnName),
                   e);
      }
    }
    return value;
  }

  /**
   * Moves the cursor down one row from its current position. A
   * <code>ResultSet</code> cursor is initially positioned before the
   * first row; the first call to the method <code>next</code> makes the first
   * row the current row; the second call makes the second row the current row,
   * and so on.
   *
   * @return <code>true</code> if the new current row is valid;
   *   <code>false</code> if there are no more rows
   * @throws SQLException
   *   On a database access error
   */
  public boolean next()
    throws SQLException
  {
    readColumns = new HashSet<>();

    final boolean next = results.next();
    logSQLWarnings(results);
    if (next)
    {
      rowCount = rowCount + 1;
    }
    return next;
  }

  public void setDescription(final String description)
  {
    this.description = description;
  }

  private Object getColumnData(final ResultsColumn resultsColumn)
    throws SQLException
  {
    final int javaSqlType = resultsColumn
      .getColumnDataType()
      .getJavaSqlType()
      .getVendorTypeNumber();
    final int ordinalPosition = resultsColumn.getOrdinalPosition();

    Object columnData;

    switch (javaSqlType)
    {
      case BLOB:
      case LONGVARBINARY:
        // Do not read binary data - just determine if it is NULL
        final Object object = results.getObject(ordinalPosition);
        if (results.wasNull() || object == null)
        {
          columnData = null;
        }
        else
        {
          columnData = new BinaryData();
        }
        break;
      case CLOB:
      case NCLOB:
      case LONGNVARCHAR:
      case LONGVARCHAR:
        final Reader reader = results.getCharacterStream(ordinalPosition);
        if (results.wasNull() || reader == null)
        {
          columnData = null;
        }
        else
        {
          columnData = readCharacterData(reader);
        }
        break;
      default:
        columnData = results.getObject(ordinalPosition);
        if (results.wasNull())
        {
          columnData = null;
        }
        break;
    }
    return columnData;
  }

  private Object readCharacterData(final Reader reader)
  {
    if (reader != null && showLobs)
    {
      try
      {
        if (reader != null)
        {
          return readFully(reader);
        }
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING, "Could not read character data", e);
        return new BinaryData();
      }
    }
    return new BinaryData();
  }

  private boolean useColumn(final String columnName)
  {
    final Optional<ResultsColumn> optionalResultsColumn =
      resultsColumns.lookupColumn(columnName);
    optionalResultsColumn.ifPresent(readColumns::add);
    return optionalResultsColumn.isPresent();
  }

}
