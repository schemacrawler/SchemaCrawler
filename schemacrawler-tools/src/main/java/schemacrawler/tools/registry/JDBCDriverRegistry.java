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

package schemacrawler.tools.registry;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.database.DatabaseUtility;

public class JDBCDriverRegistry implements PluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(JDBCDriverRegistry.class.getName());

  private static JDBCDriverRegistry jdbcDriverRegistrySingleton;

  public static JDBCDriverRegistry getJDBCDriverRegistry() {
    if (jdbcDriverRegistrySingleton == null) {
      jdbcDriverRegistrySingleton = new JDBCDriverRegistry();
    }
    return jdbcDriverRegistrySingleton;
  }

  private static List<CommandDescription> loadJDBCDrivers() {
    final List<CommandDescription> availableJDBCDrivers = new ArrayList<>();
    try {
      final Collection<Driver> drivers = DatabaseUtility.getAvailableJdbcDrivers();
      for (final Driver driver : drivers) {
        final String driverName = driver.getClass().getName();
        final String driverDescription =
            String.format("%2d.%d", driver.getMajorVersion(), driver.getMinorVersion());
        availableJDBCDrivers.add(new CommandDescription(driverName, driverDescription));
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not list JDBC drivers", e);
    }
    return availableJDBCDrivers;
  }

  private final Collection<CommandDescription> commandDescriptions;

  private JDBCDriverRegistry() {
    commandDescriptions = loadJDBCDrivers();
  }

  @Override
  public Collection<PluginCommand> getCommandLineCommands() {
    return Collections.emptyList();
  }

  @Override
  public Collection<PluginCommand> getHelpCommands() {
    return Collections.emptyList();
  }

  @Override
  public Collection<CommandDescription> getCommandDescriptions() {
    return new ArrayList<>(commandDescriptions);
  }
}
