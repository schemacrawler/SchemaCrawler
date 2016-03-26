/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static us.fatehi.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class TestSqliteDistribution
{

  private DatabaseConnector dbConnector;

  @Before
  public void setup()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    dbConnector = registry.lookupDatabaseConnector("sqlite");
  }

  @Test
  public void testIdentifierQuoteString()
    throws Exception
  {
    assertEquals("\"",
                 dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
                   .toOptions().getIdentifierQuoteString());
  }

  @Test
  public void testSqliteMain()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final OutputFormat outputFormat = TextOutputFormat.text;

      final Path sqliteDbFile = copyResourceToTempFile("/sc.db");

      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "sqlite");
      argsMap.put("database", sqliteDbFile.toString());
      argsMap.put("command", "details,dump,count");
      argsMap.put("infolevel", "detailed");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals("sqlite.main" + "." + outputFormat.getFormat());
    }
  }

  @Test
  public void testSupports()
    throws Exception
  {
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsCatalogs());
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsSchemas());
  }

}
