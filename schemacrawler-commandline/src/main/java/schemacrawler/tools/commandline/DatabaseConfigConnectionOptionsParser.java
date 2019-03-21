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
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.util.Map;

import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
public final class DatabaseConfigConnectionOptionsParser
  implements OptionsParser<Config>
{

  private final CommandLine commandLine;

  @CommandLine.Option(names = {
    "--host" }, description = "Database server host")
  private String host;

  @CommandLine.Option(names = {
    "--port" }, description = "Database server port")
  private int port;

  @CommandLine.Option(names = {
    "--database" }, description = "Database server host")
  private String database;

  @CommandLine.Option(names = {
    "--urlx" }, description = "JDBC URL additional properties")
  private Map<String, String> urlx;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public DatabaseConfigConnectionOptionsParser()
  {
    commandLine = newCommandLine(this);
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

  @Override
  public Config parse(final String[] args)
  {
    commandLine.parse(args);

    final Config config = new Config();

    if (host != null)
    {
      config.put("host", host);
    }

    if (port < 0 || port > 65535)
    {
      throw new SchemaCrawlerCommandLineException(
        "Please provide a valid value for port, " + port);
    }
    if (port > 0)
    {
      config.put("port", String.valueOf(port));
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
