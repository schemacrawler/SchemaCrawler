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

package schemacrawler.tools.commandline;


import static java.util.stream.Collectors.joining;

import java.util.Map;

import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class DatabaseConfigConnectionOptions
  implements DatabaseConnectable
{

  @CommandLine.Option(names = {
    "--server" }, required = true, description = "Database server type")
  private String databaseSystemIdentifier;
  @CommandLine.Option(names = {
    "--host" }, description = "Database server host")
  private String host;
  @CommandLine.Option(names = {
    "--port" }, description = "Database server port")
  private Integer port;
  @CommandLine.Option(names = {
    "--database" }, description = "Database name")
  private String database;
  @CommandLine.Option(names = {
    "--urlx" }, description = "JDBC URL additional properties")
  private Map<String, String> urlx;

  @Override
  public DatabaseConnector getDatabaseConnector()
  {
    try
    {
      return new DatabaseConnectorRegistry()
        .lookupDatabaseConnector(databaseSystemIdentifier);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerCommandLineException(
        "Please provide database connection options",
        e);
    }
  }

  @Override
  public Config getDatabaseConnectionConfig()
  {
    final Config config = new Config();

    if (host != null)
    {
      config.put("host", host);
    }

    if (port != null)
    {
      if (port < 0 || port > 65535)
      {
        throw new SchemaCrawlerCommandLineException(
          "Please provide a valid value for port, " + port);
      }
      if (port > 0)
      {
        config.put("port", String.valueOf(port));
      }
    }

    if (database != null)
    {
      config.put("database", database);
    }

    if (urlx != null && !urlx.isEmpty())
    {
      final String urlxValue = urlx.entrySet().stream().map(Object::toString)
        .collect(joining("&"));
      config.put("urlx", urlxValue);
    }

    return config;
  }
}
