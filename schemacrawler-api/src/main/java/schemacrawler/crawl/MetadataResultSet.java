/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.sql.Types.BLOB;
import static java.sql.Types.CLOB;
import static java.sql.Types.LONGNVARCHAR;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCLOB;
import static schemacrawler.schemacrawler.QueryUtility.executeAgainstSchema;
import static schemacrawler.utility.EnumUtility.enumValue;
import static schemacrawler.utility.EnumUtility.enumValueFromId;
import static us.fatehi.utility.IOUtility.readFully;
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
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.isIntegral;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.IdentifiedEnum;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.Query;
import schemacrawler.utility.BinaryData;
import us.fatehi.utility.UtilityLogger;
import us.fatehi.utility.string.StringFormat;

/**
 * A wrapper around a JDBC ResultSet obtained from a database metadata call. This allows type-safe
 * methods to obtain boolean, integer and string data, while abstracting away the quirks of the JDBC
 * metadata API.
 */
public final class MetadataResultSet implements AutoCloseable {

  private static final Logger LOGGER = Logger.getLogger(MetadataResultSet.class.getName());

  private static final int FETCHSIZE = 20;

  private final ResultsColumns resultsColumns;
  private final ResultSet results;
  private final String description;
  private Set<ResultsColumn> readColumns;
  private int rowCount;
  private boolean showLobs;
  private int maxRows;

  public MetadataResultSet(
      final Query query, final Statement statement, final Map<String, InclusionRule> limitMap)
      throws SQLException {
    this(executeAgainstSchema(query, statement, limitMap), query.getName());
  }

  public MetadataResultSet(final ResultSet resultSet, final String description)
      throws SQLException {
    results = requireNonNull(resultSet, "Cannot use null results");
    try {
      results.setFetchSize(FETCHSIZE);
    } catch (final NullPointerException | SQLException e) {
      LOGGER.log(Level.WARNING, "Could not set fetch size", e);
    }
    this.description = requireNotBlank(description, "No result-set description provided");

    resultsColumns = new ResultsCrawler(results).crawl();
    readColumns = new HashSet<>();
    showLobs = true;
    maxRows = Integer.MAX_VALUE;
  }

  /**
   * Releases this <code>ResultSet</code> object's database and JDBC resources immediately instead
   * of waiting for this to happen when it is automatically closed.
   *
   * @throws SQLException On an exception
   */
  @Override
  public void close() throws SQLException {
    results.close();
    LOGGER.log(Level.FINE, new StringFormat("Processed %d rows for <%s>", rowCount, description));
  }

  /**
   * Gets unread (and therefore unmapped) columns from the database metadata result-set, and makes
   * them available as additional attributes.
   *
   * @return Map of additional attributes to the database object
   */
  public Map<String, Object> getAttributes() {
    final Map<String, Object> attributes = new HashMap<>();
    for (final ResultsColumn resultsColumn : resultsColumns) {
      if (!readColumns.contains(resultsColumn)) {
        try {
          final String key = resultsColumn.getLabel().toUpperCase();
          final Object value = getColumnData(resultsColumn);
          attributes.put(key, value);
        } catch (final SQLException | ArrayIndexOutOfBoundsException e) {
          /*
           * MySQL connector is broken and can cause ArrayIndexOutOfBoundsExceptions for no good
           * reason (tested with connector 5.1.26 and server version 5.0.95). Ignoring the
           * exception, we can still get some useful data out of the database.
           */
          LOGGER.log(
              Level.WARNING,
              e,
              new StringFormat("Could not read value for column <%s>", resultsColumn));
        }
      }
    }
    return attributes;
  }

  public BigInteger getBigInteger(final String columnName) {
    String stringBigInteger = getString(columnName);
    if (isBlank(stringBigInteger)) {
      return null;
    }
    stringBigInteger = stringBigInteger.replaceAll("[, ]", stringBigInteger);
    BigInteger value;
    try {
      value = new BigInteger(stringBigInteger);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not get big integer value", e);
      return null;
    }
    return value;
  }

  /**
   * Checks if the value of a column from the result set evaluates to true.
   *
   * @param columnName Column name to check
   * @return Whether the string evaluates to true
   */
  public boolean getBoolean(final String columnName) {
    if (useColumn(columnName)) {
      try {
        final Object booleanValue = results.getObject(columnName);
        final String stringBooleanValue;
        if (results.wasNull() || booleanValue == null) {
          LOGGER.log(
              Level.FINER,
              new StringFormat("NULL value for column <%s>, so evaluating to 'false'", columnName));
          return false;
        }
        stringBooleanValue = String.valueOf(booleanValue).trim();

        if (isIntegral(stringBooleanValue)) {
          return !"0".equals(stringBooleanValue);
        }
        return "yes".equalsIgnoreCase(stringBooleanValue)
            || "true".equalsIgnoreCase(stringBooleanValue);
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not read boolean value for column <%s>", columnName));
      }
    }
    return false;
  }

  public String[] getColumnNames() {
    final List<String> columnNames = new ArrayList<>();
    resultsColumns.forEach(resultsColumn -> columnNames.add(resultsColumn.getName()));
    return columnNames.toArray(new String[0]);
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName Column name
   * @param defaultValue Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E>> E getEnum(final String columnName, final E defaultValue) {
    requireNotBlank(columnName, "No column name provided");
    requireNonNull(defaultValue, "No default value provided");
    final String value = getString(columnName);
    if (isBlank(value)) {
      return defaultValue;
    }
    return enumValue(value.toLowerCase(Locale.ENGLISH), defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName Column name
   * @param defaultValue Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E> & IdentifiedEnum> E getEnumFromId(
      final String columnName, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");
    final int value = getInt(columnName, defaultValue.id());
    return enumValueFromId(value, defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an enum.
   *
   * @param columnName Column name
   * @param defaultValue Default enum value to return
   * @return Enum value of the column, or the default if not available
   */
  public <E extends Enum<E> & IdentifiedEnum> E getEnumFromShortId(
      final String columnName, final E defaultValue) {
    requireNonNull(defaultValue, "No default value provided");
    final int value = getShort(columnName, (short) defaultValue.id());
    return enumValueFromId(value, defaultValue);
  }

  /**
   * Reads the value of a column from the result set as an integer. If the value was null, returns
   * the default.
   *
   * @param columnName Column name
   * @param defaultValue Default value
   * @return Integer value of the column, or the default if not available
   */
  public int getInt(final String columnName, final int defaultValue) {
    int value = defaultValue;
    if (useColumn(columnName)) {
      try {
        value = results.getInt(columnName);
        if (results.wasNull()) {
          LOGGER.log(
              Level.FINER,
              new StringFormat(
                  "NULL int value for column <%s>, so using default %d", columnName, defaultValue));
          value = defaultValue;
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not read integer value for column <%s>", columnName));
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a long. If the value was null, returns the
   * default.
   *
   * @param columnName Column name
   * @param defaultValue Default value
   * @return Long value of the column, or the default if not available
   */
  public long getLong(final String columnName, final long defaultValue) {
    long value = defaultValue;
    if (useColumn(columnName)) {
      try {
        value = results.getLong(columnName);
        if (results.wasNull()) {
          LOGGER.log(
              Level.FINER,
              new StringFormat(
                  "NULL long value for column <%s>, so using default %d",
                  columnName, defaultValue));
          value = defaultValue;
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not read long value for column <%s>", columnName));
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a short. If the value was null, returns the
   * default.
   *
   * @param columnName Column name
   * @param defaultValue Default value
   * @return Short value of the column, or the default if not available
   */
  public short getShort(final String columnName, final short defaultValue) {
    short value = defaultValue;
    if (useColumn(columnName)) {
      try {
        value = results.getShort(columnName);
        if (results.wasNull()) {
          LOGGER.log(
              Level.FINER,
              new StringFormat(
                  "NULL short value for column <%s>, so using default %d",
                  columnName, defaultValue));
          value = defaultValue;
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not read short value for column <%s>", columnName));
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a string.
   *
   * @param columnName Column name
   * @return String value of the column, or null if not available
   */
  public String getString(final String columnName) {
    String value = null;
    if (useColumn(columnName)) {
      try {
        value = results.getString(columnName);
        if (results.wasNull()) {
          value = null;
        }

        if (value != null) {
          value = value.trim();
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not read string value for column <%s>", columnName));
      }
    }
    return value;
  }

  /**
   * Moves the cursor down one row from its current position. A <code>ResultSet</code> cursor is
   * initially positioned before the first row; the first call to the method <code>next</code> makes
   * the first row the current row; the second call makes the second row the current row, and so on.
   *
   * @return <code>true</code> if the new current row is valid; <code>false</code> if there are no
   *     more rows
   * @throws SQLException On a database access error
   */
  public boolean next() throws SQLException {
    if (rowCount == maxRows) {
      return false;
    }

    readColumns = new HashSet<>();

    final boolean next = results.next();
    new UtilityLogger(LOGGER).logSQLWarnings(results);
    if (next) {
      rowCount = rowCount + 1;
    }
    return next;
  }

  public void resetMaxRows() {
    maxRows = Integer.MAX_VALUE;
  }

  public List<Object> row() throws SQLException {
    final List<Object> currentRow = new ArrayList<>();
    for (final ResultsColumn resultsColumn : resultsColumns) {
      currentRow.add(getColumnData(resultsColumn));
    }

    return currentRow;
  }

  public void setMaxRows(final int maxRows) {
    if (maxRows < 0) {
      return;
    }
    this.maxRows = maxRows;
  }

  public void setShowLobs(final boolean showLobs) {
    this.showLobs = showLobs;
  }

  private Object getColumnData(final ResultsColumn resultsColumn) throws SQLException {
    final int javaSqlType =
        resultsColumn.getColumnDataType().getJavaSqlType().getVendorTypeNumber();
    final int ordinalPosition = resultsColumn.getOrdinalPosition();

    Object columnData;

    switch (javaSqlType) {
      case BLOB:
      case LONGVARBINARY:
        // Do not read binary data - just determine if it is NULL
        final Object object = results.getObject(ordinalPosition);
        if (results.wasNull() || object == null) {
          columnData = null;
        } else {
          columnData = new BinaryData();
        }
        break;
      case CLOB:
      case NCLOB:
      case LONGNVARCHAR:
      case LONGVARCHAR:
        final Reader reader = results.getCharacterStream(ordinalPosition);
        if (results.wasNull() || reader == null) {
          columnData = null;
        } else {
          columnData = readCharacterData(reader);
        }
        break;
      default:
        columnData = results.getObject(ordinalPosition);
        if (results.wasNull()) {
          columnData = null;
        }
        break;
    }
    return columnData;
  }

  private Object readCharacterData(final Reader reader) {
    try {
      if (reader != null && showLobs) {
        return readFully(reader);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read character data", e);
    }

    return new BinaryData();
  }

  private boolean useColumn(final String columnName) {
    final Optional<ResultsColumn> optionalResultsColumn = resultsColumns.lookupColumn(columnName);
    optionalResultsColumn.ifPresent(readColumns::add);
    return optionalResultsColumn.isPresent();
  }
}
