package schemacrawler.integration.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.testdb.TestDatabase;

@TestInstance(Lifecycle.PER_CLASS)
public class TestSchemaCreator {

  private TestDatabase testDatabase;

  @BeforeAll
  public void startDatabase() {
    testDatabase = TestDatabase.initialize();
  }

  @AfterAll
  public void stopDatabase() {
    testDatabase.stop();
  }

  @Test
  public void testSchemaCreator() throws SQLException {
    try (final Connection connection = testDatabase.getConnection();
        final ResultSet resultSchemas = connection.getMetaData().getSchemas()) {

      final List<String> actualSchemas = new ArrayList<>();
      while (resultSchemas.next()) {
        actualSchemas.add(resultSchemas.getString("TABLE_SCHEM"));
      }
      Collections.sort(actualSchemas);

      // Replace with your expected schema names
      final List<String> expectedSchemas =
          List.of(
              "BOOKS",
              "FOR_LINT",
              "INFORMATION_SCHEMA",
              "PUBLIC",
              "PUBLISHER SALES",
              "SYSTEM_LOBS");

      assertEquals(expectedSchemas, actualSchemas, "Schema list does not match expected values");
    }
  }
}
