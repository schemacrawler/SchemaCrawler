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


import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.ioresource.ClasspathInputResource;

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
            "/db2.information_schema"),
          (schemaRetrievalOptionsBuilder, connection) -> {},
          (limitOptionsBuilder, connection) -> {});
  }

  @Override
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      super.getSchemaRetrievalOptionsBuilder(connection);
    schemaRetrievalOptionsBuilder.with(tableColumnsRetrievalStrategy,
                                       data_dictionary_all);
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

  @Override
  protected String constructConnectionUrl(final String providedHost,
      final Integer providedPort, final String providedDatabase,
      final Map<String, String> urlx)
  {

    final String defaultHost = "localhost";
    final int defaultPort = 50000;
    final String defaultDatabase = "";
    final String urlFormat =
        "jdbc:db2://%s:%d/%s:retrieveMessagesFromServerOnGetMessage=true;";

    final String host;
    if (isBlank(providedHost))
    {
      host = defaultHost;
    } else
    {
      host = providedHost;
    }

    final int port;
    if (providedPort == null || providedPort < 0 || providedPort > 65535)
    {
      port = defaultPort;
    } else
    {
      port = providedPort;
    }

    final String database;
    if (isBlank(providedDatabase))
    {
      database = defaultDatabase;
    } else
    {
      database = providedDatabase;
    }

    final String url = String.format(urlFormat, host, port, database);

    return url;
  }
  
}
