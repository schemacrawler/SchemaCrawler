/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.iosource.ClasspathInputResource;

import java.io.IOException;
import java.util.regex.Pattern;

public final class OfflineDatabaseConnector
    extends DatabaseConnector
{

  public static final DatabaseServerType DB_SERVER_TYPE = new DatabaseServerType("offline",
                                                                                 "SchemaCrawler " +
                                                                                     "Offline " +
                                                                                     "Catalog " +
                                                                                     "Snapshot");
  private static final long serialVersionUID = 1727911478084169179L;

  public OfflineDatabaseConnector()
      throws IOException
  {
    super(DB_SERVER_TYPE,
          new ClasspathInputResource("/help/Connections.offline.txt"),
          new ClasspathInputResource("/schemacrawler-offline.config.properties"),
          null,
          url -> Pattern.matches("jdbc:offline:.*", url));
  }

}
