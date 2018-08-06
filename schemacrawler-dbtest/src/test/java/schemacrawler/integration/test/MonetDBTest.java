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
package schemacrawler.integration.test;


import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedDatabase;
import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class MonetDBTest
  extends BaseAdditionalDatabaseTest
{

  private boolean isDatabaseRunning;

  @After
  public void stopDatabaseServer()
    throws MonetDBEmbeddedException
  {
    if (isDatabaseRunning)
    {
      MonetDBEmbeddedDatabase.stopDatabase();
    }
  }

  @Before
  public void createDatabase()
  {
    try
    {
      // Set up native libraries, and load JDBC driver
      final Path directoryPath = Files.createTempDirectory("monetdbjavalite");
      MonetDBEmbeddedDatabase.startDatabase(directoryPath.toString());
      if (MonetDBEmbeddedDatabase.isDatabaseRunning())
      {
        MonetDBEmbeddedDatabase.stopDatabase();
      }

      createDatabase("jdbc:monetdb:embedded::memory:", null, null, "/monetdb.scripts.txt");

      isDatabaseRunning = true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      // Do not run if MonetDBLite cannot be loaded
      isDatabaseRunning = false;
    }
  }

  @Test
  public void testMonetDBWithConnection()
    throws Exception
  {
    if (!isDatabaseRunning)
    {
      System.out.println("Did not run MonetDB test");
      return;
    }

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noIndexNames().showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(executable, "text", "testMonetDBWithConnection.txt");
  }

}
