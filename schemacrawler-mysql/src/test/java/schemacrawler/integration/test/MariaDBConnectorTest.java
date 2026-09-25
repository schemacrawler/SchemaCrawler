package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.utility.DatabaseConnectorUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public class MariaDBConnectorTest {

  @Test
  public void shouldResolveConnectorForMariaDbJdbcUrl() throws SQLException {

    // Check that MySQL connector is on the classpath
    final boolean hasMySqlConnector =
        DatabaseConnectorRegistry.getRegistry().hasDatabaseSystemIdentifier("mysql");
    assertThat("MySQL connector should be on the classpath", hasMySqlConnector, is(true));

    final Connection connection = Mockito.mock(Connection.class);
    final DatabaseMetaData metaData = Mockito.mock(DatabaseMetaData.class);

    Mockito.when(connection.isValid(ArgumentMatchers.anyInt())).thenReturn(true);
    Mockito.when(connection.getMetaData()).thenReturn(metaData);
    Mockito.when(metaData.getURL()).thenReturn("jdbc:mariadb://localhost:3306/test");

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.fromConnection(connection);

    // Fails in 17.15.x with:
    // InternalRuntimeException: Add the SchemaCrawler database connector plugin for
    // <mysql> to the CLASSPATH
    assertDoesNotThrow(
        () -> {
          final SchemaRetrievalOptions options =
              DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
          assertThat(options, is(not(nullValue())));
        });
  }
}
