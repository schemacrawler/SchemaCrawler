/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.enumerated_column;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated;

import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.server.mysql.MySQLEnumDataTypeHelper;

public class MySQLEnumDataTypeHelperTest {

  @Test
  public void testGetEnumDataTypeInfo() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);

    when(column.getAttribute("COLUMN_TYPE")).thenReturn("enum('Moe','Larry','Curly')");

    final MySQLEnumDataTypeHelper helper = new MySQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(enumerated_column));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(3));
    assertThat(enumValues.get(0), is("Moe"));
    assertThat(enumValues.get(1), is("Larry"));
    assertThat(enumValues.get(2), is("Curly"));
  }

  @Test
  public void testBadEnumDataType() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);

    for (final String badValue :
        new String[] {
          null,
          "",
          "some-random-stuff",
          "enum()",
          "enum(,,)",
          "enum(abc)",
          "enum('abc)",
          "enum(  )",
          "enum(')"
        }) {
      when(column.getAttribute("COLUMN_TYPE")).thenReturn(badValue);

      final MySQLEnumDataTypeHelper helper = new MySQLEnumDataTypeHelper();
      final EnumDataTypeInfo enumDataTypeInfo =
          helper.getEnumDataTypeInfo(column, columnDataType, connection);

      assertThat(
          "Failure COLUMN_TYPE=%s".formatted(badValue),
          enumDataTypeInfo.getType(),
          is(not_enumerated));

      final List<String> enumValues = enumDataTypeInfo.getEnumValues();

      assertThat(enumValues.size(), is(0));
    }
  }
}
