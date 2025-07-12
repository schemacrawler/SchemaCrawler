/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.enumerated_data_type;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.server.postgresql.PostgreSQLEnumDataTypeHelper;
import schemacrawler.test.utility.DisableLogging;
import us.fatehi.test.utility.TestObjectUtility;

@DisableLogging
public class PostgreSQLEnumDataTypeHelperTest {

  @Test
  public void testGetEnumDataTypeInfo() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mockColumnDataType("enum_type", true);
    final Connection connection = TestObjectUtility.mockConnection();

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }

  @Test
  public void testGetEnumValues() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mockColumnDataType("enum_type", true);
    final Connection connection = mock(Connection.class);
    final Statement mockStatement = mock(Statement.class);
    final ResultSet mockResultSet =
        TestObjectUtility.mockResultSet(
            "PostgreSQL enum",
            new String[] {"TYPE_CATALOG", "TYPE_SCHEMA", "TYPE_NAME", "ENUM_LABEL"},
            new Object[][] {
              {null, "", columnDataType.getName(), "Moe"},
              {null, "", columnDataType.getName(), "Larry"},
              {null, "", columnDataType.getName(), "Curly"}
            });

    when(connection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.execute(anyString())).thenReturn(true);
    when(mockStatement.getResultSet()).thenReturn(mockResultSet);

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();

    EnumDataTypeInfo enumDataTypeInfo;
    List<String> enumValues;
    // Test column data type first time
    enumDataTypeInfo = helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(enumerated_data_type));

    enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(3));
    assertThat(enumValues, containsInAnyOrder("Moe", "Larry", "Curly"));

    // Test column data type second time
    enumDataTypeInfo = helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(enumerated_data_type));

    enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(3));
    assertThat(enumValues, containsInAnyOrder("Moe", "Larry", "Curly"));
  }

  @Test
  public void testSQLException() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mockColumnDataType("enum_type", true);
    final Connection connection = mock(Connection.class);

    when(connection.createStatement()).thenThrow(SQLException.class);

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }

  private ColumnDataType mockColumnDataType(
      final String columnDataTypeName, final boolean isEnumerated) {
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    when(columnDataType.getName()).thenReturn(columnDataTypeName);
    when(columnDataType.isEnumerated()).thenReturn(isEnumerated);
    return columnDataType;
  }
}
