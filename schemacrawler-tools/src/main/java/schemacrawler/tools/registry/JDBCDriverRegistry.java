/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.registry;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public class JDBCDriverRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(JDBCDriverRegistry.class.getName());

  private static JDBCDriverRegistry jdbcDriverRegistrySingleton;

  public static JDBCDriverRegistry getJDBCDriverRegistry() {
    if (jdbcDriverRegistrySingleton == null) {
      jdbcDriverRegistrySingleton = new JDBCDriverRegistry();
      jdbcDriverRegistrySingleton.log();
    }
    return jdbcDriverRegistrySingleton;
  }

  private static List<PropertyName> loadJDBCDrivers() {

    // Use thread-safe list
    final List<PropertyName> availableJDBCDrivers = new CopyOnWriteArrayList<>();
    try {
      final Collection<Driver> drivers = DatabaseUtility.getAvailableJdbcDrivers();
      for (final Driver driver : drivers) {
        final String driverName = driver.getClass().getName();
        LOGGER.log(Level.FINE, new StringFormat("Found JDBC driver <%s>", driverName));
        final String driverDescription =
            "%2d.%d".formatted(driver.getMajorVersion(), driver.getMinorVersion());
        availableJDBCDrivers.add(new PropertyName(driverName, driverDescription));
      }
    } catch (final Throwable e) {
      throw new InternalRuntimeException("Could not load JDBC drivers", e);
    }
    Collections.sort(availableJDBCDrivers);
    return availableJDBCDrivers;
  }

  private final Collection<PropertyName> jdbcDrivers;

  private JDBCDriverRegistry() {
    jdbcDrivers = loadJDBCDrivers();
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    return new ArrayList<>(jdbcDrivers);
  }

  @Override
  public String getName() {
    return "JDBC Drivers";
  }
}
