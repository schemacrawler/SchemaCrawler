package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.server.postgresql.PostgreSQLEnumDataTypeHelper;

public class PostgreSQLEnumDataTypeHelperTest {

  @Test
  public void testGetEnumDataTypeInfo() throws Exception {
    final Column column = mock(Column.class);
    final ColumnDataType columnDataType = mock(ColumnDataType.class);
    final Connection connection = mock(Connection.class);

    when(columnDataType.getName()).thenReturn("enum_type");
    when(connection.createStatement()).thenReturn(mock(java.sql.Statement.class));
    when(connection.createStatement().executeQuery(Mockito.anyString()))
        .thenReturn(mock(java.sql.ResultSet.class));

    final PostgreSQLEnumDataTypeHelper helper = new PostgreSQLEnumDataTypeHelper();
    final EnumDataTypeInfo enumDataTypeInfo =
        helper.getEnumDataTypeInfo(column, columnDataType, connection);

    assertThat(enumDataTypeInfo.getType(), is(not_enumerated));

    final List<String> enumValues = enumDataTypeInfo.getEnumValues();

    assertThat(enumValues.size(), is(0));
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
