/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.server.db2;


import schemacrawler.crawl.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseServerType;

public final class DB2DatabaseConnector
  extends DatabaseConnector
{

  private static final long serialVersionUID = 2668483709122768087L;

  public DB2DatabaseConnector()
  {
    super(new DatabaseServerType("db2", "IBM DB2"),
          "/help/Connections.db2.txt",
          "/schemacrawler-db2.config.properties",
          "/db2.information_schema",
          "jdbc:db2:.*");
  }

  @Override
  public DatabaseSpecificOverrideOptionsBuilder getDatabaseSpecificOverrideOptionsBuilder()
  {
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = super.getDatabaseSpecificOverrideOptionsBuilder();
    databaseSpecificOverrideOptionsBuilder
      .withTableColumnRetrievalStrategy(MetadataRetrievalStrategy.metadata_all);
    return databaseSpecificOverrideOptionsBuilder;
  }

}
