/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;

import static java.util.Collections.list;

import java.io.PrintStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvailableJDBCDrivers implements Iterable<Driver> {

  private static final Logger LOGGER = Logger.getLogger(AvailableJDBCDrivers.class.getName());

  private static List<Driver> availableJDBCDrivers() {
    final List<Driver> availableJDBCDrivers = new ArrayList<>();
    try {
      availableJDBCDrivers.addAll(list(DriverManager.getDrivers()));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not list JDBC drivers", e);
    }
    return availableJDBCDrivers;
  }

  private final List<Driver> availableJDBCDrivers;

  public AvailableJDBCDrivers() {
    availableJDBCDrivers = availableJDBCDrivers();
  }

  @Override
  public Iterator<Driver> iterator() {
    return availableJDBCDrivers.iterator();
  }

  public void print(final PrintStream out) {
    if (out == null) {
      return;
    }

    out.println();
    out.println("Available JDBC drivers:");
    for (final Driver driver : availableJDBCDrivers) {
      out.printf(
          " %-50s %2d.%d%n",
          driver.getClass().getName(), driver.getMajorVersion(), driver.getMinorVersion());
    }
  }

  public int size() {
    return availableJDBCDrivers.size();
  }

  @Override
  public String toString() {
    return "AvailableJDBCDrivers " + availableJDBCDrivers;
  }
}
