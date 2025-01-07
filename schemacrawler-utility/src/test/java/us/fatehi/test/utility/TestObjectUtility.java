package us.fatehi.test.utility;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import java.nio.file.AccessMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestObjectUtility {

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
    testObject1.setIntegerList(Arrays.asList(1, 1, 2, 3, 5, 8));
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
      lenient().when(mockConnection.getMetaData()).thenReturn(mockDbMetaData);
      lenient().when(mockConnection.isClosed()).thenReturn(false);

      final Statement mockStatement = mockStatement();
      lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);

      final ResultSet mockResultSet = mockResultSet(new String[] {"col1"}, null);
      lenient().when(mockStatement.getResultSet()).thenReturn(mockResultSet);

      return mockConnection;
    } catch (SQLException e) {
      return mock(Connection.class);
    }
  }

  public static Statement mockStatement() {
    try {
      final Statement mockStatement = mock(Statement.class);
      lenient().when(mockStatement.execute(anyString())).thenReturn(true);
      lenient().when(mockStatement.getResultSet()).thenReturn(mock(ResultSet.class));
      lenient().when(mockStatement.getUpdateCount()).thenReturn(0);
      lenient().when(mockStatement.execute(anyString())).thenReturn(true);
      return mockStatement;
    } catch (SQLException e) {
      return mock(Statement.class);
    }
  }

  public static DatabaseMetaData mockDatabaseMetaData() {
    try {
      final DatabaseMetaData mockDbMetaData = mock(DatabaseMetaData.class);
      lenient().when(mockDbMetaData.getDatabaseProductName()).thenReturn("Fake Database");
      lenient().when(mockDbMetaData.getDatabaseProductVersion()).thenReturn("0.0.1");
      lenient().when(mockDbMetaData.getURL()).thenReturn("jdbc:fake://fakeconnection");
      lenient().when(mockDbMetaData.getDriverName()).thenReturn("Fake Driver");
      lenient().when(mockDbMetaData.getDriverVersion()).thenReturn("0.0.1");
      lenient().when(mockDbMetaData.supportsCatalogsInTableDefinitions()).thenReturn(false);
      lenient().when(mockDbMetaData.supportsSchemasInTableDefinitions()).thenReturn(true);
      return mockDbMetaData;
    } catch (final SQLException e) {
      return mock(DatabaseMetaData.class);
    }
  }

  public static ResultSet mockResultSet(final String[] columnNames, final Object[][] data)
      throws SQLException {
    final ResultSet mockRs = mock(ResultSet.class);
    final ResultSetMetaData rsmd = mock(ResultSetMetaData.class);

    // Mock ResultSetMetaData
    lenient().when(mockRs.getMetaData()).thenReturn(rsmd);
    lenient().when(rsmd.getColumnCount()).thenReturn(columnNames.length);
    for (int i = 0; i < columnNames.length; i++) {
      lenient().when(rsmd.getColumnName(i + 1)).thenReturn(columnNames[i]);
    }

    // Mock ResultSet data
    final int[] rowIndex = {-1};
    if (data == null) {
      lenient().when(mockRs.next()).thenAnswer(invocation -> false);
    } else {
      lenient()
          .when(mockRs.next())
          .thenAnswer(
              invocation -> {
                rowIndex[0]++;
                return rowIndex[0] < data.length;
              });
    }

    if (data == null) {
      lenient().when(mockRs.getObject(anyInt())).thenAnswer(invocation -> null);
      lenient().when(mockRs.getString(anyInt())).thenAnswer(invocation -> (String) null);
    } else {
      for (int i = 0; i < columnNames.length; i++) {
        final int columnIndex = i;
        lenient()
            .when(mockRs.getObject(columnIndex + 1))
            .thenAnswer(invocation -> data[rowIndex[0]][columnIndex]);
        lenient()
            .when(mockRs.getString(columnIndex + 1))
            .thenAnswer(invocation -> (String) data[rowIndex[0]][columnIndex]);
      }
    }

    return mockRs;
  }

  private TestObjectUtility() {
    // Prevent instantiation
  }
}
