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
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchemaCreator
  implements Runnable
{

  private static final Logger LOGGER = Logger
    .getLogger(TestDatabase.class.getName());

  private final Connection connection;
  private final String scriptsResource;

  public SchemaCreator(final Connection connection,
                       final String scriptsResource)
  {
    this.connection = requireNonNull(connection);
    this.scriptsResource = requireNonNull(scriptsResource);
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

        try (final Reader reader = new InputStreamReader(TestDatabase.class
          .getResourceAsStream(scriptResource), UTF_8);)
        {
          final SqlScript sqlScript = new SqlScript(scriptResource,
                                                    connection,
                                                    reader);
          sqlScript.run();
        }
      }
    }
    catch (final Exception e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING,
                 String
                   .format("Script: %s -- %s", scriptResource, e.getMessage()),
                 e);
      throw new RuntimeException(e);
    }
  }

}
