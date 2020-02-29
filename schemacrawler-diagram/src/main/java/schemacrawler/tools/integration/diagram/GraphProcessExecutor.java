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
package schemacrawler.tools.integration.diagram;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.readResourceFully;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.ProcessExecutor;
import sf.util.FileContents;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

final class GraphProcessExecutor
  extends AbstractGraphProcessExecutor
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(GraphProcessExecutor.class.getName());

  private final List<String> graphvizOpts;

  GraphProcessExecutor(final Path dotFile,
                       final Path outputFile,
                       final DiagramOutputFormat diagramOutputFormat,
                       final List<String> graphvizOpts)
    throws SchemaCrawlerException
  {
    super(dotFile, outputFile, diagramOutputFormat);

    this.graphvizOpts =
      requireNonNull(graphvizOpts, "No Graphviz options provided");
  }

  @Override
  public Boolean call()
  {

    final List<String> command = createDiagramCommand();
    LOGGER.log(Level.INFO,
               new StringFormat("Generating diagram using Graphviz:\n%s",
                                command.toString()));

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(command);

    Integer exitCode;
    try
    {
      exitCode = processExecutor.call();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.INFO,
                 String.format("Could not generate diagram using Graphviz:%n%s",
                               command.toString()),
                 e);
      exitCode = Integer.MIN_VALUE;
    }
    final boolean successful = exitCode != null && exitCode == 0;

    LOGGER.log(Level.INFO,
               new FileContents(processExecutor.getProcessOutput()));
    final Supplier<String> processError =
      new FileContents(processExecutor.getProcessError());
    if (!successful)
    {
      LOGGER.log(Level.SEVERE,
                 new StringFormat("Process returned exit code %d%n%s",
                                  exitCode,
                                  processError));
      showCommandline(outputFile, processExecutor.getCommand());
    }
    else
    {
      LOGGER.log(Level.WARNING, processError);
      LOGGER.log(Level.INFO,
                 new StringFormat("Generated diagram <%s>", outputFile));
    }

    return successful;
  }

  @Override
  public boolean canGenerate()
  {
    return GraphvizUtility.isGraphvizAvailable();
  }

  private List<String> createDiagramCommand()
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");

    command.addAll(graphvizOpts);

    command.add("-T");
    command.add(diagramOutputFormat.getFormat());
    command.add("-o");
    command.add(outputFile.toString());
    command.add(dotFile.toString());

    return command;
  }

  private void showCommandline(final Path outputFile,
                               final List<String> command)
  {
    if (!LOGGER.isLoggable(Level.SEVERE))
    {
      return;
    }

    // Find name of DOT file in local directory
    final Path movedDotFile = outputFile
      .normalize()
      .getParent()
      .resolve(outputFile.getFileName() + ".dot");

    // Print command to run
    command.remove(command.size() - 1);
    command.remove(command.size() - 1);
    command.add(outputFile.toString());
    command.add(movedDotFile.toString());

    LOGGER.log(Level.SEVERE,
               String.format("%s%nGenerate your diagram manually, using:%n%s",
                             readResourceFully("/dot.error.txt"),
                             String.join(" ", command)));
  }

}
