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
package schemacrawler.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.createTempFilePath;
import static sf.util.Utility.containsWhitespace;
import static sf.util.Utility.isBlank;

import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class ProcessExecutor
  implements Callable<Integer>
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ProcessExecutor.class.getName());

  private List<String> command;
  private Path processOutput;
  private Path processError;
  private int exitCode;

  @Override
  public Integer call()
    throws Exception
  {
    requireNonNull(command, "No command provided");

    try
    {
      processOutput = createTempFilePath("scdot", "stdout");
      processError = createTempFilePath("scdot", "stderr");

      if (command.isEmpty())
      {
        return null;
      }

      LOGGER.log(Level.CONFIG, new StringFormat("Executing:%n%s", command));

      final ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectOutput(Redirect.appendTo(processOutput.toFile()));
      processBuilder.redirectOutput(Redirect.appendTo(processError.toFile()));

      final Process process = processBuilder.start();
      exitCode = process.waitFor();
    }
    catch (final Throwable t)
    {
      if (exitCode == 0)
      {
        exitCode = Integer.MIN_VALUE;
      }
      LOGGER.log(Level.SEVERE, t.getMessage(), t);
    }

    return exitCode;
  }

  public List<String> getCommand()
  {
    return command;
  }

  public int getExitCode()
  {
    return exitCode;
  }

  public Path getProcessError()
  {
    return processError;
  }

  public Path getProcessOutput()
  {
    return processOutput;
  }

  public void setCommandLine(final List<String> args)
  {
    command = quoteCommandLine(args);
  }

  /**
   * Quotes command-line arguments that have spaces, and removes blank
   * items.
   *
   * @param args
   *        Command-line arguments
   * @return Quoted command-line arguments
   */
  protected List<String> quoteCommandLine(final List<String> args)
  {
    requireNonNull(args, "No command provided");
    if (args.isEmpty())
    {
      throw new IllegalArgumentException("No command provided");
    }

    final List<String> command = new ArrayList<>();
    for (final String arg: args)
    {
      if (isBlank(arg))
      {
        continue;
      }
      else if (containsWhitespace(arg))
      {
        command.add(String.format("\"%s\"", arg));
      }
      else
      {
        command.add(arg);
      }
    }

    return command;
  }

}
