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

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.property.PropertyName;

public abstract class BasePluginRegistry implements PluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(BasePluginRegistry.class.getName());

  @Override
  public Collection<PluginCommand> getCommandLineCommands() {
    return Collections.emptyList();
  }

  @Override
  public Collection<PluginCommand> getHelpCommands() {
    return Collections.emptyList();
  }

  @Override
  public void log() {
    final boolean log = LOGGER.isLoggable(Level.CONFIG);
    if (!log) {
      return;
    }

    int index = 0;
    final StringBuilder buffer = new StringBuilder(1024);
    try {
      buffer.append("Registered ").append(getName()).append(":").append(System.lineSeparator());
      for (final PropertyName registeredPlugin : getRegisteredPlugins()) {
        index++;
        if (log) {
          buffer
              .append(
                  String.format(
                      "%2d %50s %s",
                      index, registeredPlugin.getName(), registeredPlugin.getDescription()))
              .append(System.lineSeparator());
        }
      }
    } catch (final Throwable e) {
      // Log the error and continue
      LOGGER.log(Level.WARNING, "Could not list " + getName(), e);
    }
    LOGGER.log(Level.CONFIG, buffer.toString());
  }
}
