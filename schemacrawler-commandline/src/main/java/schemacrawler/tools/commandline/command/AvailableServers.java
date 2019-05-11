/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class AvailableServers
  implements Iterable<String>
{

  private static List<String> availableServers()
  {
    final List<String> availableServers = new ArrayList<>();
    try
    {
      for (final DatabaseServerType serverType : new DatabaseConnectorRegistry())
      {
        final String description = serverType.getDatabaseSystemIdentifier();
        availableServers.add(description);
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not initialize command registry",
        e);
    }

    return availableServers;
  }

  private final List<String> availableServers;

  public AvailableServers()
  {
    availableServers = availableServers();
  }

  @Override
  public Iterator<String> iterator()
  {
    return availableServers.iterator();
  }

  public int size()
  {
    return availableServers.size();
  }

}
