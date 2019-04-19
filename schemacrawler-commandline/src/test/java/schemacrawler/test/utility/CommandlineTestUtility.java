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
import static java.nio.file.StandardOpenOption.*;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import picocli.CommandLine;
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
    try (final TestWriter out = testout)
    {
      final Map<String, String> commandlineArgsMap = new HashMap<>();
      commandlineArgsMap.put("-url", connectionInfo.getConnectionUrl());
      commandlineArgsMap.put("-user", "sa");
      commandlineArgsMap.put("-password", "");
      if (propertiesFile != null)
      {
        commandlineArgsMap.put("g", propertiesFile.toString());
      }
      commandlineArgsMap.put("c", command);
      commandlineArgsMap.put("-output-format", outputFormatValue);
      commandlineArgsMap.put("-output-file", out.toString());

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
    config.toProperties()
      .store(tempFileWriter, "Store config to temporary file for testing");

    return tempFile;
  }

  public static void parseCommand(final Object object, String[] args)
  {

    newCommandLine(object).parse(args);
  }

  public static void runCommandInTest(final Object object, String[] args)
  {

    class ThrowExceptionHandler<R>
      extends CommandLine.AbstractHandler<R, ThrowExceptionHandler<R>>
      implements CommandLine.IExceptionHandler2<R>
    {
      public List<Object> handleException(CommandLine.ParameterException ex,
                                          PrintStream out,
                                          CommandLine.Help.Ansi ansi,
                                          String... args)
      {
        internalHandleParseException(ex, out, ansi, args);
        return Collections.<Object>emptyList();
      }

      /**
       * Prints the message of the specified exception, followed by the usage message for the command or subcommand
       * whose input was invalid, to the stream returned by {@link #err()}.
       *
       * @param ex   the ParameterException describing the problem that occurred while parsing the command line arguments,
       *             and the CommandLine representing the command or subcommand whose input was invalid
       * @param args the command line arguments that could not be parsed
       * @return the empty list
       * @since 3.0
       */
      public R handleParseException(CommandLine.ParameterException ex,
                                    String[] args)
      {
        internalHandleParseException(ex, err(), ansi(), args);
        return returnResultOrExit(null);
      }

      private void internalHandleParseException(CommandLine.ParameterException ex,
                                                PrintStream out,
                                                CommandLine.Help.Ansi ansi,
                                                String[] args)
      {
        throw ex;
      }

      /**
       * This implementation always simply rethrows the specified exception.
       *
       * @param ex          the ExecutionException describing the problem that occurred while executing the {@code Runnable} or {@code Callable} command
       * @param parseResult the result of parsing the command line arguments
       * @return nothing: this method always rethrows the specified exception
       * @throws CommandLine.ExecutionException always rethrows the specified exception
       * @since 3.0
       */
      public R handleExecutionException(CommandLine.ExecutionException ex,
                                        CommandLine.ParseResult parseResult)
      {
        return throwOrExit(ex);
      }

      @Override
      protected ThrowExceptionHandler<R> self()
      {
        return this;
      }
    }

    newCommandLine(object).parseWithHandlers(new picocli.CommandLine.RunLast(),
                                             new ThrowExceptionHandler(),
                                             args);
  }

  private CommandlineTestUtility()
  {
    // Prevent instantiation
  }

}
