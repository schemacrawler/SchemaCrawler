/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
