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

package schemacrawler.tools.commandline.command;


import static java.util.stream.Collectors.joining;
import static sf.util.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.AvailableServers;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLineException;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import sf.util.TemplatingUtility;

public class DatabaseConfigConnectionOptions
  implements DatabaseConnectable
{

  private static Map<String, String> parseConnectionProperties(final Config connectionProperties)
  {
    final String URLX = "urlx";
    final String connectionPropertiesString = connectionProperties.get(URLX);
    final Map<String, String> urlxProperties = new HashMap<>();
    if (!isBlank(connectionPropertiesString))
    {
      for (final String property : connectionPropertiesString.split(";"))
      {
        if (!isBlank(property))
        {
          final String[] propertyValues = property.split("=");
          if (propertyValues.length >= 2)
          {
            final String key = propertyValues[0];
            final String value = propertyValues[1];
            if (key != null && value != null)
            {
              // Properties is based on Hashtable, which cannot take
              // null keys or values
              urlxProperties.put(key, value);
            }
          }
        }
      }
    }

    return urlxProperties;
  }

  private static String parseConnectionUrl(final Config connectionProperties)
  {
    final String URL = "url";
    final String oldConnectionUrl = connectionProperties.get(URL);

    // Substitute parameters
    TemplatingUtility.substituteVariables(connectionProperties);
    final String connectionUrl = connectionProperties.get(URL);

    // Check that all required parameters have been substituted
    final Set<String> unmatchedVariables = TemplatingUtility.extractTemplateVariables(
      connectionUrl);
    if (!unmatchedVariables.isEmpty())
    {
      throw new IllegalArgumentException(String.format(
        "Insufficient parameters for database connection URL: missing %s",
        unmatchedVariables));
    }

    // Reset old connection URL
    connectionProperties.put(URL, oldConnectionUrl);

    return connectionUrl;
  }

  @CommandLine.Option(names = {
    "--database"
  },
                      description = "Database name")
  private String database;
  @CommandLine.Option(names = {
    "--server"
  },
                      required = true,
                      description = "Database server type",
                      completionCandidates = AvailableServers.class)
  private String databaseSystemIdentifier;
  @CommandLine.Option(names = {
    "--host"
  },
                      description = "Database server host")
  private String host;
  @CommandLine.Option(names = {
    "--port"
  },
                      description = "Database server port")
  private Integer port;
  @CommandLine.Option(names = {
    "--urlx"
  },
                      description = "JDBC URL additional properties")
  private Map<String, String> urlx;

  @Override
  public DatabaseConnector getDatabaseConnector()
  {
    try
    {
      return new DatabaseConnectorRegistry().lookupDatabaseConnector(
        databaseSystemIdentifier);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerCommandLineException(
        "Please provide database connection options",
        e);
    }
  }

  @Override
  public DatabaseConnectionSource toDatabaseConnectionSource(final Config config)
  {
    // Override host, port, database and urlx
    config.putAll(getDatabaseConnectionConfig());

    // Substitute templated URL with provided value
    final String connectionUrl = parseConnectionUrl(config);
    // Get additional driver properties
    final Map<String, String> connectionProperties = parseConnectionProperties(
      config);

    final DatabaseConnectionSource databaseConnectionSource = new DatabaseConnectionSource(
      connectionUrl,
      connectionProperties);
    return databaseConnectionSource;
  }

  private Config getDatabaseConnectionConfig()
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
        throw new IllegalArgumentException("Invalid port number, " + port);
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
      final String urlxValue = urlx.entrySet()
                                   .stream()
                                   .map(Object::toString)
                                   .collect(joining("&"));
      config.put("urlx", urlxValue);
    }

    return config;
  }

}
