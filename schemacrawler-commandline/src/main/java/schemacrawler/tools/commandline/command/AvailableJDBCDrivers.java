/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import schemacrawler.tools.registry.JDBCDriverRegistry;

public class AvailableJDBCDrivers extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableJDBCDrivers() {
    super(JDBCDriverRegistry.getJDBCDriverRegistry().getRegisteredPlugins());
    name = JDBCDriverRegistry.getJDBCDriverRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}
