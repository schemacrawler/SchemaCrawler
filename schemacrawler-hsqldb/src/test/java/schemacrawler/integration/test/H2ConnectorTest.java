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

public class H2ConnectorTest {

  @Test
  public void shouldResolveConnectorForMariaDbJdbcUrl() throws SQLException {

    // Check that HyperSQL connector is on the classpath
    final boolean hasConnector =
        DatabaseConnectorRegistry.getRegistry().hasDatabaseSystemIdentifier("hsqldb");
    assertThat("HyperSQL connector should be on the classpath", hasConnector, is(true));

    final Connection connection = Mockito.mock(Connection.class);
    final DatabaseMetaData metaData = Mockito.mock(DatabaseMetaData.class);

    Mockito.when(connection.isValid(ArgumentMatchers.anyInt())).thenReturn(true);
    Mockito.when(connection.getMetaData()).thenReturn(metaData);
    Mockito.when(metaData.getURL()).thenReturn("jdbc:h2:mem:schemacrawler");

    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.fromConnection(connection);

    assertDoesNotThrow(
        () -> {
          final SchemaRetrievalOptions options =
              DatabaseConnectorUtility.matchSchemaRetrievalOptions(connectionSource);
          assertThat(options, is(not(nullValue())));
          assertThat(options.getDatabaseServerType().getDatabaseSystemIdentifier(), is("hsqldb"));
        });
  }
}
