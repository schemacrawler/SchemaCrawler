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
package schemacrawler.server.sqlserver;


import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;
import sf.util.SchemaCrawlerLogger;

public final class SqlServerDatabaseConnector
  extends DatabaseConnector
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    SqlServerDatabaseConnector.class.getName());

  public SqlServerDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("sqlserver", "Microsoft SQL Server"),
          new ClasspathInputResource(
            "/schemacrawler-sqlserver.config.properties"),
          (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder
            .fromResourceFolder("/sqlserver.information_schema"));
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand.addOption("server",
                            "--server=mysql%n"
                            + "Loads SchemaCrawler plug-in for Microsoft SQL Server%n"
                            + "If you are using named pipes, or Windows authentication, "
                            + "you will need to provide a database connection URL on "
                            + "the SchemaCrawler command-line",
                            String.class)
                 .addOption("host",
                            "Host name%n" + "Optional, defaults to localhost",
                            String.class)
                 .addOption("port",
                            "Port number%n" + "Optional, defaults to 1433",
                            Integer.class)
                 .addOption("database",
                            "Database name%n"
                            + "Be sure to also restrict your schemas to this database, "
                            + "by using an additional option,%n"
                            + "--schemas=<database>.dbo",
                            String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:sqlserver:.*", url);
  }

}
