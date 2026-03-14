/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.enumerated_data_type;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated;
import static schemacrawler.test.utility.crawl.LightColumnDataTypeUtility.enumColumnDataType;
import static us.fatehi.test.utility.TestObjectUtility.mockConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.server.postgresql.PostgreSQLEnumDataTypeHelper;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.crawl.LightTable;
import us.fatehi.test.utility.TestObjectUtility.Results;

@DisableLogging
public class PostgreSQLEnumDataTypeHelperTest {

  @Test
  public void testGetEnumDataTypeInfo() throws Exception {
    final Column column = new LightTable("table").addColumn("column");
    final ColumnDataType columnDataType = enumColumnDataType();
    final Connection connection = mockConnection();

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }

  @Test
  public void testGetEnumValues() throws Exception {
    final Column column = new LightTable("table").addColumn("column");
    final ColumnDataType columnDataType = enumColumnDataType();
    final Connection connection =
        mockConnection(
            new Results(
                new String[] {"TYPE_CATALOG", "TYPE_SCHEMA", "TYPE_NAME", "ENUM_LABEL"},
                new Object[][] {
                  {null, "", columnDataType.getName(), "Moe"},
                  {null, "", columnDataType.getName(), "Larry"},
                  {null, "", columnDataType.getName(), "Curly"}
                }));

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
    final Column column = new LightTable("table").addColumn("column");
    final ColumnDataType columnDataType = enumColumnDataType();
    final Connection connection = mockConnection();
    when(connection.createStatement()).thenThrow(SQLException.class);

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }
}
