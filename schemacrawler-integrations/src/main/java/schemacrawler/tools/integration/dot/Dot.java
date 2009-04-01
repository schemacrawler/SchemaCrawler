package schemacrawler.tools.integration.dot;


import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Dot
{

  private static final Logger LOGGER = Logger.getLogger(Dot.class.getName());

  Dot()
    throws IOException
  {
    dot("-V");
  }

  void generateDiagram(final File dotFile, final File diagramFile)
    throws IOException
  {
    if (dotFile == null || !dotFile.exists() || !dotFile.canRead())
    {
      throw new IOException("Cannot read the input DOT file, " + dotFile);
    }
    if (diagramFile == null)
    {
      throw new IOException("Cannot write diagram file, " + diagramFile);
    }

    dot("-q", "-Tpng", "-o", diagramFile.getAbsolutePath(), dotFile
      .getAbsolutePath());
  }

  private static void dot(final String... args)
    throws IOException
  {
    final List<String> dotCommand = new ArrayList<String>(Arrays.asList(args));
    dotCommand.add(0, "dot");
    LOGGER.log(Level.INFO, "Executing: " + dotCommand);
    final ProcessBuilder pb = new ProcessBuilder(dotCommand);
    pb.redirectErrorStream(true);
    final Process process = pb.start();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(process
      .getInputStream()));

    String line;
    try
    {
      try
      {
        while ((line = reader.readLine()) != null)
        {
          LOGGER.log(Level.INFO, line);
        }
      }
      catch (EOFException e)
      {
        // 
      }
      reader.close();
    }
    catch (IOException e)
    {
      LOGGER.log(Level.WARNING, "Could not read dot output" + e.getMessage());
    }

    try
    {
      process.waitFor();
    }
    catch (InterruptedException e)
    {
      //
    }

    process.getInputStream().close();
    process.getOutputStream().close();
    process.getErrorStream().close();
  }

}
