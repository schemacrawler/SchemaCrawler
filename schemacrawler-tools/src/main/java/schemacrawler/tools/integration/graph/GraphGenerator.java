/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.integration.graph;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

final class GraphGenerator
{

  private static final String SC_GRAPHVIZ_OPTS = "SC_GRAPHVIZ_OPTS";

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
    if (dotFile == null || dotFile.isDirectory() || !dotFile.exists()
        || !dotFile.canRead())
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

    final List<String> command = new ArrayList<String>();
    command.add("dot");

    final List<String> scGraphVizOpts = getGraphVizOpts();
    command.addAll(scGraphVizOpts);

    command.add("-T");
    command.add(graphOutputFormat);
    command.add("-o");
    command.add(diagramFile.getAbsolutePath());
    command.add(dotFile.getAbsolutePath());

    final ProcessExecutor processExecutor = new ProcessExecutor(command);
    final int exitCode = processExecutor.execute();

    final String processOutput = processExecutor.getProcessOutput();
    if (!Utility.isBlank(processOutput))
    {
      LOGGER.log(Level.INFO, processOutput);
    }
    final String processError = processExecutor.getProcessError();
    if (exitCode != 0)
    {
      throw new IOException(String.format("Process returned exit code %d%n%s",
                                          exitCode,
                                          processError));
    }
    if (!Utility.isBlank(processError))
    {
      LOGGER.log(Level.WARNING, processError);
    }
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

  private List<String> getGraphVizOpts()
  {
    final String scGraphVizOptsEnv = System.getenv(SC_GRAPHVIZ_OPTS);
    final String scGraphVizOptsProp = System.getProperty(SC_GRAPHVIZ_OPTS);

    final StringBuilder scGraphVizOpts = new StringBuilder();
    if (!Utility.isBlank(scGraphVizOptsEnv))
    {
      scGraphVizOpts.append(scGraphVizOptsEnv).append(" ");
    }
    if (!Utility.isBlank(scGraphVizOptsProp))
    {
      scGraphVizOpts.append(scGraphVizOptsProp).append(" ");
    }

    if (scGraphVizOpts.length() > 0)
    {
      return Arrays.asList(scGraphVizOpts.toString().split("\\s+"));
    }
    else
    {
      return Collections.emptyList();
    }
  }

}
