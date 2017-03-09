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
package schemacrawler.tools.integration.graph;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.move;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.readResourceFully;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.ProcessExecutor;
import sf.util.FileContents;
import sf.util.StringFormat;

public class GraphProcessExecutor
  extends ProcessExecutor
{

  private static final Logger LOGGER = Logger
    .getLogger(GraphProcessExecutor.class.getName());

  private final Path outputFile;
  private final Path dotFile;

  public GraphProcessExecutor(final Path dotFile,
                              final Path outputFile,
                              final GraphOptions graphOptions,
                              final GraphOutputFormat graphOutputFormat)
    throws IOException
  {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No graph output file provided");
    requireNonNull(graphOptions, "No graph options provided");
    requireNonNull(graphOutputFormat, "No graph output format provided");

    if (!(exists(dotFile) && isRegularFile(dotFile) && isReadable(dotFile)))
    {
      throw new IOException("Cannot read DOT file, " + dotFile);
    }
    this.dotFile = dotFile;

    final Path outputDir = requireNonNull(outputFile.getParent(),
                                          "Invalid output directory");
    if (isDirectory(outputFile) || !exists(outputDir)
        || !isDirectory(outputDir))
    {
      throw new IOException("Cannot write graph file, " + dotFile);
    }
    this.outputFile = outputFile;

    if (graphOutputFormat == GraphOutputFormat.scdot)
    {
      return;
    }

    createDiagramCommand(dotFile, outputFile, graphOptions, graphOutputFormat);
    LOGGER
      .log(Level.INFO,
           "Generating diagram using Graphviz:\n" + getCommand().toString());

  }

  @Override
  public Integer call()
    throws Exception
  {
    // For scdot, we may not need to run the process
    final List<String> command = getCommand();
    if (command == null || command.isEmpty())
    {
      return 0;
    }

    final Integer exitCode = super.call();
    final boolean isProcessInError = exitCode == null || exitCode != 0;

    LOGGER.log(Level.INFO, new FileContents(getProcessOutput()));
    final Supplier<String> processError = new FileContents(getProcessOutput());
    if (isProcessInError)
    {
      LOGGER.log(Level.SEVERE,
                 new StringFormat("Process returned exit code %d%n%s",
                                  exitCode,
                                  processError));
      captureRecovery();
    }
    else
    {
      LOGGER.log(Level.WARNING, processError);
      LOGGER.log(Level.INFO,
                 new StringFormat("Generated diagram <%s>", outputFile));
    }

    return exitCode;
  }

  public Path getDotFile()
  {
    return dotFile;
  }

  public Path getOutputFile()
  {
    return outputFile;
  }

  private void captureRecovery()
    throws SchemaCrawlerException
  {
    // Move DOT file to current directory
    final Path movedDotFile = outputFile.normalize().getParent()
      .resolve(dotFile.getFileName());

    // Print command to run
    final List<String> command = getCommand();
    command.remove(command.size() - 1);
    command.remove(command.size() - 1);
    command.add(outputFile.toString());
    command.add(movedDotFile.toString());

    final String message = String
      .format("%s%nGenerate your diagram manually, using:%n%s",
              readResourceFully("/dot.error.txt"),
              String.join(" ", quoteCommandLine(command)));

    try
    {
      move(dotFile, movedDotFile);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException(String.format("Could not move %s to %s",
                                                     dotFile,
                                                     movedDotFile),
                                       e);
    }

    LOGGER.log(Level.SEVERE, message);
    throw new SchemaCrawlerException(message);
  }

  private void createDiagramCommand(final Path dotFile,
                                    final Path outputFile,
                                    final GraphOptions graphOptions,
                                    final GraphOutputFormat graphOutputFormat)
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");

    if (graphOptions != null)
    {
      command.addAll(graphOptions.getGraphvizOpts());
    }
    command.add("-T");
    command.add(graphOutputFormat.getFormat());
    command.add("-o");
    command.add(outputFile.toString());
    command.add(dotFile.toString());

    setCommandLine(command);
  }

}
