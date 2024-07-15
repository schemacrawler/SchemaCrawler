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

package schemacrawler.tools.commandline.command;

import java.util.ArrayList;
import java.util.List;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandDescription;

public class AvailableServers extends BaseAvailableCommandDescriptions {

  private static List<CommandDescription> availableServers() {
    final List<CommandDescription> availableServers = new ArrayList<>();
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    for (final DatabaseServerType serverType : databaseConnectorRegistry) {
      final CommandDescription serverDescription =
          new CommandDescription(
              serverType.getDatabaseSystemIdentifier(), serverType.getDatabaseSystemName());
      availableServers.add(serverDescription);
    }
    return availableServers;
  }

  public AvailableServers() {
    super(availableServers());
  }

  @Override
  protected String getName() {
    return "SchemaCrawler database server plugins";
  }
}
