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

package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.*;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.IOUtility;

public class SchemaCrawlerXmlOutputTest
  extends BaseDatabaseTest
{

  private static final String XML_OUTPUT = "xml_output/";

  @Test
  public void validCountXMLOutput()
    throws Exception
  {
    // clean(XML_OUTPUT);

    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(SchemaTextDetailType.details.name(), failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void validXMLOutput()
    throws Exception
  {
    // clean(XML_OUTPUT);

    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(Operation.count.name(), failures);
    checkValidXmlOutput(Operation.dump.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.brief.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.schema.name(), failures);
    checkValidXmlOutput(SchemaTextDetailType.details.name(), failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void checkValidXmlOutput(final String command,
                                   final List<String> failures)
    throws IOException, Exception, SchemaCrawlerException
  {
    final String referenceFile = command + ".html";
    final Path testOutputFile = IOUtility
      .createTempFilePath(referenceFile, TextOutputFormat.html.getFormat());

    final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.html,
                                                          testOutputFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);

    final SchemaCrawlerOptions schemaCrawlerOptions = executable
      .getSchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.minimum());
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
    schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setAlphabeticalSortForTables(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    executable
      .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
        .toConfig());
    executable.setOutputOptions(outputOptions);
    final Connection connection = getConnection();
    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = SchemaCrawlerUtility
        .matchDatabaseSpecificOverrideOptions(connection);
    executable.execute(connection, databaseSpecificOverrideOptions);

    failures.addAll(compareOutput(XML_OUTPUT + referenceFile,
                                  testOutputFile,
                                  TextOutputFormat.html.getFormat()));
  }

}
