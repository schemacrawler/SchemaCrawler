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
package schemacrawler.server.mysql;


import static java.util.Objects.requireNonNull;
import static schemacrawler.server.mysql.MySQLUtility.getEnumValues;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;

public final class MySQLDatabaseConnector
  extends DatabaseConnector
{

  private static final long serialVersionUID = 1456580846425210048L;

  private static final EnumDataTypeHelper enumDataTypeHelper =
    (column, columnDataType, connection) -> {
      requireNonNull(column, "No column provided");
      final List<String> enumValues = getEnumValues(column);
      return new EnumDataTypeInfo(!enumValues.isEmpty(), false, enumValues);
    };
  public static final BiConsumer<InformationSchemaViewsBuilder, Connection>
    informationSchemaBuilderConsumer =
    (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder.fromResourceFolder(
      "/mysql.information_schema");

  public MySQLDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("mysql", "MySQL"),
          new ClasspathInputResource("/schemacrawler-mysql.config.properties"),
          informationSchemaBuilderConsumer,
          enumDataTypeHelper);
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=mysql%n" + "Loads SchemaCrawler plug-in for MySQL",
                 String.class)
      .addOption("host",
                 "Host name%n" + "Optional, defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n" + "Optional, defaults to 3306",
                 Integer.class)
      .addOption("database", "Database name", String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:(mysql|mariadb):.*", url);
  }

}
