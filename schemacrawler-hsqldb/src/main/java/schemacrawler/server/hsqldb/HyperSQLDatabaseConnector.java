/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.server.hsqldb;


import java.io.IOException;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;

public final class HyperSQLDatabaseConnector
  extends DatabaseConnector
{

  public HyperSQLDatabaseConnector() throws IOException
  {
    super(new DatabaseServerType("hsqldb", "HyperSQL DataBase"),
        url -> url != null && url.startsWith("jdbc:hsqldb:"),
        (informationSchemaViewsBuilder,
            connection) -> informationSchemaViewsBuilder
                .fromResourceFolder("/hsqldb.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) -> {
        }, (limitOptionsBuilder, connection) -> {
        },
        () -> DatabaseConnectionUrlBuilder.builder(
            "jdbc:hsqldb:hsql://${host}:${port}/${database};readonly=true;hsqldb.lock_file=false")
            .withDefaultPort(9001));
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=hsqldb%n"
                 + "Loads SchemaCrawler plug-in for HyperSQL",
                 String.class)
      .addOption("host",
                 "Host name%n" + "Optional, defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n" + "Optional, defaults to 9001",
                 Integer.class)
      .addOption("database", "Database name", String.class);
    return pluginCommand;
  }

}
