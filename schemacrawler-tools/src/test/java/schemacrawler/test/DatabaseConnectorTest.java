package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;

public class DatabaseConnectorTest {

  @Test
  public void databaseConnector() throws Exception {

    final DatabaseConnector databaseConnector = new TestDatabaseConnector();
    assertThat(
        databaseConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is("test-db"));
    assertThat(databaseConnector.getHelpCommand().getName(), is("server:test-db"));

    DatabaseConnectionOptions connectionOptions;
    DatabaseConnectionSource connectionSource;

    connectionOptions = new DatabaseUrlConnectionOptions("jdbc:test-db:some-database");
    connectionSource = databaseConnector.newDatabaseConnectionSource(connectionOptions);
    assertThat(connectionSource.getConnectionUrl(), is("jdbc:test-db:some-database"));

    connectionOptions =
        new DatabaseServerHostConnectionOptions(
            "test-db", "some-host", 2121, "some-database", null);
    connectionSource = databaseConnector.newDatabaseConnectionSource(connectionOptions);
    assertThat(connectionSource.getConnectionUrl(), is("jdbc:test-db:some-database"));

    assertThat(
        databaseConnector.getSchemaRetrievalOptionsBuilder(connectionSource.get()),
        is(not(nullValue())));

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    assertThat(
        databaseConnector.setSchemaCrawlerOptionsDefaults(schemaCrawlerOptions),
        is(not(nullValue())));

    assertThat(databaseConnector.supportsUrl("jdbc:test-db:some-database"), is(true));
  }
}
