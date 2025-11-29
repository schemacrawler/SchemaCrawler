/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.databaseconnector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.TestDatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
public class DatabaseConnectorTest {

  @Test
  public void databaseConnector() throws Exception {

    final DatabaseConnector databaseConnector = new TestDatabaseConnector();
    assertThat(
        databaseConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is("test-db"));
    assertThat(databaseConnector.getHelpCommand().getName(), is("server:test-db"));

    DatabaseConnectionOptions connectionOptions;
    DatabaseConnectionSource connectionSource;

    connectionOptions =
        new DatabaseServerHostConnectionOptions(
            "test-db", "some-host", 2121, "some-database", null);
    connectionSource =
        databaseConnector.newDatabaseConnectionSource(
            connectionOptions, new MultiUseUserCredentials());

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

  /**
   * NOTE: This test does not test production code, but rather test utility code. However, it covers
   * basic logic in the database connector class.
   */
  @Test
  public void testDatabaseConnector() throws Exception {
    final DatabaseConnector databaseConnector = new TestDatabaseConnector();

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is("server:test-db"));

    assertThat(
        databaseConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is("test-db"));

    assertThat(databaseConnector.supportsUrl("jdbc:test-db:somevalue"), is(true));
    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"), is(false));
    assertThat(databaseConnector.supportsUrl(null), is(false));

    assertThat(databaseConnector.toString(), is("Database connector for test-db - Test Database"));
  }

  @Test
  public void unknownDatabaseConnector() {
    final DatabaseConnector databaseConnector = UnknownDatabaseConnector.UNKNOWN;

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is(""));

    assertThat(
        databaseConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is(nullValue()));

    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"), is(false));
    assertThat(databaseConnector.supportsUrl(null), is(false));

    assertThat(
        databaseConnector.toString(), is("Database connector for unknown database system type"));
  }
}
