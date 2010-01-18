package schemacrawler.tools.integration.graph;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class GraphGenerator
{

  private static final Logger LOGGER = Logger.getLogger(GraphGenerator.class
    .getName());

  private static String getGraphGenerator()
  {
    return System.getProperty("schemacrawler.graph_generator", "dot");
  }

  private static void run(final String... args)
    throws IOException
  {
    final List<String> command = new ArrayList<String>(Arrays.asList(args));
    command.add(0, getGraphGenerator());
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
        LOGGER.log(Level.WARNING, "Could not read diagram generator output"
          + e.getMessage());
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

    process.getInputStream()
      .close();
    process.getOutputStream()
      .close();
    process.getErrorStream()
      .close();

    if (exitCode != 0)
    {
      throw new IOException(buffer.toString());
    }
    else if (buffer.length() > 0)
    {
      LOGGER.log(Level.INFO, buffer.toString());
    }
  }

  GraphGenerator()
    throws IOException
  {
    run("-V");
  }

  static void generateDiagram(final File dotFile,
                              final String outputFormat,
                              final File diagramFile)
    throws IOException
  {
    if (dotFile == null || !dotFile.exists() || !dotFile.canRead())
    {
      throw new IOException("Cannot read the input DOT file, " + dotFile);
    }
    if (diagramFile == null)
    {
      throw new IOException("Cannot write diagram file");
    }

    run("-q", "-T" + outputFormat, "-o", diagramFile.getAbsolutePath(), dotFile
      .getAbsolutePath());
  }

}
