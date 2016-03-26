/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.offline;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseServerType;
import schemacrawler.tools.executable.Executable;

public final class OfflineDatabaseConnector
  extends DatabaseConnector
{

  private static final long serialVersionUID = 1727911478084169179L;

  public OfflineDatabaseConnector()
  {
    super(new DatabaseServerType("offline", "Offline"),
          "/help/Connections.offline.txt",
          "/schemacrawler-offline.config.properties",
          null,
          "jdbc:offline:.*");
  }

  @Override
  public Executable newExecutable(final String command)
    throws SchemaCrawlerException
  {
    return new OfflineSnapshotExecutable(command);
  }

}
