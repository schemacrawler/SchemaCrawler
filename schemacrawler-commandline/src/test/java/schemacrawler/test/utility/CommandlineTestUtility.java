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

package schemacrawler.test.utility;


import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.options.OutputFormat;
import sf.util.IOUtility;

public final class CommandlineTestUtility
{

  public static Path commandlineExecution(final DatabaseConnectionInfo connectionInfo,
                                          final String command,
                                          final Map<String, String> argsMap,
                                          final Config config,
                                          final OutputFormat outputFormat)
    throws Exception
  {
    return commandlineExecution(connectionInfo,
                                command,
                                argsMap,
                                writeConfigToTempFile(config),
                                outputFormat.getFormat());
  }

  public static Path commandlineExecution(final DatabaseConnectionInfo connectionInfo,
                                          final String command,
                                          final Map<String, String> argsMap,
                                          final OutputFormat outputFormat)
    throws Exception
  {
    return commandlineExecution(connectionInfo,
                                command,
                                argsMap,
                                (Path) null,
                                outputFormat.getFormat());
  }

  public static Path commandlineExecution(final DatabaseConnectionInfo connectionInfo,
                                          final String command,
                                          final Map<String, String> argsMap,
                                          final String outputFormatValue)
    throws Exception
  {
    return commandlineExecution(connectionInfo,
                                command,
                                argsMap,
                                (Path) null,
                                outputFormatValue);
  }

  public static Path commandlineExecution(final DatabaseConnectionInfo connectionInfo,
                                          final String command,
                                          final Map<String, String> argsMap,
                                          final String propertiesFileResource,
                                          final OutputFormat outputFormat)
    throws Exception
  {
    final Path propertiesFile = copyResourceToTempFile(propertiesFileResource);
    return commandlineExecution(connectionInfo,
                                command,
                                argsMap,
                                propertiesFile,
                                outputFormat.getFormat());
  }

  private static Path commandlineExecution(final DatabaseConnectionInfo connectionInfo,
                                           final String command,
                                           final Map<String, String> argsMap,
                                           final Path propertiesFile,
                                           final String outputFormatValue)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> commandlineArgsMap = new HashMap<>();
      commandlineArgsMap.put("url", connectionInfo.getConnectionUrl());
      commandlineArgsMap.put("user", "sa");
      commandlineArgsMap.put("password", "");
      if (propertiesFile != null)
      {
        commandlineArgsMap.put("g", propertiesFile.toString());
      }
      commandlineArgsMap.put("command", command);
      commandlineArgsMap.put("outputformat", outputFormatValue);
      commandlineArgsMap.put("outputfile", out.toString());

      // Override and add to command-line arguments
      if (argsMap != null)
      {
        commandlineArgsMap.putAll(argsMap);
      }

      Main.main(flattenCommandlineArgs(commandlineArgsMap));
    }
    return testout.getFilePath();
  }

  private static Path writeConfigToTempFile(final Config config)
    throws IOException
  {
    if (config == null)
    {
      return null;
    }

    final Path tempFile = IOUtility.createTempFilePath("test", ".properties")
      .normalize().toAbsolutePath();

    final Writer tempFileWriter = newBufferedWriter(tempFile,
                                                    WRITE,
                                                    TRUNCATE_EXISTING,
                                                    CREATE);
    config.toProperties().store(tempFileWriter,
                                "Store config to temporary file for testing");

    return tempFile;
  }

  private CommandlineTestUtility()
  {
    // Prevent instantiation
  }

}
