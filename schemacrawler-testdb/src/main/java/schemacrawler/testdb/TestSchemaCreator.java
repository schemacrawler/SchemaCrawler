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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSchemaCreator
  implements Runnable
{

  enum ProcessCode
  {
   process,
   skip,
   delimit;
  }

  private static final Logger LOGGER = Logger
    .getLogger(TestSchemaCreator.class.getName());

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

    final Set<String> scriptResources = getBooksSchemaFiles();

    String scriptResourceLine = null;
    String scriptResource = null;
    ProcessCode processCode = null;
    try (
        final BufferedReader scriptsReader = new BufferedReader(new InputStreamReader(TestSchemaCreator.class
          .getResourceAsStream(scriptsResource), UTF_8));)
    {
      while ((scriptResourceLine = scriptsReader.readLine()) != null)
      {
        if (scriptResourceLine.trim().isEmpty())
        {
          continue;
        }

        if (scriptResourceLine.startsWith("#"))
        {
          processCode = ProcessCode.skip;
          scriptResource = scriptResourceLine.substring(1);
        }
        else if (scriptResourceLine.startsWith("~"))
        {
          processCode = ProcessCode.delimit;
          scriptResource = scriptResourceLine.substring(1);
        }
        else
        {
          processCode = ProcessCode.process;
          scriptResource = scriptResourceLine;
        }

        scriptResources.remove(scriptResource);

        final String delimiter;
        switch (processCode)
        {
          case process:
            delimiter = ";";
            break;

          case delimit:
            delimiter = "@";
            break;
          case skip:
          default:
            continue;
        }

        try (final Reader reader = new InputStreamReader(TestSchemaCreator.class
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

    if (!scriptResources.isEmpty())
    {
      final List<String> scriptResourcesList = new ArrayList<>(scriptResources);
      Collections.sort(scriptResourcesList);
      throw new RuntimeException("Did not process\n"
                                 + String.join("\n", scriptResourcesList));
    }
  }

  private Set<String> getBooksSchemaFiles()
  {
    final String DB_BOOKS = "/db/books/";
    final Set<String> filenames = new HashSet<>();

    try (
        final InputStream in = TestSchemaCreator.class
          .getResourceAsStream(DB_BOOKS);
        final BufferedReader br = new BufferedReader(new InputStreamReader(in)))
    {
      String resource;
      while ((resource = br.readLine()) != null)
      {
        if (!resource.contains("drop"))
        {
          filenames.add(DB_BOOKS + resource);
        }
      }
    }
    catch (final IOException e)
    {
      throw new RuntimeException("Cannot read resource " + DB_BOOKS, e);
    }

    return filenames;
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
