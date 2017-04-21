/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.IOUtility;

public class HideEmptyTablesCommandLineTest
  extends BaseDatabaseTest
{

  private static final String HIDE_EMPTY_TABLES_OUTPUT = "hide_empty_tables_output/";

  @Test
  public void hideEmptyTables()
    throws Exception
  {
    clean(HIDE_EMPTY_TABLES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.maximum;

    final String referenceFile = "hideEmptyTables.txt";
    final Path testOutputFile = IOUtility.createTempFilePath(referenceFile,
                                                             "data");

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, String> args = new HashMap<>();
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");
    args.put("infolevel", infoLevel.name());
    args.put("command", schemaTextDetailType.name());
    args.put("outputformat", outputFormat.getFormat());
    args.put("outputfile", testOutputFile.toString());
    args.put("noinfo", "true");
    args.put("routines", "");
    args.put("hideemptytables", "true");

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
