/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class SQLiteDatabaseConnector extends DatabaseConnector {

  public SQLiteDatabaseConnector() {
    super(
        new DatabaseServerType("sqlite", "SQLite"),
        url -> url != null && url.startsWith("jdbc:sqlite:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/sqlite.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder.withIdentifierQuoteString("\""),
        limitOptionsBuilder -> {},
        () ->
            DatabaseConnectionSourceBuilder.builder("jdbc:sqlite:${database}")
                .withDefaultUrlx("application_id", "SchemaCrawler")
                .withDefaultUrlx("open_mode", "2"));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server", String.class, "--server=sqlite%n" + "Loads SchemaCrawler plug-in for SQLite")
        .addOption("host", String.class, "Should be omitted")
        .addOption("port", Integer.class, "Should be omitted")
        .addOption("database", String.class, "SQLite database file path");
    return pluginCommand;
  }
}
