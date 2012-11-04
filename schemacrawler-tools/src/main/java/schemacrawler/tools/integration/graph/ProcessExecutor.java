/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.integration.graph;


import static sf.util.Utility.containsWhitespace;
import static sf.util.Utility.readFully;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ProcessExecutor
{

  final class StreamReader
    implements Callable<String>
  {

    private final InputStream in;

    private StreamReader(final InputStream in)
    {
      if (in == null)
      {
        throw new RuntimeException("No input stream provided");
      }
      this.in = in;
    }

    @Override
    public String call()
      throws Exception
    {
      final Reader reader = new BufferedReader(new InputStreamReader(in));
      return readFully(reader);
    }

  }

  private static final Logger LOGGER = Logger.getLogger(ProcessExecutor.class
    .getName());

  static private String createCommandLine(final List<String> command)
  {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (final String arg: command)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        sb.append(" ");
      }
      if (containsWhitespace(arg))
      {
        sb.append("\"").append(arg).append("\"");
      }
      else
      {
        sb.append(arg);
      }
    }
    return sb.toString();
  }

  private final List<String> command;
  private String processOutput;

  private String processError;

  public ProcessExecutor(final List<String> command)
    throws IOException
  {
    if (command == null || command.isEmpty())
    {
      throw new RuntimeException("No command provided");
    }
    this.command = command;
    LOGGER.log(Level.CONFIG, command.toString());
  }

  public int execute()
    throws IOException
  {
    LOGGER.log(Level.CONFIG, "Executing:\n" + createCommandLine(command));

    final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    try
    {

      final ProcessBuilder processBuilder = new ProcessBuilder(command);
      final Process process = processBuilder.start();

      final FutureTask<String> inReaderTask = new FutureTask<String>(new StreamReader(process
        .getInputStream()));
      threadPool.execute(inReaderTask);
      final FutureTask<String> errReaderTask = new FutureTask<String>(new StreamReader(process
        .getErrorStream()));
      threadPool.execute(errReaderTask);

      final int exitCode = process.waitFor();

      processOutput = inReaderTask.get();
      processError = errReaderTask.get();

      return exitCode;
    }
    catch (final SecurityException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (final ExecutionException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (final InterruptedException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    finally
    {
      threadPool.shutdown();
    }
  }

  public String getProcessError()
  {
    return processError;
  }

  public String getProcessOutput()
  {
    return processOutput;
  }

}
