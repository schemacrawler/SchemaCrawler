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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.property.PropertyName;

public class JDBCDriverRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(JDBCDriverRegistry.class.getName());

  private static JDBCDriverRegistry jdbcDriverRegistrySingleton;

  public static JDBCDriverRegistry getJDBCDriverRegistry() {
    if (jdbcDriverRegistrySingleton == null) {
      jdbcDriverRegistrySingleton = new JDBCDriverRegistry();
    }
    return jdbcDriverRegistrySingleton;
  }

  public static void reload() {
    if (jdbcDriverRegistrySingleton != null) {
      final Collection<PropertyName> registry =
          jdbcDriverRegistrySingleton.commandDescriptions;
      registry.clear();
      registry.addAll(loadJDBCDrivers());
    }
  }

  private static List<PropertyName> loadJDBCDrivers() {

    // Use thread-safe list
    final List<PropertyName> availableJDBCDrivers = new CopyOnWriteArrayList<>();
    try {
      final Collection<Driver> drivers = DatabaseUtility.getAvailableJdbcDrivers();
      for (final Driver driver : drivers) {
        final String driverName = driver.getClass().getName();
        final String driverDescription =
            String.format("%2d.%d", driver.getMajorVersion(), driver.getMinorVersion());
        availableJDBCDrivers.add(new PropertyName(driverName, driverDescription));
      }
    } catch (final Throwable e) {
      throw new InternalRuntimeException("Could not load JDBC drivers", e);
    }
    return availableJDBCDrivers;
  }

  private final Collection<PropertyName> commandDescriptions;

  private JDBCDriverRegistry() {
    commandDescriptions = loadJDBCDrivers();
  }

  @Override
  public Collection<PropertyName> getCommandDescriptions() {
    return new ArrayList<>(commandDescriptions);
  }

  @Override
  public String getName() {
    return "JDBC drivers";
  }
}
