package schemacrawler.tools.integration.graph;


import java.nio.file.Path;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import schemacrawler.schemacrawler.SchemaCrawlerException;

final class GraphExecutorUtility
{

  public static boolean canMap(final GraphOutputFormat graphOutputFormat)
  {
    return map(graphOutputFormat) != null;
  }

  /**
   * Need a static method to account for imports pf pure Java Graphviz
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
    try
    {
      Graphviz.fromFile(dotFile.toFile()).render(map(graphOutputFormat))
        .toFile(outputFile.toFile());
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Cannot generate graph from " + dotFile,
                                       e);
    }
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

  private GraphExecutorUtility()
  {
    // Prevent instantiation
  }

}
