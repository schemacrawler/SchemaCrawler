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


import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

public class SQLiteExecuableTest
  extends BaseSqliteTest
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void dump()
    throws Exception
  {
    run(testName.currentMethodFullName(), "dump");
  }

  @Test
  public void count()
    throws Exception
  {
    run(testName.currentMethodFullName(), "count");
  }

  private void run(final String currentMethodFullName, final String command)
    throws Exception
  {
    final Path sqliteDbFile = IOUtility.createTempFilePath("sc", ".db")
      .normalize().toAbsolutePath();

    TestSchemaCreator.main(new String[] {
                                          "jdbc:sqlite:" + sqliteDbFile,
                                          null,
                                          null,
                                          "/sqlite.scripts.txt" });

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final SchemaTextOptions textOptions = new SchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable
      .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
        .toConfig());

    executeExecutable(sqliteDbFile, executable, currentMethodFullName);
  }

}
