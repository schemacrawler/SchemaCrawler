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
package schemacrawler.server.db2;


import static schemacrawler.plugin.EnumDataTypeHelper.noOpEnumDataTypeHelper;

import java.io.IOException;
import java.sql.Connection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.crawl.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;
import sf.util.SchemaCrawlerLogger;

public final class DB2DatabaseConnector
  extends DatabaseConnector
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(DB2DatabaseConnector.class.getName());

  public DB2DatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("db2", "IBM DB2"),
          new ClasspathInputResource("/schemacrawler-db2.config.properties"),
          (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder.fromResourceFolder(
            "/db2.information_schema"), noOpEnumDataTypeHelper);
  }

  @Override
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      super.getSchemaRetrievalOptionsBuilder(connection);
    schemaRetrievalOptionsBuilder.withTableColumnRetrievalStrategy(
      MetadataRetrievalStrategy.metadata_all);
    return schemaRetrievalOptionsBuilder;
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=db2%n" + "Loads SchemaCrawler plug-in for IBM DB2",
                 String.class)
      .addOption("host",
                 "Host name%n" + "Optional, defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n" + "Optional, defaults to 50000",
                 Integer.class)
      .addOption("database", "Database name", String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:db2:.*", url);
  }

}
