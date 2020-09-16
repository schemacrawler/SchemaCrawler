/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.sitegen;


import static java.nio.file.Files.deleteIfExists;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestAssertNoSystemErrOutput;
import schemacrawler.test.utility.TestAssertNoSystemOutOutput;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.integration.diagram.DiagramOutputFormat;
import schemacrawler.tools.integration.serialize.SerializationFormat;
import schemacrawler.tools.lint.executable.LintReportOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteSnapshotVariationsTest
{

  @BeforeAll
  public static void _saveConfigProperties()
    throws IOException
  {
    propertiesFile =
      copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  @BeforeAll
  public static void _setupDirectory(final TestContext testContext)
    throws IOException, URISyntaxException
  {
    snapshotsDirectory =
      testContext.resolveTargetFromRootPath("_website/snapshot-examples");
    lintReportsDirectory =
      testContext.resolveTargetFromRootPath("_website/lint-report-examples");
  }

  private static Path lintReportsDirectory;
  private static Path propertiesFile;
  private static Path snapshotsDirectory;

  @Test
  public void lintReportExamples(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    for (final OutputFormat outputFormat : LintReportOutputFormat.values())
    {
      final String extension = outputFormat.getFormat();
      run(connectionInfo,
          "lint",
          null,
          outputFormat,
          lintReportsDirectory.resolve("lint_report." + extension));
    }
  }

  @Test
  public void snapshotsExamples(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    for (final OutputFormat outputFormat : new OutputFormat[] {
      TextOutputFormat.html, TextOutputFormat.text, DiagramOutputFormat.htmlx
    })
    {
      final String extension;
      if ("htmlx".equals(outputFormat.getFormat()))
      {
        extension = "svg.html";
      }
      else
      {
        extension = outputFormat.getFormat();
      }

      run(connectionInfo,
          "details",
          new HashMap<>(),
          outputFormat,
          snapshotsDirectory.resolve("snapshot." + extension));
    }
  }

  @Test
  public void serializeExamples(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    for (final OutputFormat outputFormat : new OutputFormat[] {
      SerializationFormat.json, SerializationFormat.yaml
    })
    {
      final String extension = outputFormat.getFormat();
      run(connectionInfo,
          "serialize",
          null,
          outputFormat,
          snapshotsDirectory.resolve("snapshot." + extension));
    }
  }

  private void run(final DatabaseConnectionInfo connectionInfo,
                   final String command,
                   final Map<String, String> additionalArgsMap,
                   final OutputFormat outputFormat,
                   final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    final Map<String, String> argsMap = new HashMap<>();
    if (additionalArgsMap != null)
    {
      argsMap.putAll(additionalArgsMap);
    }
    argsMap.put("-info-level", "maximum");
    argsMap.put("-title", "Details of Example Database");

    commandlineExecution(connectionInfo,
                         command,
                         argsMap,
                         propertiesFile,
                         outputFormat.getFormat(),
                         outputFile);
  }

}
