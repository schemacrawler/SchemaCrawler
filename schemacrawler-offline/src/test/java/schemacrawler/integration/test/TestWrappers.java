package schemacrawler.integration.test;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineConnection;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.sql.Connection;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.offline.jdbc.OfflineConnection;

public class TestWrappers {

  @Test
  public void createStatement() {
    final OfflineConnection connection = newOfflineConnection(Paths.get("."));

    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "createStatement"),
        "Testing connection method, createStatement");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "createStatement", 0, 0),
        "Testing connection method, createStatement");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "createStatement", 0, 0, 0),
        "Testing connection method, createStatement");
  }

  @Test
  public void prepareCall() {
    final OfflineConnection connection = newOfflineConnection(Paths.get("."));

    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareCall", ""),
        "Testing connection method, prepareCall - 0");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareCall", "", 0, 0),
        "Testing connection method, prepareCall - 1");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareCall", "", 0, 0, 0),
        "Testing connection method, prepareCall - 2");
  }

  @Test
  public void prepareStatement() {
    final OfflineConnection connection = newOfflineConnection(Paths.get("."));

    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareStatement", ""),
        "Testing connection method, prepareStatement - 1");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareStatement", "", 0),
        "Testing connection method, prepareStatement - 1");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareStatement", "", 0, 0),
        "Testing connection method, prepareStatement - 2");
    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(connection, "prepareCall", "", 0, 0, 0),
        "Testing connection method, prepareStatement - 3");
  }

  @Test
  public void testConnectionMethodsForCoverage() throws Exception {
    final OfflineConnection connection = newOfflineConnection(Paths.get("."));

    String methodName;

    methodName = "toString";
    assertThat(
        "Testing connection method, " + methodName,
        (String) invokeMethod(connection, methodName),
        startsWith(OfflineConnection.class.getName()));

    methodName = "isValid";
    assertThat(
        "Testing connection method, " + methodName,
        invokeMethod(connection, methodName, 0),
        is(true));

    methodName = "isWrapperFor";
    assertThat(
        "Testing connection method, " + methodName,
        invokeMethod(connection, methodName, Connection.class),
        is(true));
    methodName = "isWrapperFor";
    assertThat(
        "Testing connection method, " + methodName,
        invokeMethod(connection, methodName, String.class),
        is(false));
    methodName = "isWrapperFor";
    assertThat(
        "Testing connection method, " + methodName,
        invokeMethod(connection, methodName, (Class<?>) null),
        is(false));
  }

  @Test
  public void testConnectionMethodsNoArguments() {
    final OfflineConnection connection = newOfflineConnection(Paths.get("."));

    for (final String methodName :
        new String[] {
          "clearWarnings",
          "commit",
          "createBlob",
          "createClob",
          "createNClob",
          "createSQLXML",
          "createStatement",
          "getClientInfo",
          "setSavepoint",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(connection, methodName),
          "Testing connection method, " + methodName);
    }
  }
}
