/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram;

import static schemacrawler.tools.command.text.diagram.GraphvizUtility.isGraphvizJavaAvailable;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import us.fatehi.utility.string.StringFormat;

final class GraphvizJavaExecutor extends AbstractGraphProcessExecutor {

  private static final Logger LOGGER = Logger.getLogger(GraphvizJavaExecutor.class.getName());

  GraphvizJavaExecutor(
      final Path dotFile, final Path outputFile, final DiagramOutputFormat diagramOutputFormat) {
    super(dotFile, outputFile, diagramOutputFormat);
  }

  @Override
  public boolean canGenerate() {
    return isGraphvizJavaAvailable(diagramOutputFormat);
  }

  @Override
  public void run() {
    GraphvizJavaExecutorUtility.generateGraph(dotFile, outputFile, diagramOutputFormat);
    LOGGER.log(Level.INFO, new StringFormat("Generated diagram <%s>", outputFile));
  }
}
