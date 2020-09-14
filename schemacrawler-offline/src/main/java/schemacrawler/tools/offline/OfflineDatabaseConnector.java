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
package schemacrawler.tools.offline;


import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class OfflineDatabaseConnector
  extends DatabaseConnector
{

  public static final DatabaseServerType DB_SERVER_TYPE =
    new DatabaseServerType("offline",
                           "SchemaCrawler "
                           + "Offline "
                           + "Catalog "
                           + "Snapshot");

  public OfflineDatabaseConnector()
    throws IOException
  {
    super(DB_SERVER_TYPE,
          new ClasspathInputResource("/schemacrawler-offline.config.properties"),
          (informationSchemaViewsBuilder, connection) -> {},
          (schemaRetrievalOptionsBuilder, connection) -> {},
          (limitOptionsBuilder, connection) -> {},
          () -> DatabaseConnectionUrlBuilder.builder(
              "jdbc:offline:${database}"));
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=offline%n"
                 + "Loads SchemaCrawler plug-in for offline snapshots",
                 String.class)
      .addOption("host", "Should be omitted", String.class)
      .addOption("port", "Should be omitted", Integer.class)
      .addOption("database",
                 "File name and location of the database metadata snapshot",
                 String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:offline:.*", url);
  }
  
}
