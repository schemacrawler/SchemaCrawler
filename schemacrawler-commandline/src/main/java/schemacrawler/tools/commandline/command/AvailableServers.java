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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class AvailableServers implements Iterable<String> {

  private static List<String> availableServers() {
    final List<String> availableServers = new ArrayList<>();
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    for (final DatabaseServerType serverType : databaseConnectorRegistry) {
      final String name = serverType.getDatabaseSystemIdentifier();
      availableServers.add(name);
    }
    return availableServers;
  }

  private final List<String> availableServers;

  public AvailableServers() {
    availableServers = availableServers();
  }

  @Override
  public Iterator<String> iterator() {
    return availableServers.iterator();
  }

  public int size() {
    return availableServers.size();
  }
}
