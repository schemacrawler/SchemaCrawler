/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSchemaCreator
  implements Runnable
{

  private static final Logger LOGGER = Logger
    .getLogger(TestDatabase.class.getName());

  private static final boolean debug = Boolean.valueOf(System
    .getProperty("schemacrawler.testdb.TestSchemaCreator.debug", "false"));

  public static void main(final String[] args)
    throws Exception
  {
    final String connectionUrl = args[0];
    final String user = args[1];
    final String password = args[2];
    final String scriptsResource = args[3];

    final Connection connection = DriverManager
      .getConnection(connectionUrl, user, password);
    connection.setAutoCommit(false);
    final TestSchemaCreator schemaCreator = new TestSchemaCreator(connection,
                                                                  scriptsResource);
    schemaCreator.run();
  }

  private final Connection connection;
  private final String scriptsResource;

  public TestSchemaCreator(final Connection connection,
                           final String scriptsResource)
  {
    this.connection = requireNonNull(connection,
                                     "No database connection provided");
    this.scriptsResource = requireNonNull(scriptsResource,
                                          "No script resource provided");
  }

  @Override
  public void run()
  {
    String scriptResource = null;
    try (
        final BufferedReader hsqlScriptsReader = new BufferedReader(new InputStreamReader(TestDatabase.class
          .getResourceAsStream(scriptsResource), UTF_8));)
    {
      while ((scriptResource = hsqlScriptsReader.readLine()) != null)
      {
        if (scriptResource.trim().isEmpty())
        {
          continue;
        }
        if (scriptResource.startsWith("#"))
        {
          continue;
        }

        final String delimiter;
        if (scriptResource.startsWith("~"))
        {
          scriptResource = scriptResource.substring(1);
          delimiter = "@";
        }
        else
        {
          delimiter = ";";
        }

        try (final Reader reader = new InputStreamReader(TestDatabase.class
          .getResourceAsStream(scriptResource), UTF_8);)
        {
          if (debug)
          {
            LOGGER.log(Level.INFO, "Executing: " + scriptResource);
          }
          final SqlScript sqlScript = new SqlScript(scriptResource,
                                                    connection,
                                                    reader,
                                                    delimiter);
          sqlScript.run();
        }

        if (debug)
        {
          LOGGER.log(Level.INFO, "Complete");
        }
      }
    }
    catch (final Exception e)
    {
      final Throwable throwable = getCause(e);
      final String message = String
        .format("Script: %s -- %s", scriptResource, throwable.getMessage());

      System.err.println(message);
      LOGGER.log(Level.WARNING, message, throwable);

      throw new RuntimeException(message, throwable);
    }
  }

  private Throwable getCause(final Throwable e)
  {
    Throwable cause = null;
    Throwable result = e;

    while (null != (cause = result.getCause()) && result != cause)
    {
      result = cause;
    }
    return result;
  }

}
