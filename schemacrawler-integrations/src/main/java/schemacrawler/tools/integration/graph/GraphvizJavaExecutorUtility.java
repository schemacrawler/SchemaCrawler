package schemacrawler.tools.integration.graph;


import static java.util.Objects.requireNonNull;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizEngine;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.IOUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;
import sf.util.Utility;

public final class GraphvizJavaExecutorUtility
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(GraphvizJavaExecutorUtility.class.getName());

  public static boolean canMap(final GraphOutputFormat graphOutputFormat)
  {
    return map(graphOutputFormat) != null;
  }

  /**
   * Need a static method to account for imports of pure Java Graphviz
   * library.
   *
   * @param dotFile
   *        Path to DOT file
   * @param outputFile
   *        Path to output file
   * @param graphOutputFormat
   *        Output format
   * @throws SchemaCrawlerException
   *         Thrown on an exception
   */
  public static void generateGraph(final Path dotFile,
                                   final Path outputFile,
                                   final GraphOutputFormat graphOutputFormat)
    throws SchemaCrawlerException
  {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No graph output file provided");
    requireNonNull(graphOutputFormat, "No graph output format provided");

    try
    {
      // Strip all line breaks, in order to use the pure Java engine for
      // Graphviz
      String dotSource = IOUtility.readFully(new FileReader(dotFile.toFile()));
      dotSource = dotSource.replaceAll("\\R", " ");

      final List<GraphvizEngine> engines = loadGraphvizEngines();
      Graphviz.useEngine(engines);

      final Format format = map(graphOutputFormat);
      Graphviz.fromString(dotSource).render(format).toFile(outputFile.toFile());
    }
    catch (final Throwable e)
    {
      throw new SchemaCrawlerException("Cannot generate graph from " + dotFile,
                                       e);
    }
  }

  public static boolean isGraphvizJavaAvailable(final GraphOutputFormat graphOutputFormat)
  {
    final String className = "guru.nidi.graphviz.engine.Graphviz";
    final boolean hasClass = Utility.isClassAvailable(className);
    final boolean supportsFormat = canMap(graphOutputFormat);

    LOGGER.log(Level.INFO,
               new StringFormat("Checking if diagram can be generated - "
                                + " can load <%s> = <%b>, "
                                + " can generate format <%s> = <%b>",
                                className,
                                hasClass,
                                graphOutputFormat.getDescription(),
                                supportsFormat));

    return hasClass && supportsFormat;
  }

  public static void main(final String[] args)
    throws Exception
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("<format> <DOT file> <output file>");
    }

    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .valueOf(args[0]);
    final Path dotFile = Paths.get(args[1]).normalize().toAbsolutePath();
    final Path outputFile = Paths.get(args[2]).normalize().toAbsolutePath();

    generateGraph(dotFile, outputFile, graphOutputFormat);
  }

  private static List<GraphvizEngine> loadGraphvizEngines()
  {
    final List<GraphvizEngine> engines = new ArrayList<>();
    try
    {
      final GraphvizEngine engine = new GraphvizV8Engine();
      engines.add(engine);
    }
    catch (final NoClassDefFoundError e)
    {
      LOGGER.log(Level.INFO, "Cannot load GraphvizV8Engine");
    }

    try
    {
      final GraphvizEngine engine = new GraphvizJdkEngine();
      engines.add(engine);
    }
    catch (final NoClassDefFoundError e)
    {
      LOGGER.log(Level.INFO, "Cannot load GraphvizJdkEngine");
    }

    return engines;
  }

  private static Format map(final GraphOutputFormat graphOutputFormat)
  {
    if (graphOutputFormat == null)
    {
      return null;
    }
    final Format format;
    switch (graphOutputFormat)
    {
      case svg:
        format = Format.SVG;
        break;
      case png:
        format = Format.PNG;
        break;
      case ps:
        format = Format.PS;
        break;
      case xdot:
        format = Format.XDOT;
        break;
      case plain:
        format = Format.PLAIN;
        break;
      default:
        format = null;
        break;
    }
    return format;
  }

  private GraphvizJavaExecutorUtility()
  {
    // Prevent instantiation
  }

}
