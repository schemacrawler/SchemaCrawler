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
package schemacrawler.test.sitegen;


import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.test.utility.*;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SiteSnapshotVariationsTest
{

  private static void run(final DatabaseConnectionInfo connectionInfo,
                          final Map<String, String> argsMap,
                          final OutputFormat outputFormat,
                          final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    final String command = "details,count,dump";

    argsMap.put("-title", "Details of Example Database");

    final Path snapshotFile = commandlineExecution(connectionInfo,
                                                   command,
                                                   argsMap,
                                                   "/hsqldb.INFORMATION_SCHEMA.config.properties",
                                                   outputFormat);
    move(snapshotFile, outputFile);
  }
  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
    throws IOException, URISyntaxException
  {
    if (directory != null)
    {
      return;
    }
    directory = testContext.resolveTargetFromRootPath(
      "_website/snapshot-examples");
  }

  @Test
  public void snapshots(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    for (final OutputFormat outputFormat : new OutputFormat[] {
      TextOutputFormat.html,
      TextOutputFormat.text,
      GraphOutputFormat.htmlx
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

      final Map<String, String> args = new HashMap<>();
      args.put("-info-level", "maximum");

      run(connectionInfo,
          args,
          outputFormat,
          directory.resolve("snapshot." + extension));
    }
  }

}
