package schemacrawler.crawl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import sf.util.Utilities;

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
      final ResultsRetriever resultsRetriever = new ResultsRetriever(resultSet);
      final ResultsColumns resultColumns = resultsRetriever.retrieveResults();
      for (final ResultsColumn resultColumn: resultColumns.getColumns())
      {
        resultSetColumns.add(resultColumn.getName());
      }
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.FINE, "Could not get columns list");
    }
    this.resultSetColumns = Collections.unmodifiableSet(resultSetColumns);

    readColumns = new HashSet<String>();
  }

  public void setFetchSize(final int rows)
    throws SQLException
  {
    results.setFetchSize(rows);
  }

  void close()
    throws SQLException
  {
    results.close();
  }

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
    // Get attributes
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
        LOGGER.log(Level.FINE, "Could not read value for column " + columnName);
      }
    }
    return attributes;
  }

  /**
   * Checks if the text is true.
   * 
   * @param text
   *        Text to check.
   * @return Whether the string is true or yes.
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
        value = useColumn(stringValue) && stringValue.equalsIgnoreCase("YES")
                || Boolean.valueOf(stringValue).booleanValue();
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.FINE, "Could not read boolean value for column "
                               + columnName);
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
        LOGGER.log(Level.FINE, "Could not read integer value for column "
                               + columnName);
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
        LOGGER.log(Level.FINE, "Could not read long value for column "
                               + columnName);
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
        LOGGER.log(Level.FINE, "Could not read short value for column "
                               + columnName);
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
        LOGGER.log(Level.FINE, "Could not read string value for column "
                               + columnName);
      }
    }
    return value;
  }

  boolean next()
    throws SQLException
  {
    readColumns = new HashSet<String>();
    return results.next();
  }

  private boolean useColumn(final String columnName)
  {
    final boolean useColumn = !Utilities.isBlank(columnName);
    if (useColumn)
    {
      readColumns.add(columnName);
    }
    return useColumn;
  }

}
