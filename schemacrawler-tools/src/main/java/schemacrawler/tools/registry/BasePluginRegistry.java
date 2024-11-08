/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.property.PropertyName;

public abstract class BasePluginRegistry implements PluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(BasePluginRegistry.class.getName());

  @Override
  public void log() {
    final boolean log = LOGGER.isLoggable(Level.CONFIG);
    if (!log) {
      return;
    }

    int index = 0;
    final StringBuilder buffer = new StringBuilder(1024);
    try {
      int maxNameLength = 0;
      final Collection<PropertyName> registeredPlugins = getRegisteredPlugins();
      for (final PropertyName registeredPlugin : registeredPlugins) {
        final int length = registeredPlugin.getName().length();
        if (length > maxNameLength) {
          maxNameLength = length;
        }
      }
      final String format = String.format("%%2d %%%ds %%s", maxNameLength);

      buffer.append("Registered ").append(getName()).append(":").append(System.lineSeparator());
      for (final PropertyName registeredPlugin : registeredPlugins) {
        index++;
        buffer
            .append(
                String.format(
                    format, index, registeredPlugin.getName(), registeredPlugin.getDescription()))
            .append(System.lineSeparator());
      }
    } catch (final Throwable e) {
      // Log the error and continue
      LOGGER.log(Level.WARNING, "Could not list " + getName(), e);
    }
    LOGGER.log(Level.CONFIG, buffer.toString());
  }
}
