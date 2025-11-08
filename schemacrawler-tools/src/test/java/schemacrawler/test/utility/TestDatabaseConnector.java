/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

/**
 * SchemaCrawler database support plug-in.
 *
 * <p>Plug-in to support a hypothetical RMDBS, "Test Database".
 *
 * @see <a href="https://www.schemacrawler.com">SchemaCrawler</a>
 */
public final class TestDatabaseConnector extends DatabaseConnector {

  public TestDatabaseConnector() throws Exception {
    super(
        new DatabaseServerType("test-db", "Test Database"),
        url -> url != null && url.startsWith("jdbc:test-db:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/test-db.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) -> {},
        limitOptionsBuilder -> {},
        () -> DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${database}"));
    forceInstantiationFailureIfConfigured();
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand.addOption(
        "server",
        String.class,
        "--server=test-db%n" + "Loads SchemaCrawler plug-in for Test Database");
    return pluginCommand;
  }

  private void forceInstantiationFailureIfConfigured() {
    final String propertyValue =
        System.getProperty(this.getClass().getName() + ".force-instantiation-failure");
    if (propertyValue != null) {
      throw new RuntimeException("Forced instantiation error");
    }
  }
}
