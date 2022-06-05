/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.BinaryData;
import us.fatehi.utility.database.DatabaseUtility;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataResultSetTest {

  @Test
  @DisplayName("Retrieve bad values from results")
  public void badValues(final Connection connection) throws Exception {

    final String columnName = "COLUMN1";

    try (final Statement statement = connection.createStatement(); ) {

      statement.execute("DROP TABLE IF EXISTS TABLE1");
      statement.execute("CREATE TABLE TABLE1(COLUMN1 VARCHAR(2))");
      statement.execute("INSERT INTO TABLE1(COLUMN1) VALUES('A')");

      try (final MetadataResultSet results =
          new MetadataResultSet(
              DatabaseUtility.executeSql(statement, "SELECT * FROM TABLE1"), "badValues")) {
        while (results.next()) {
          final int value1 = results.getInt(columnName, 0);
          assertThat(value1, is(0));
          final long value2 = results.getLong(columnName, 0L);
          assertThat(value2, is(0L));
          final short value3 = results.getShort(columnName, (short) 0);
          assertThat(value3, is((short) 0));
          final BigInteger value4 = results.getBigInteger(columnName);
          assertThat(value4, is(nullValue()));
          final String value5 = results.getString(columnName);
          assertThat(value5, is("A"));
          final boolean value6 = results.getBoolean(columnName);
          assertThat(value6, is(false));
          final DayOfWeek value7 = results.getEnum(columnName, DayOfWeek.MONDAY);
          assertThat(value7, is(DayOfWeek.MONDAY));

          final List<Object> row = results.row();
          assertThat(row, hasSize(1));
          assertThat(row.get(0), is("A"));

          final String[] columnNames = results.getColumnNames();
          assertThat(columnNames, arrayWithSize(1));
          assertThat(columnNames[0], is(columnName));
        }
      }

    } finally {
      DatabaseUtility.executeSql(connection.createStatement(), "DROP TABLE IF EXISTS TABLE1");
    }
  }

  @Test
  @DisplayName("Retrieve boolean values from results")
  public void booleanValues(final Connection connection) throws Exception {

    final String column1Name = "COLUMN1";

    try (final Statement statement = connection.createStatement(); ) {
      for (final String value : Arrays.asList("1", "TRUE", "True", "true", "YES", "Yes", "yes")) {
        final String sql =
            String.format("SELECT '%s' AS " + column1Name + " FROM (VALUES(0))", value);
        try (final MetadataResultSet results =
            new MetadataResultSet(
                DatabaseUtility.executeSql(statement, sql), "booleanValues-true")) {
          while (results.next()) {
            final boolean booleanValue = results.getBoolean(column1Name);
            assertThat(
                String.format("Incorrect boolean value for '%s'", value), booleanValue, is(true));
          }
        }
      }

      for (final String value :
          Arrays.asList("0", "FALSE", "False", "false", "NO", "No", "no", "", "unknown")) {
        final String sql =
            String.format("SELECT '%s' AS " + column1Name + " FROM (VALUES(0))", value);
        try (final MetadataResultSet results =
            new MetadataResultSet(
                DatabaseUtility.executeSql(statement, sql), "booleanValues-false")) {
          while (results.next()) {
            final boolean booleanValue = results.getBoolean(column1Name);
            assertThat(
                String.format("Incorrect boolean value for '%s'", value), booleanValue, is(false));
          }
        }
      }
    }
  }

  @Test
  @DisplayName("Retrieve large object values from results")
  public void largeObjectValues(final Connection connection) throws Exception {

    final String columnName = "COLUMN1";

    final BiConsumer<String, ResultSet> assertAll =
        (dataType, resultSet) -> {
          try {
            final MetadataResultSet results = new MetadataResultSet(resultSet, "largeObjectValues");

            final String stringValue = results.getString(columnName);
            if (dataType.equals("BLOB")) {
              // BLOBs are not read
              assertThat(stringValue, is(nullValue()));
            } else if (dataType.contains("BINARY")) {
              assertThat(stringValue, is("41"));
            } else {
              assertThat(stringValue, is("A"));
            }

            final List<Object> row = results.row();
            assertThat(row, hasSize(1));
            final Object objectValue = row.get(0);
            if (dataType.equals("BLOB")) {
              // BLOBs are not read
              assertThat(String.valueOf(objectValue), is(new BinaryData().toString()));
            } else if (dataType.contains("BINARY")) {
              assertThat(objectValue, is(new byte[] {65}));
            } else {
              assertThat(objectValue, is("A"));
            }

            final String[] columnNames = results.getColumnNames();
            assertThat(columnNames, arrayWithSize(1));
            assertThat(columnNames[0], is(columnName));

            final ResultsColumns resultsColumns = new ResultsCrawler(resultSet).crawl();
            final ColumnDataType columnDataType =
                resultsColumns.getColumns().get(0).getColumnDataType();
            assertThat(dataType, containsString(columnDataType.getName()));

          } catch (final SQLException e) {
            fail(e);
          }
        };

    try (final Statement statement = connection.createStatement(); ) {
      for (final String dataType :
          Arrays.asList(
              "CHARACTER(1) ", "VARCHAR(1)", "CLOB", "BINARY(1)", "VARBINARY(1)", "BLOB")) {

        statement.execute("DROP TABLE IF EXISTS TABLE1");
        statement.execute(String.format("CREATE TABLE TABLE1(COLUMN1 %s)", dataType));

        if (dataType.contains("BINARY") || dataType.equals("BLOB")) {
          final PreparedStatement preparedStatement =
              connection.prepareStatement("INSERT INTO TABLE1(COLUMN1) VALUES(?)");
          preparedStatement.setBinaryStream(1, new ByteArrayInputStream("A".getBytes("UTF-8")));
          preparedStatement.execute();
        } else {
          statement.execute("INSERT INTO TABLE1(COLUMN1) VALUES('A')");
        }

        final String sql = "SELECT * FROM TABLE1";
        try (final ResultSet results = DatabaseUtility.executeSql(statement, sql)) {
          while (results.next()) {
            assertAll.accept(dataType, results);
          }
        }
      }
    } finally {
      DatabaseUtility.executeSql(connection.createStatement(), "DROP TABLE IF EXISTS TABLE1");
    }
  }

  @Test
  @DisplayName("Retrieve null values from results")
  public void nullValues(final Connection connection) throws Exception {

    final String column1Name = "COLUMN1";
    final String column2Name = "COLUMN2";

    final BiConsumer<String, MetadataResultSet> asserts =
        (columnName, results) -> {
          final int value1 = results.getInt(columnName, 0);
          assertThat(value1, is(0));
          final long value2 = results.getLong(columnName, 0L);
          assertThat(value2, is(0L));
          final short value3 = results.getShort(columnName, (short) 0);
          assertThat(value3, is((short) 0));
          final BigInteger value4 = results.getBigInteger(columnName);
          assertThat(value4, is(nullValue()));
          final String value5 = results.getString(columnName);
          assertThat(value5, is(nullValue()));
          final boolean value6 = results.getBoolean(columnName);
          assertThat(value6, is(false));
          final DayOfWeek value7 = results.getEnum(columnName, DayOfWeek.MONDAY);
          assertThat(value7, is(DayOfWeek.MONDAY));
        };

    final BiConsumer<String, ResultSet> assertAll =
        (dataType, resultSet) -> {
          try {

            final MetadataResultSet results = new MetadataResultSet(resultSet, "nullValues");

            asserts.accept(column1Name, results);
            asserts.accept(column2Name, results);

            final List<Object> row = results.row();
            assertThat(row, hasSize(1));
            assertThat(row.get(0), is(nullValue()));

            final String[] columnNames = results.getColumnNames();
            assertThat(columnNames, arrayWithSize(1));
            assertThat(columnNames[0], is(column1Name));

            final ResultsColumns resultsColumns = new ResultsCrawler(resultSet).crawl();
            final ColumnDataType columnDataType =
                resultsColumns.getColumns().get(0).getColumnDataType();
            assertThat(dataType, containsString(columnDataType.getName()));

          } catch (final SQLException e) {
            fail(e);
          }
        };

    try (final Statement statement = connection.createStatement(); ) {
      for (final String dataType :
          Arrays.asList(
              "BIT(1)",
              "DECIMAL",
              "INTEGER",
              "SMALLINT",
              "BIGINT",
              "DOUBLE",
              "DATE",
              "TIME",
              "TIMESTAMP",
              "CHARACTER(1) ",
              "VARCHAR(1)",
              "LONGVARCHAR",
              "BINARY(1)",
              "VARBINARY(1)",
              "LONGVARBINARY")) {
        final String sql =
            String.format(
                "SELECT CAST(NULL AS %s) AS " + column1Name + " FROM (VALUES(0))", dataType);
        try (final ResultSet results = DatabaseUtility.executeSql(statement, sql)) {
          while (results.next()) {
            assertAll.accept(dataType, results);
          }
        }
      }
    }
  }
}
