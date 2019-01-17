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
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SchemaCrawlerXmlOutputTest
{

  private static final String XML_OUTPUT = "xml_output/";

  @Test
  public void validCountXMLOutput(final Connection connection)
    throws Exception
  {
    // clean(XML_OUTPUT);

    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(connection,
                        SchemaTextDetailType.details.name(),
                        failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void validXMLOutput(final Connection connection)
    throws Exception
  {
    // clean(XML_OUTPUT);

    final List<String> failures = new ArrayList<>();

    checkValidXmlOutput(connection, Operation.count.name(), failures);
    checkValidXmlOutput(connection, Operation.dump.name(), failures);
    checkValidXmlOutput(connection,
                        SchemaTextDetailType.brief.name(),
                        failures);
    checkValidXmlOutput(connection,
                        SchemaTextDetailType.schema.name(),
                        failures);
    checkValidXmlOutput(connection,
                        SchemaTextDetailType.details.name(),
                        failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void checkValidXmlOutput(final Connection connection,
                                   final String command,
                                   final List<String> failures)
    throws IOException, Exception, SchemaCrawlerException
  {
    final String referenceFile = command + ".html";
    final Path testOutputFile = IOUtility
      .createTempFilePath(referenceFile, TextOutputFormat.html.getFormat());

    final OutputOptions outputOptions = OutputOptionsBuilder
      .newOutputOptions(TextOutputFormat.html, testOutputFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);

    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) FieldUtils
      .readField(executable, "schemaCrawlerOptions", true);
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().fromOptions(schemaCrawlerOptions)
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum())
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
      .includeAllRoutines();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.sortTables().noSchemaCrawlerInfo(false)
      .showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());
    executable.setOutputOptions(outputOptions);
    executable.setConnection(connection);
    executable.execute();

    failures.addAll(compareOutput(XML_OUTPUT + referenceFile,
                                  testOutputFile,
                                  TextOutputFormat.html.getFormat()));
  }

}
