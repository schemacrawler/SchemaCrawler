/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItemInArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.registry.JDBCDriverRegistry;
import schemacrawler.tools.registry.PluginRegistry;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.property.PropertyName;

public class AvailableRegistryPluginsTest {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        getRegisteredPlugins(CatalogLoaderRegistry.getCatalogLoaderRegistry()),
        arrayContainingInAnyOrder("testloader", "schemacrawlerloader"));
  }

  @Test
  public void availableCommands() {
    assertThat(
        getRegisteredPlugins(CommandRegistry.getCommandRegistry()),
        arrayContainingInAnyOrder("test-command"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    final String[] availableJDBCDrivers =
        getRegisteredPlugins(JDBCDriverRegistry.getJDBCDriverRegistry());
    assertThat(availableJDBCDrivers.length, is(16));
    assertThat(availableJDBCDrivers, hasItemInArray(TestDatabaseDriver.class.getName()));
  }

  @Test
  public void availableServers() {
    assertThat(
        getRegisteredPlugins(DatabaseConnectorRegistry.getDatabaseConnectorRegistry()),
        arrayContainingInAnyOrder("test-db"));
  }

  private String[] getRegisteredPlugins(final PluginRegistry registry) {
    final List<String> commands = new ArrayList<>();
    final Collection<PropertyName> registeredPlugins = registry.getRegisteredPlugins();
    for (final PropertyName registeredPlugin : registeredPlugins) {
      commands.add(registeredPlugin.getName());
    }
    return commands.toArray(new String[0]);
  }
}
