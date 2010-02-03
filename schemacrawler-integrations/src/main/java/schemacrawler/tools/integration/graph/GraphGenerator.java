package schemacrawler.tools.integration.graph;


import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

final class GraphGenerator
{

  private static final Logger LOGGER = Logger.getLogger(GraphGenerator.class
    .getName());

  private static void executeGraphGeneratorProcess(final String... args)
    throws IOException
  {
    final List<String> command = new ArrayList<String>(Arrays.asList(args));
    final String graphGenerator = System
      .getProperty("schemacrawler.graph_generator", "dot");
    command.add(0, graphGenerator);
    LOGGER.log(Level.INFO, "Executing: " + command);
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    final Process process = pb.start();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(process
      .getInputStream()));

    final StringBuilder buffer = new StringBuilder();
    String line;
    try
    {
      while ((line = reader.readLine()) != null)
      {
        buffer.append(line);
      }
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch (final EOFException e)
      {
        LOGGER.log(Level.WARNING, "Could not read diagram generator output", e);
      }
    }

    int exitCode = 0;
    try
    {
      exitCode = process.waitFor();
    }
    catch (final InterruptedException e)
    {
      //
    }

    process.getInputStream().close();
    process.getOutputStream().close();
    process.getErrorStream().close();

    if (exitCode != 0)
    {
      throw new IOException(buffer.toString());
    }
    else if (buffer.length() > 0)
    {
      LOGGER.log(Level.INFO, buffer.toString());
    }
  }

  private final File dotFile;
  private String graphOutputFormat;
  private File diagramFile;

  GraphGenerator(final File dotFile)
    throws IOException
  {
    if (dotFile == null || !dotFile.exists() || !dotFile.canRead())
    {
      throw new IOException("Cannot read the input DOT file, " + dotFile);
    }
    this.dotFile = dotFile;
    graphOutputFormat = "png";

    executeGraphGeneratorProcess("-V");
  }

  void generateDiagram()
    throws IOException
  {
    final File diagramFile = getDiagramFile();
    executeGraphGeneratorProcess("-q",
                                 "-T" + graphOutputFormat,
                                 "-o",
                                 diagramFile.getAbsolutePath(),
                                 dotFile.getAbsolutePath());
  }

  File getDiagramFile()
  {
    return diagramFile;
  }

  final File getDotFile()
  {
    return dotFile;
  }

  final String getGraphOutputFormat()
  {
    return graphOutputFormat;
  }

  final void setDiagramFile(final File diagramFile)
  {
    if (diagramFile == null)
    {
      this.diagramFile = new File(".", "schemacrawler." + UUID.randomUUID()
                                       + "." + graphOutputFormat);
    }
    else
    {
      this.diagramFile = diagramFile;
    }
  }

  void setGraphOutputFormat(final String outputFormat)
  {
    graphOutputFormat = outputFormat;
    final List<String> outputFormats = Arrays.asList("canon",
                                                     "cmap",
                                                     "cmapx",
                                                     "cmapx_np",
                                                     "dot",
                                                     "eps",
                                                     "fig",
                                                     "gd",
                                                     "gd2",
                                                     "gif",
                                                     "gv",
                                                     "imap",
                                                     "imap_np",
                                                     "ismap",
                                                     "jpe",
                                                     "jpeg",
                                                     "jpg",
                                                     "pdf",
                                                     "plain",
                                                     "plain-ext",
                                                     "png",
                                                     "ps",
                                                     "ps2",
                                                     "svg",
                                                     "svgz",
                                                     "tk",
                                                     "vml",
                                                     "vmlz",
                                                     "vrml",
                                                     "wbmp",
                                                     "xdot");
    if (Utility.isBlank(graphOutputFormat)
        || !outputFormats.contains(graphOutputFormat))
    {
      graphOutputFormat = "png";
    }
  }

}
