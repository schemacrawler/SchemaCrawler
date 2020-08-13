/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.databaseconnector;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestDatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class TestDatabaseConnectorTest
{

  /**
   * NOTE: This test does not test production code, but rather test utility
   * code. However, it covers basic logic in the database connector class.
   */
  @Test
  public void testDatabaseConnector()
    throws Exception
  {
    final DatabaseConnector databaseConnector = new TestDatabaseConnector();

    final Config config = databaseConnector.getConfig();
    assertThat(config, is(notNullValue()));
    assertThat(config, aMapWithSize(4));

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is("server:test-db"));

    assertThat(databaseConnector
                 .getDatabaseServerType()
                 .getDatabaseSystemIdentifier(), is("test-db"));

    final EnumDataTypeHelper enumDataTypeHelper =
      databaseConnector.getEnumDataTypeHelper();
    assertThat(enumDataTypeHelper
                 .getEnumDataTypeInfo(null, null, null)
                 .getEnumValues(), is(empty()));

    assertThat(databaseConnector.supportsUrl("jdbc:test-db:somevalue"),
               is(true));
    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"),
               is(false));
    assertThat(databaseConnector.supportsUrl(null), is(false));

    assertThat(databaseConnector.toString(),
               is("Database connector for test-db - Test Database"));
  }

  @Test
  public void unknownDatabaseConnector()
  {
    final DatabaseConnector databaseConnector = DatabaseConnector.UNKNOWN;

    final Config config = databaseConnector.getConfig();
    assertThat(config, is(notNullValue()));
    assertThat(config, is(anEmptyMap()));

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is(nullValue()));

    assertThat(databaseConnector
                 .getDatabaseServerType()
                 .getDatabaseSystemIdentifier(), is(nullValue()));

    final EnumDataTypeHelper enumDataTypeHelper =
      databaseConnector.getEnumDataTypeHelper();
    assertThat(enumDataTypeHelper
                 .getEnumDataTypeInfo(null, null, null)
                 .getEnumValues(), is(empty()));

    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"),
               is(false));
    assertThat(databaseConnector.supportsUrl(null), is(false));

    assertThat(databaseConnector.toString(),
               is("Database connector for unknown database system type"));
  }

  @Test
  public void newConnectionWithUnknownConnector()
    throws SchemaCrawlerException
  {
    final DatabaseConnector databaseConnector = DatabaseConnector.UNKNOWN;

    final DatabaseConnectionSource expectedDatabaseConnectionSource =
      expectedDatabaseConnectionSource(
        "jdbc:hsqldb:hsql://localhost:9001/schemacrawler");

    final DatabaseConnectionSource databaseConnectionSource =
      databaseConnector.newDatabaseConnectionSource((config) -> expectedDatabaseConnectionSource);
    assertThat(databaseConnectionSource.getConnectionUrl(),
               is(expectedDatabaseConnectionSource.getConnectionUrl()));

  }

  @Test
  public void newMajorDatabaseConnectionWithUnknownConnector()
  {
    final DatabaseConnector databaseConnector = DatabaseConnector.UNKNOWN;

    final DatabaseConnectionSource expectedDatabaseConnectionSource =
      expectedDatabaseConnectionSource(
        "jdbc:mysql://localhost:9001/schemacrawler");

    final DatabaseConnectorOptions databaseConnectorOptions =
      (config) -> expectedDatabaseConnectionSource;

    assertThrows(SchemaCrawlerException.class,
                 () -> databaseConnector.newDatabaseConnectionSource(
                   databaseConnectorOptions));

  }

  @Test
  @SetSystemProperty(key = "SC_IGNORE_MISSING_DATABASE_PLUGIN", value = "true")
  public void newMajorDatabaseConnectionWithUnknownConnectorWithOverride()
    throws SchemaCrawlerException
  {
    final DatabaseConnector databaseConnector = DatabaseConnector.UNKNOWN;

    final DatabaseConnectionSource expectedDatabaseConnectionSource =
      expectedDatabaseConnectionSource(
        "jdbc:mysql://localhost:9001/schemacrawler");

    final DatabaseConnectionSource databaseConnectionSource =
      databaseConnector.newDatabaseConnectionSource((config) -> expectedDatabaseConnectionSource);
    assertThat(databaseConnectionSource.getConnectionUrl(),
               is(expectedDatabaseConnectionSource.getConnectionUrl()));

  }

  private DatabaseConnectionSource expectedDatabaseConnectionSource(final String connectionUrl)
  {
    final DatabaseConnectionSource expectedDatabaseConnectionSource =
      new DatabaseConnectionSource(connectionUrl);
    expectedDatabaseConnectionSource.setUserCredentials(new SingleUseUserCredentials(
      "sa",
      ""));
    return expectedDatabaseConnectionSource;
  }

}
