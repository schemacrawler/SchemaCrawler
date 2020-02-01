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
package schemacrawler.server.postgresql;


import static java.util.Objects.requireNonNull;
import static schemacrawler.server.postgresql.PostgreSQLUtility.getEnumValues;
import static sf.util.DatabaseUtility.checkConnection;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;
import sf.util.SchemaCrawlerLogger;

public final class PostgreSQLDatabaseConnector
  extends DatabaseConnector
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(PostgreSQLDatabaseConnector.class.getName());

  private static final EnumDataTypeHelper enumDataTypeHelper = new PostgreSQLEnumDataTypeHelper();
  public static final BiConsumer<InformationSchemaViewsBuilder, Connection>
    informationSchemaBuilderConsumer =
    (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder.fromResourceFolder(
      "/postgresql.information_schema");

  public PostgreSQLDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("postgresql", "PostgreSQL"),
          new ClasspathInputResource(
            "/schemacrawler-postgresql.config.properties"),
          informationSchemaBuilderConsumer,
          enumDataTypeHelper);
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=postgresql%n"
                 + "Loads SchemaCrawler plug-in for PostgreSQL",
                 String.class)
      .addOption("host",
                 "Host name%n"
                 + "Optional, uses the PGHOSTADDR and PGHOST environmental variables "
                 + "if available, or defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n"
                 + "Optional, uses the PGPORT environmental variable "
                 + "if available, or defaults to 5432",
                 Integer.class)
      .addOption("database",
                 "Database name%n"
                 + "Optional, uses the PGDATABASE environmental variable if available",
                 String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:postgresql:.*", url);
  }

}
