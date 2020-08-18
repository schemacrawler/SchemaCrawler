package schemacrawler.tools.integration.diagram;


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
import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import us.fatehi.utility.IOUtility;

public final class GraphvizJavaExecutorUtility
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(GraphvizJavaExecutorUtility.class.getName());

  /**
   * Need a static method to account for imports of pure Java Graphviz library.
   *
   * @param dotFile
   *   Path to DOT file
   * @param outputFile
   *   Path to output file
   * @param diagramOutputFormat
   *   Output format
   * @throws SchemaCrawlerException
   *   Thrown on an exception
   */
  public static void generateGraph(final Path dotFile,
                                   final Path outputFile,
                                   final DiagramOutputFormat diagramOutputFormat)
    throws SchemaCrawlerException
  {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No diagram output file provided");
    requireNonNull(diagramOutputFormat, "No diagram output format provided");

    try
    {
      // Strip all line breaks, in order to use the pure Java engine for
      // Graphviz
      String dotSource = IOUtility.readFully(new FileReader(dotFile.toFile()));
      dotSource = dotSource.replaceAll("\\R", " ");

      final List<GraphvizEngine> engines = loadGraphvizEngines();
      Graphviz.useEngine(engines);

      final Format format = map(diagramOutputFormat);
      if (format == null)
      {
        throw new IllegalArgumentException("Unsupported output format, "
                                           + diagramOutputFormat);
      }

      Graphviz
        .fromString(dotSource)
        .render(format)
        .toFile(outputFile.toFile());
    }
    catch (final Throwable e)
    {
      throw new SchemaCrawlerException("Cannot generate diagram from "
                                       + dotFile, e);
    }
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

  public static void main(final String[] args)
    throws Exception
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("<format> <DOT file> <output file>");
    }

    final DiagramOutputFormat diagramOutputFormat =
      DiagramOutputFormat.valueOf(args[0]);
    final Path dotFile = Paths
      .get(args[1])
      .normalize()
      .toAbsolutePath();
    final Path outputFile = Paths
      .get(args[2])
      .normalize()
      .toAbsolutePath();

    generateGraph(dotFile, outputFile, diagramOutputFormat);
  }

  private static Format map(final DiagramOutputFormat diagramOutputFormat)
  {
    if (diagramOutputFormat == null)
    {
      return null;
    }
    final Format format;
    switch (diagramOutputFormat)
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
