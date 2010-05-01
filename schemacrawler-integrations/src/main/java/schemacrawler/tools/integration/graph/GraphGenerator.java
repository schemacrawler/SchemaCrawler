package schemacrawler.tools.integration.graph;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

final class GraphGenerator
{

  private static final Logger LOGGER = Logger.getLogger(GraphGenerator.class
    .getName());

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
  }

  void generateDiagram()
    throws IOException
  {
    final File diagramFile = getDiagramFile();

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

    final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    try
    {
      final Process process = new ProcessBuilder(command).start();

      final StreamReaderTask inReaderTask = new StreamReaderTask(process
        .getInputStream());
      threadPool.execute(inReaderTask);
      final StreamReaderTask errReaderTask = new StreamReaderTask(process
        .getErrorStream());
      threadPool.execute(errReaderTask);

      final int exitCode = process.waitFor();

      final String processOutput = inReaderTask.get();
      final String processError = errReaderTask.get();

      if (exitCode != 0)
      {
        if (!Utility.isBlank(processError))
        {
          throw new IOException(processError);
        }
        else
        {
          throw new IOException(processOutput);
        }
      }
      else
      {
        if (!Utility.isBlank(processOutput))
        {
          LOGGER.log(Level.INFO, processOutput);
        }
        if (!Utility.isBlank(processError))
        {
          LOGGER.log(Level.WARNING, processError);
        }
      }
    }
    catch (InterruptedException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    catch (ExecutionException e)
    {
      throw new IOException(e.getMessage(), e);
    }
    finally
    {
      threadPool.shutdown();
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
