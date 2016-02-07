/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.containsWhitespace;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readFully;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;

public class ProcessExecutor
  implements Callable<Integer>
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

  private static final Logger LOGGER = Logger
    .getLogger(ProcessExecutor.class.getName());

  private List<String> command;
  private String processOutput;
  private String processError;
  private int exitCode;

  @Override
  public Integer call()
    throws IOException
  {
    requireNonNull(command, "No command provided");
    if (command.isEmpty())
    {
      throw new IOException("No command provided");
    }

    LOGGER.log(Level.CONFIG, new StringFormat("Executing:%n%s", command));

    final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    try
    {

      final ProcessBuilder processBuilder = new ProcessBuilder(command);
      final Process process = processBuilder.start();

      final FutureTask<String> inReaderTask = new FutureTask<>(new StreamReader(process
        .getInputStream()));
      threadPool.execute(inReaderTask);
      final FutureTask<String> errReaderTask = new FutureTask<>(new StreamReader(process
        .getErrorStream()));
      threadPool.execute(errReaderTask);

      exitCode = process.waitFor();

      processOutput = inReaderTask.get();
      processError = errReaderTask.get();

      return exitCode;
    }
    catch (final SecurityException | ExecutionException
        | InterruptedException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (final Throwable t)
    {
      LOGGER.log(Level.SEVERE, t.getMessage(), t);
      throw new IOException(t.getMessage(), t);
    }
    finally
    {
      threadPool.shutdown();
    }
  }

  public List<String> getCommand()
  {
    return command;
  }

  public int getExitCode()
  {
    return exitCode;
  }

  public String getProcessError()
  {
    return processError;
  }

  public String getProcessOutput()
  {
    return processOutput;
  }

  public void setCommandLine(final List<String> args)
  {
    requireNonNull(args, "No command provided");
    if (args.isEmpty())
    {
      throw new IllegalArgumentException("No command provided");
    }

    command = new ArrayList<String>();
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
  }

}
