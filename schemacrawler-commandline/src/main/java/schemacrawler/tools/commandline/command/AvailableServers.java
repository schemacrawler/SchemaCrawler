/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class AvailableServers extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableServers() {
    super(DatabaseConnectorRegistry.getDatabaseConnectorRegistry().getRegisteredPlugins());
    name = DatabaseConnectorRegistry.getDatabaseConnectorRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}
