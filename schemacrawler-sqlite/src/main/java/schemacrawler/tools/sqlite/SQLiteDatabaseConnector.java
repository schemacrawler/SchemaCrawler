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
package schemacrawler.tools.sqlite;


import java.io.IOException;
import java.sql.Connection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;

public final class SQLiteDatabaseConnector
  extends DatabaseConnector
{

  public SQLiteDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("sqlite", "SQLite"),
          new ClasspathInputResource("/schemacrawler-sqlite.config.properties"),
          (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder
            .fromResourceFolder("/sqlite.information_schema"));
  }

  @Override
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = super.getSchemaRetrievalOptionsBuilder(
      connection);
    schemaRetrievalOptionsBuilder.withIdentifierQuoteString("\"");
    return schemaRetrievalOptionsBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DatabaseConnectionSource newDatabaseConnectionSource(final DatabaseConnectorOptions databaseConnectorOptions)
    throws SchemaCrawlerException
  {
    try
    {
      Class.forName("org.sqlite.JDBC");
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load SQLite JDBC driver", e);
    }

    return super.newDatabaseConnectionSource(databaseConnectorOptions);
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand.addOption("server",
                            "--server=sqlite%n"
                            + "Loads SchemaCrawler plug-in for SQLite",
                            String.class)
                 .addOption("host", "Should be omitted", String.class)
                 .addOption("port", "Should be omitted", Integer.class)
                 .addOption("database",
                            "SQLite database file path",
                            String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:sqlite:.*", url);
  }

}
