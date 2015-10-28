package schemacrawler.tools.integration.graph;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.utility.ProcessExecutor;

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

    if (isDirectory(outputFile) || !exists(outputFile.getParent())
        || !isDirectory(outputFile.getParent()))
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
           "Generating diagram using GraphViz:\n" + getCommand().toString());

  }

  @Override
  public Integer call()
    throws IOException
  {
    // For scdot, we may not need to run the process
    final List<String> command = getCommand();
    if (command == null || command.isEmpty())
    {
      return 0;
    }

    final Integer exitCode = super.call();

    final String processOutput = getProcessOutput();
    if (!isBlank(processOutput))
    {
      LOGGER.log(Level.INFO, processOutput);
    }
    final String processError = getProcessError();
    if (exitCode != null && exitCode != 0)
    {
      throw new IOException(String.format("Process returned exit code %d%n%s",
                                          exitCode,
                                          processError));
    }
    if (!isBlank(processError))
    {
      LOGGER.log(Level.WARNING, processError);
    }

    LOGGER.log(Level.INFO, "Generated diagram, " + outputFile);

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

  private void createDiagramCommand(final Path dotFile,
                                    final Path outputFile,
                                    final GraphOptions graphOptions,
                                    final GraphOutputFormat graphOutputFormat)
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");

    command.addAll(graphOptions.getGraphVizOpts());
    command.add("-T");
    command.add(graphOutputFormat.getFormat());
    command.add("-o");
    command.add(outputFile.toString());
    command.add(dotFile.toString());

    setCommandLine(command);
  }

}
