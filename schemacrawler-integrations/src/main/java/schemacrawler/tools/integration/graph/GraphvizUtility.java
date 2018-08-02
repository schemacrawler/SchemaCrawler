package schemacrawler.tools.integration.graph;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.utility.ProcessExecutor;
import sf.util.FileContents;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public final class GraphvizUtility
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(GraphvizUtility.class.getName());

  public static boolean isGraphvizAvailable()
  {

    final List<String> command = new ArrayList<>();
    command.add("dot");
    command.add("-V");

    LOGGER.log(Level.INFO,
               new StringFormat("Checking if Graphviz is available:\n%s",
                                command.toString()));

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(command);

    Integer exitCode;
    try
    {
      exitCode = processExecutor.call();
      LOGGER.log(Level.INFO,
                 new FileContents(processExecutor.getProcessOutput()));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not execute Graphviz command", e);
      LOGGER.log(Level.WARNING,
                 new FileContents(processExecutor.getProcessError()));

      exitCode = Integer.MIN_VALUE;
    }
    final boolean successful = exitCode != null && exitCode == 0;

    return successful;
  }

  private GraphvizUtility()
  {
    // Prevent instantiation
  }

}
