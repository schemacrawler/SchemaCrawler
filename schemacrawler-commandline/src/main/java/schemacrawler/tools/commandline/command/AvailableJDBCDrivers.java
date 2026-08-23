/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import us.fatehi.utility.database.JdbcDriverRegistry;

public class AvailableJDBCDrivers extends BaseAvailableRegistryPlugins {

  private final String name;

  public AvailableJDBCDrivers() {
    super(JdbcDriverRegistry.getRegistry().availableJDBCDrivers());
    name = "JDBC Drivers";
  }

  @Override
  public String getName() {
    return name;
  }
}
