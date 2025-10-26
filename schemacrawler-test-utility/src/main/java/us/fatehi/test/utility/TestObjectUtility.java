/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.AccessMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestObjectUtility {

  private static final class ResultSetInvocationHandler implements InvocationHandler {

    private final String resultSetDescription;
    private final Object[][] data;
    private final String[] columnNames;
    private int rowIndex;
    private boolean wasNull;
    private ResultSetMetaData rsmd;

    private ResultSetInvocationHandler(
        final String resultSetDescription, final Object[][] data, final String[] columnNames)
        throws SQLException {
      this.resultSetDescription =
          requireNonNull(resultSetDescription, "No result set description provided");
      this.data = data;
      this.columnNames = requireNonNull(columnNames, "No column names provided");
      rsmd = createResultSetMetaData();
      rowIndex = -1;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Throwable {
      // Result set
      final String methodName = method.getName();
      switch (methodName) {
        case "close":
          return null;
        case "getMetaData":
          return rsmd;
        case "next":
          wasNull = false;
          if (data == null) {
            return false;
          }
          rowIndex = rowIndex + 1;
          return rowIndex < data.length;
        case "getColumnName":
        case "getColumnLabel":
          if (args[0] instanceof Integer index) {
            return columnNames[index];
          }
          return "columnName";
        case "getCatalogName":
          return "catalogName";
        case "getSchemaName":
          return "schemaName";
        case "getTableName":
          return "tableName";
        case "getObject":
        case "getString":
        case "getInt":
        case "getShort":
          wasNull = false;
          int index = -1;
          if (args[0] instanceof Integer integer) {
            index = integer - 1;
          }
          if (args[0] instanceof String columnName) {
            index = Arrays.asList(columnNames).indexOf(columnName);
          }
          Object columnData;
          if (data == null || rowIndex < 0 || index < 0) {
            columnData = null;
          } else {
            columnData = data[rowIndex][index];
          }
          if (columnData == null) {
            wasNull = true;
            if ("getInt".equals(methodName) || "getShort".equals(methodName)) {
              throw new SQLException("Cannot convert <null> to an integer".formatted());
            }
            return null;
          }
          if (methodName != null) {
            switch (methodName) {
              case "getObject":
                return columnData;
              case "getString":
                return String.valueOf(columnData);
              case "getInt":
                return ((Number) columnData).intValue();
              case "getShort":
                return ((Number) columnData).shortValue();
              default:
                break;
            }
          }
        case "setFetchSize":
          return null;
        case "wasNull":
          return wasNull;
        case "toString":
          return "ResultSet: " + resultSetDescription;
        default:
          fail("%s(%s)".formatted(method, args));
          return null;
      }
    }

    private ResultSetMetaData createResultSetMetaData() throws SQLException {
      final ResultSetMetaData rsmd = mock(ResultSetMetaData.class);
      lenient().when(rsmd.getColumnCount()).thenReturn(columnNames.length);
      for (int i = 0; i < columnNames.length; i++) {
        lenient().when(rsmd.getColumnName(i + 1)).thenReturn(columnNames[i]);
        lenient().when(rsmd.getColumnLabel(i + 1)).thenReturn(columnNames[i]);
      }
      lenient().when(rsmd.toString()).thenReturn("ResultSetMetaData: " + resultSetDescription);
      return rsmd;
    }
  }

  public static Map<String, Object> fakeObjectMapFor(final Class<?> clazz) {
    final Map<String, Object> fakeObjectMap = new HashMap<>();
    fakeObjectMap.put("@object", clazz.getName());
    return fakeObjectMap;
  }

  public static TestObject makeTestObject() {
    final TestObject testObject1 = new TestObject();
    testObject1.setPlainString("hello world");
    testObject1.setPrimitiveInt(99);
    testObject1.setPrimitiveDouble(99.99);
    testObject1.setPrimitiveBoolean(true);
    testObject1.setPrimitiveArray(new int[] {1, 1, 2, 3, 5, 8});
    testObject1.setPrimitiveEnum(AccessMode.READ);
    testObject1.setObjectArray(new String[] {"a", "b", "c"});
    testObject1.setIntegerList(List.of(1, 1, 2, 3, 5, 8));
    final HashMap<Integer, String> map = new HashMap<>();
    map.put(1, "a");
    map.put(2, "b");
    map.put(3, "c");
    testObject1.setMap(map);
    final TestObject testObject = testObject1;
    return testObject;
  }

  public static Map<String, Object> makeTestObjectMap() {

    final TestObject testObject = makeTestObject();

    final ObjectMapper objectMapper = new ObjectMapper();

    final Map<String, Object> testObjectMap =
        new TreeMap<>(objectMapper.convertValue(testObject, Map.class));
    testObjectMap.put("@object", testObject.getClass().getName());

    return testObjectMap;
  }

  public static Connection mockConnection() {
    try {
      final DatabaseMetaData mockDbMetaData = mockDatabaseMetaData();

      final Connection mockConnection = mock(Connection.class);
      lenient().when(mockConnection.toString()).thenReturn("Connection: Mocked Connection");
      lenient().when(mockConnection.getMetaData()).thenReturn(mockDbMetaData);
      lenient().when(mockConnection.isClosed()).thenReturn(false);

      final Statement mockStatement = mockStatement();
      lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);

      final ResultSet mockResultSet =
          mockResultSet("Mocked connection", new String[] {"col1"}, null);
      lenient().when(mockStatement.getResultSet()).thenReturn(mockResultSet);

      return mockConnection;
    } catch (final SQLException e) {
      return mock(Connection.class);
    }
  }

  public static DatabaseMetaData mockDatabaseMetaData() {
    try {
      final DatabaseMetaData mockDbMetaData = mock(DatabaseMetaData.class);
      lenient().when(mockDbMetaData.toString()).thenReturn("DatabaseMetaData: Mocked Database");
      lenient().when(mockDbMetaData.getDatabaseProductName()).thenReturn("Mocked Database");
      lenient().when(mockDbMetaData.getDatabaseProductVersion()).thenReturn("0.0.1");
      lenient().when(mockDbMetaData.getURL()).thenReturn("jdbc:mocked://mockedconnection");
      lenient().when(mockDbMetaData.getDriverName()).thenReturn("Mocked Driver");
      lenient().when(mockDbMetaData.getDriverVersion()).thenReturn("0.0.1-ALPHA");
      lenient().when(mockDbMetaData.supportsCatalogsInTableDefinitions()).thenReturn(false);
      lenient().when(mockDbMetaData.supportsSchemasInTableDefinitions()).thenReturn(true);
      return mockDbMetaData;
    } catch (final SQLException e) {
      return mock(DatabaseMetaData.class);
    }
  }

  public static ResultSet mockResultSet(
      final String resultSetDescription, final String[] columnNames, final Object[][] data)
      throws SQLException {
    return (ResultSet)
        newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class[] {ResultSet.class},
            new ResultSetInvocationHandler(resultSetDescription, data, columnNames));
  }

  public static Statement mockStatement() {
    try {
      final Statement mockStatement = mock(Statement.class);
      lenient().when(mockStatement.execute(anyString())).thenReturn(true);
      lenient().when(mockStatement.getResultSet()).thenReturn(mock(ResultSet.class));
      lenient().when(mockStatement.getUpdateCount()).thenReturn(0);
      lenient().when(mockStatement.execute(anyString())).thenReturn(true);
      return mockStatement;
    } catch (final SQLException e) {
      return mock(Statement.class);
    }
  }

  private TestObjectUtility() {
    // Prevent instantiation
  }
}
