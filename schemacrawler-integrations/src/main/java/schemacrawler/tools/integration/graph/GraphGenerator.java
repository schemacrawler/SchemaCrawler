package schemacrawler.tools.integration.graph;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

final class GraphGenerator
{

  private static final Logger LOGGER = Logger.getLogger(GraphGenerator.class
    .getName());

  private final File dotFile;
  private final String graphOutputFormat;
  private final File diagramFile;

  GraphGenerator(final File dotFile,
                 final String outputFormat,
                 final File diagramOutputFile)
    throws IOException
  {
    if (dotFile == null || !dotFile.exists() || !dotFile.canRead())
    {
      throw new IOException("Cannot read the input DOT file, " + dotFile);
    }
    this.dotFile = dotFile;
    graphOutputFormat = determineGraphOutputFormat(outputFormat);
    diagramFile = determineDiagramFile(diagramOutputFile);
  }

  void generateDiagram()
    throws IOException
  {

    final String graphGenerator = System
      .getProperty("schemacrawler.graph_generator", "dot");
    final String[] command = new String[] {
        graphGenerator,
        "-q",
        "-T" + graphOutputFormat,
        "-o",
        diagramFile.getAbsolutePath(),
        dotFile.getAbsolutePath()
    };
    LOGGER.log(Level.INFO, "Executing: " + Arrays.toString(command));

    try
    {
      final class StreamReader
        implements Callable<String>
      {

        private final InputStream in;

        StreamReader(final InputStream in)
        {
          this.in = in;
        }

        public String call()
          throws Exception
        {
          final Reader reader = new BufferedReader(new InputStreamReader(in));
          return Utility.readFully(reader);
        }
      }

      final ExecutorService threadPool = Executors.newFixedThreadPool(2);
      final Process process = new ProcessBuilder(command).start();

      final FutureTask<String> inReaderTask = new FutureTask<String>(new StreamReader(process
        .getInputStream()));
      threadPool.execute(inReaderTask);
      final FutureTask<String> errReaderTask = new FutureTask<String>(new StreamReader(process
        .getErrorStream()));
      threadPool.execute(errReaderTask);

      final int exitCode = process.waitFor();

      final String processOutput = inReaderTask.get();
      if (!Utility.isBlank(processOutput))
      {
        LOGGER.log(Level.INFO, processOutput);
      }
      final String processError = errReaderTask.get();
      if (!Utility.isBlank(processError))
      {
        LOGGER.log(Level.WARNING, processError);
      }

      threadPool.shutdown();

      if (exitCode != 0)
      {
        throw new IOException("Process returned exit code " + exitCode);
      }
    }
    catch (final SecurityException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (final ExecutionException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (final InterruptedException e)
    {
      throw new IOException(e.getMessage(), e);
    }
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

  private File determineDiagramFile(final File diagramOutputFile)
  {
    File diagramFile;
    if (diagramOutputFile == null)
    {
      diagramFile = new File(".", "schemacrawler." + UUID.randomUUID() + "."
                                  + graphOutputFormat);
    }
    else
    {
      diagramFile = diagramOutputFile;
    }
    return diagramFile;
  }

  private String determineGraphOutputFormat(final String outputFormat)
  {
    String graphOutputFormat = outputFormat;
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
    return graphOutputFormat;
  }

}
