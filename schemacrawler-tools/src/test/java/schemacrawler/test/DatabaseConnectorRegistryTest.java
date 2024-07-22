/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.TestDatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.property.PropertyName;

public class DatabaseConnectorRegistryTest {

  @Test
  public void databaseConnectorRegistry() {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();

    final List<DatabaseServerType> databaseServerTypes =
        databaseConnectorRegistry.getDatabaseServerTypes();

    assertThat(databaseServerTypes, hasSize(1));
    assertThat(databaseConnectorRegistry.hasDatabaseSystemIdentifier("test-db"), is(true));
    assertThat(databaseConnectorRegistry.getHelpCommands(), hasSize(1));

    final DatabaseConnector testDbConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier("test-db");
    assertThat(testDbConnector, is(notNullValue()));
    assertThat(
        testDbConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is("test-db"));

    final DatabaseConnector unknownConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier("newdb");
    assertThat(unknownConnector, is(notNullValue()));
    assertThat(
        unknownConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is(nullValue()));
  }

  @Test
  public void findDatabaseConnectorFromUrl() {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();

    DatabaseServerType databaseServerType;

    databaseServerType =
        databaseConnectorRegistry
            .findDatabaseConnectorFromUrl("jdbc:test-db:something")
            .getDatabaseServerType();
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("test-db"));

    databaseServerType =
        databaseConnectorRegistry
            .findDatabaseConnectorFromUrl("jdbc:other-db:something")
            .getDatabaseServerType();
    assertThat(databaseServerType, is(DatabaseServerType.UNKNOWN));

    databaseServerType =
        databaseConnectorRegistry.findDatabaseConnectorFromUrl(null).getDatabaseServerType();
    assertThat(databaseServerType, is(DatabaseServerType.UNKNOWN));
  }

  @Test
  public void commandLineCommands() throws Exception {

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final Collection<PluginCommand> commandLineCommands =
        databaseConnectorRegistry.getCommandLineCommands();
    assertThat(commandLineCommands, is(empty()));
  }

  @Test
  public void helpCommands() throws Exception {

    final TestDatabaseConnector testDatabaseConnector = new TestDatabaseConnector();

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final Collection<PluginCommand> commandLineCommands =
        databaseConnectorRegistry.getHelpCommands();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands, hasItem(testDatabaseConnector.getHelpCommand()));
  }

  @Test
  public void commandDescriptions() throws Exception {

    final TestDatabaseConnector testDatabaseConnector = new TestDatabaseConnector();
    DatabaseServerType databaseServerType = testDatabaseConnector.getDatabaseServerType();
    final PropertyName serverDescription =
        new PropertyName(
            databaseServerType.getDatabaseSystemIdentifier(),
            databaseServerType.getDatabaseSystemName());

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final Collection<PropertyName> commandLineCommands =
        databaseConnectorRegistry.getCommandDescriptions();
    assertThat(commandLineCommands, hasSize(1));
    assertThat(commandLineCommands.stream().findFirst().get(), is(serverDescription));
  }

  @Test
  public void name() throws Exception {

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    assertThat(databaseConnectorRegistry.getName(), is("SchemaCrawler database server plugins"));
  }

  @Test
  public void loadError() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestDatabaseConnector.class.getName() + ".force-instantiation-failure", "throw");

          assertThrows(InternalRuntimeException.class, () -> DatabaseConnectorRegistry.reload());
        });
    // Reset
    DatabaseConnectorRegistry.reload();
  }
}
