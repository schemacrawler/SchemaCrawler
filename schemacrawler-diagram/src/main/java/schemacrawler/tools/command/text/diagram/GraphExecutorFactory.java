/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static schemacrawler.tools.command.text.diagram.GraphvizUtility.isGraphvizAvailable;
import static schemacrawler.tools.command.text.diagram.GraphvizUtility.isGraphvizJavaAvailable;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.scdot;
import static us.fatehi.utility.IOUtility.readResourceFully;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

public class GraphExecutorFactory {

  private static final Logger LOGGER = Logger.getLogger(GraphExecutorFactory.class.getName());

  public void canGenerate(final DiagramOutputFormat diagramOutputFormat) {
    if (diagramOutputFormat == null) {
      throw new ConfigurationException("No diagram output format specified");
    } else if (diagramOutputFormat == scdot) {
      return;
    } else if (isGraphvizAvailable()) {
      return;
    } else if (isGraphvizJavaAvailable(diagramOutputFormat)) {
      return;
    } else {
      throw new ExecutionRuntimeException(
          "Cannot generate diagram in <%s> output format".formatted(diagramOutputFormat));
    }
  }

  public GraphExecutor getGraphExecutor(
      final Path dotFile,
      final DiagramOutputFormat diagramOutputFormat,
      final Path outputFile,
      final DiagramOptions commandOptions) {

    GraphExecutor graphExecutor;
    if (diagramOutputFormat != scdot) {
      final List<String> graphvizOpts = commandOptions.getGraphvizOpts();
      boolean graphExecutorAvailable = false;

      // Try 1: Use Graphviz
      graphExecutor =
          new GraphvizProcessExecutor(dotFile, outputFile, diagramOutputFormat, graphvizOpts);
      graphExecutorAvailable = graphExecutor.canGenerate();

      // Try 2: Use Java library for Graphviz
      if (!graphExecutorAvailable) {
        graphExecutor = new GraphvizJavaExecutor(dotFile, outputFile, diagramOutputFormat);
        graphExecutorAvailable = graphExecutor.canGenerate();
      }

      if (!graphExecutorAvailable) {
        final String message = readResourceFully("/dot.error.txt");
        throw new ExecutionRuntimeException(message);
      }

    } else {
      graphExecutor = new GraphNoOpExecutor(diagramOutputFormat);
    }

    LOGGER.log(Level.INFO, "Using " + graphExecutor);

    return graphExecutor;
  }
}
