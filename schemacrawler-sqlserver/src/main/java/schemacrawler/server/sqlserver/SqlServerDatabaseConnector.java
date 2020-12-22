/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.server.sqlserver;


import java.io.IOException;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;

public final class SqlServerDatabaseConnector
  extends DatabaseConnector
{

  public SqlServerDatabaseConnector() throws IOException
  {
    super(new DatabaseServerType("sqlserver", "Microsoft SQL Server"),
        url -> url != null && url.startsWith("jdbc:sqlserver:"),
        (informationSchemaViewsBuilder,
            connection) -> informationSchemaViewsBuilder
                .fromResourceFolder("/sqlserver.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) -> {
        },
        (limitOptionsBuilder) -> limitOptionsBuilder
            .includeSchemas(new RegularExpressionRule(".*\\.dbo",
                "model\\..*|master\\..*|msdb\\..*|tempdb\\..*|rdsadmin\\..*")),
        () -> DatabaseConnectionUrlBuilder.builder(
            "jdbc:sqlserver://${host}:${port};databaseName=${database};applicationName=SchemaCrawler")
            .withDefaultPort(1433));
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 String.class,
                 "--server=mysql%n"
         + "Loads SchemaCrawler plug-in for Microsoft SQL Server%n"
         + "If you are using named pipes, or Windows authentication, "
         + "you will need to provide a database connection URL on "
         + "the SchemaCrawler command-line")
      .addOption("host",
                 String.class,
                 "Host name%n" + "Optional, defaults to localhost")
      .addOption("port",
                 Integer.class,
                 "Port number%n" + "Optional, defaults to 1433")
      .addOption("database",
                 String.class,
                 "Database name%n"
         + "Be sure to also restrict your schemas to this database, "
         + "by using an additional option,%n"
         + "--schemas=<database>.dbo");
    return pluginCommand;
  }
  
}
