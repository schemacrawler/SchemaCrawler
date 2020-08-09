package schemacrawler.tools.integration.diagram;


import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.plain;
import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.png;
import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.ps;
import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.svg;
import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.xdot;
import static sf.util.Utility.isClassAvailable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;

import sf.util.string.FileContents;
import sf.util.ProcessExecutor;
import schemacrawler.SchemaCrawlerLogger;
import sf.util.string.StringFormat;

public final class GraphvizUtility
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(GraphvizUtility.class.getName());

  public static boolean isGraphvizAvailable()
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");
    command.add("-V");

    LOGGER.log(Level.FINE,
               new StringFormat("Checking if Graphviz is available:%n%s",
                                command.toString()));

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(command);

    Integer exitCode;
    try
    {
      exitCode = processExecutor.call();
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Graphviz stdout:%n%s",
                                  new FileContents(processExecutor.getProcessOutput())));
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Graphviz stderr:%n%s",
                                  new FileContents(processExecutor.getProcessError())));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not execute Graphviz command", e);
      LOGGER.log(Level.WARNING,
                 new StringFormat("Graphviz stderr:%n%s",
                                  new FileContents(processExecutor.getProcessError())));

      exitCode = Integer.MIN_VALUE;
    }
    final boolean successful = exitCode != null && exitCode == 0;
    LOGGER.log(Level.CONFIG,
               new StringFormat("Is Graphviz available? %s", successful));

    return successful;
  }

  public static boolean isGraphvizJavaAvailable(final DiagramOutputFormat diagramOutputFormat)
  {
    final String className = "guru.nidi.graphviz.engine.Graphviz";
    final boolean hasClass = isClassAvailable(className);
    final boolean supportsFormat = EnumSet
      .of(svg, png, ps, xdot, plain)
      .contains(diagramOutputFormat);

    LOGGER.log(Level.CONFIG,
               new StringFormat("Checking if diagram can be generated - "
                                + " can load <%s> = <%b>, "
                                + " can generate format <%s> = <%b>",
                                className,
                                hasClass,
                                diagramOutputFormat.getDescription(),
                                supportsFormat));

    return hasClass && supportsFormat;
  }

  private GraphvizUtility()
  {
    // Prevent instantiation
  }

}
