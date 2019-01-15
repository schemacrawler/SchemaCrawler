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
package schemacrawler.test;


import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.BaseSchemaCrawlerTest;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class NoEmptyTablesCommandLineTest
  extends BaseSchemaCrawlerTest
{

  private static final String HIDE_EMPTY_TABLES_OUTPUT = "no_empty_tables_output/";

  @Test
  public void noEmptyTables(final TestContext testContext,
                            final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    clean(HIDE_EMPTY_TABLES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.maximum;

    final String referenceFile = testContext.testMethodName() + ".txt";
    final Path testOutputFile = IOUtility.createTempFilePath(referenceFile,
                                                             "data");

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, String> args = new HashMap<>();
    args.put("url", connectionInfo.getConnectionUrl());
    args.put("user", "sa");
    args.put("password", "");
    args.put("infolevel", infoLevel.name());
    args.put("command", schemaTextDetailType.name());
    args.put("outputformat", outputFormat.getFormat());
    args.put("outputfile", testOutputFile.toString());
    args.put("noinfo", "true");
    args.put("routines", "");
    args.put("noemptytables", "true");

    Main.main(flattenCommandlineArgs(args));

    failures.addAll(compareOutput(HIDE_EMPTY_TABLES_OUTPUT + referenceFile,
                                  testOutputFile,
                                  outputFormat.getFormat()));

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
