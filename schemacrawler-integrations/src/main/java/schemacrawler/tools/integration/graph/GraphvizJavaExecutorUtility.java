package schemacrawler.tools.integration.graph;


import static java.util.Objects.requireNonNull;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.IOUtility;

public final class GraphvizJavaExecutorUtility
{

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

      Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
      final Format format = map(graphOutputFormat);
      Graphviz.fromString(dotSource).render(format).toFile(outputFile.toFile());
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Cannot generate graph from " + dotFile,
                                       e);
    }
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

  private static Format map(final GraphOutputFormat graphOutputFormat)
  {
    if (graphOutputFormat == null)
    {
      return null;
    }
    switch (graphOutputFormat)
    {
      case svg:
        return Format.SVG;
      // break;
      case png:
        return Format.PNG;
      // break;
      case ps:
        return Format.PS;
      // break;
      case xdot:
        return Format.XDOT;
      // break;
      case plain:
        return Format.PLAIN;
      // break;
      default:
        break;
    }
    return null;
  }

  private GraphvizJavaExecutorUtility()
  {
    // Prevent instantiation
  }

}
