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
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.*;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

@ExtendWith(TestLoggingExtension.class)
public class SqliteCommandlineTest
{

  private DatabaseConnector dbConnector;

  @BeforeEach
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

    final Connection connection = null;
    assertThat(dbConnector.getSchemaRetrievalOptionsBuilder(connection)
                          .toOptions()
                          .getIdentifierQuoteString(), is("\""));
  }

  @Test
  public void testSqliteMain()
    throws Exception
  {
    final OutputFormat outputFormat = TextOutputFormat.text;
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final Path sqliteDbFile = IOUtility.createTempFilePath("sc", ".db")
                                         .normalize()
                                         .toAbsolutePath();

      TestSchemaCreator.main(new String[] {
        "jdbc:sqlite:" + sqliteDbFile, null, null, "/sqlite.scripts.txt"
      });

      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("-server", "sqlite");
      argsMap.put("-database", sqliteDbFile.toString());
      argsMap.put("-no-info", Boolean.TRUE.toString());
      argsMap.put("-command", "list");
      argsMap.put("-info-level", InfoLevel.minimum.name());
      argsMap.put("-output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(
                 "sqlite.main.list." + outputFormat.getFormat())));
  }

}
