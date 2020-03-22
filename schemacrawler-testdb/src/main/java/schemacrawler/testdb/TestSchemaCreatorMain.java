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
package schemacrawler.testdb;


import picocli.CommandLine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;

@CommandLine.Command(description = "Creates a test database schema for testing SchemaCrawler",
  name = "Test Schema Creator", mixinStandardHelpOptions = true, version = "16.5.2")
public class TestSchemaCreatorMain
  implements Callable<Integer>
{

  @CommandLine.Option(names = {
    "--url"
  }, required = true, description = "JDBC connection URL to the database", paramLabel = "<url>")
  private String connectionUrl;
  @CommandLine.Option(names = {
    "--user"
  }, description = "Database user name", paramLabel = "<user>")
  private String user;
  @CommandLine.Option(names = {
    "--password"
  }, description = "Database password", paramLabel = "<password>")
  private String passwordProvided;

  private TestSchemaCreatorMain()
  {
  }

  public static void main(String... args)
  {
    final int exitCode = new CommandLine(new TestSchemaCreatorMain()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call()
  {
    try (
      final Connection connection = DriverManager.getConnection(connectionUrl,
        user,
        passwordProvided);)
    {
      connection.setAutoCommit(false);
      final String scriptsResource = findScriptsResource();
      final TestSchemaCreator testSchemaCreator = new TestSchemaCreator(connection, scriptsResource);
      testSchemaCreator.run();
    } catch (final Exception e)
    {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  private String findScriptsResource()
  {
    if (connectionUrl == null)
    {
      throw new IllegalArgumentException("No connection URL provided");
    }
    final String[] splitUrl = connectionUrl.split(":");
    if (splitUrl.length >= 2)
    {
      return String.format("/%s.scripts.txt", splitUrl[1]);
    } else
    {
      throw new IllegalArgumentException("No connection URL provided");
    }
  }

}
