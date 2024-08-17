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
import schemacrawler.test.utility.TestUtility;

public class PostgreSQLEnumDataTypeHelperTest {

  @Test
  public void testGetEnumDataTypeInfo() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);

    when(columnDataType.getName()).thenReturn("enum_type");
    when(connection.createStatement()).thenReturn(mock(java.sql.Statement.class));
    when(connection.createStatement().executeQuery(anyString()))
        .thenReturn(mock(java.sql.ResultSet.class));

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }

  @Test
  public void testGetEnumValues() throws Exception {
    final String columnDataTypeName = "enum_type";
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);
    final Statement mockStatement = mock(java.sql.Statement.class);
    final ResultSet mockResultSet =
        TestUtility.createMockResultSet(
            new String[] {"TYPE_CATALOG", "TYPE_SCHEMA", "TYPE_NAME", "ENUM_LABEL"},
            new Object[][] {
              {null, "", columnDataTypeName, "Moe"},
              {null, "", columnDataTypeName, "Larry"},
              {null, "", columnDataTypeName, "Curly"}
            });

    when(columnDataType.getName()).thenReturn(columnDataTypeName);
    when(connection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.execute(anyString())).thenReturn(true);
    when(mockStatement.getResultSet()).thenReturn(mockResultSet);

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(enumerated_data_type));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(3));
    assertThat(enumValues, containsInAnyOrder("Moe", "Larry", "Curly"));
  }

  @Test
  public void testSQLException() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);

    when(columnDataType.getName()).thenReturn("enum_type");
    when(connection.createStatement()).thenThrow(SQLException.class);

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
  }
}
