/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.commandline.utility;

import static us.fatehi.utility.Utility.join;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.JvmSystemInfo;
import schemacrawler.OperatingSystemInfo;
import schemacrawler.Version;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class CommandLineLoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(CommandLineLoggingUtility.class.getName());

  public static void logFatalStackTrace(final Throwable t) {
    if (t == null || !LOGGER.isLoggable(Level.SEVERE)) {
      return;
    }

    LOGGER.log(Level.SEVERE, t.getMessage(), t);
  }

  public static void logSafeArguments(final String[] args) {
    if (!LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    final Version version = Version.version();
    LOGGER.log(
        Level.INFO,
        String.format(
            "Environment:%n%s %s%n%s%n%s%n",
            version.getProductName(),
            version.getProductVersion(),
            new OperatingSystemInfo(),
            new JvmSystemInfo()));

    if (args == null) {
      return;
    }

    final String passwordRedacted = "<password provided>";
    final StringJoiner argsList = new StringJoiner(System.lineSeparator());
    for (final Iterator<String> iterator = Arrays.asList(args).iterator(); iterator.hasNext(); ) {
      final String arg = iterator.next();
      if (arg == null) {
        continue;
      } else {
        if (arg.matches("--password.*=.*")) {
          argsList.add(passwordRedacted);
        } else if (arg.startsWith("--password")) {
          argsList.add(passwordRedacted);
          if (iterator.hasNext()) {
            // Skip over the password
            iterator.next();
          }
        } else {
          argsList.add(arg);
        }
      }
    }

    LOGGER.log(Level.INFO, new StringFormat("Command line: %n%s", argsList.toString()));
  }

  public static void logSystemClasspath() {
    if (!LOGGER.isLoggable(Level.CONFIG)) {
      return;
    }

    LOGGER.log(
        Level.CONFIG,
        String.format("Classpath: %n%s", printPath(System.getProperty("java.class.path"))));
    LOGGER.log(
        Level.CONFIG,
        String.format("LD_LIBRARY_PATH: %n%s", printPath(System.getenv("LD_LIBRARY_PATH"))));
  }

  public static void logSystemProperties() {
    if (!LOGGER.isLoggable(Level.CONFIG)) {
      return;
    }

    final SortedMap<String, String> systemProperties = new TreeMap<>();
    for (final Entry<Object, Object> propertyValue : System.getProperties().entrySet()) {
      final String name = (String) propertyValue.getKey();
      if ((name.startsWith("java.") || name.startsWith("os.")) && !name.endsWith(".path")) {
        systemProperties.put(name, (String) propertyValue.getValue());
      }
    }

    LOGGER.log(
        Level.CONFIG,
        String.format("System properties: %n%s", join(systemProperties, System.lineSeparator())));
  }

  private static String printPath(final String path) {
    if (path == null) {
      return "";
    }
    return String.join(System.lineSeparator(), path.split(File.pathSeparator));
  }

  private CommandLineLoggingUtility() {
    // Prevent instantiation
  }
}
