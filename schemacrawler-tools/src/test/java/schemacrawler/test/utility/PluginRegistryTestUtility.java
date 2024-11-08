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

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.registry.PluginRegistry;

public final class PluginRegistryTestUtility {

  private PluginRegistryTestUtility() {
    // Prevent instantiation
  }

  public static void reload(final Class<? extends PluginRegistry> registryClass) {
    Constructor<? extends PluginRegistry> constructor = null;
    try {
      constructor = registryClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (final NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      if (e.getCause() instanceof InternalRuntimeException) {
        throw (InternalRuntimeException) e.getCause();
      }
      fail(e);
    }
  }
}
