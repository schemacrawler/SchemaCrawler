/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.diagram;


import static schemacrawler.tools.integration.diagram.GraphvizJavaExecutorUtility.generateGraph;
import static schemacrawler.tools.integration.diagram.GraphvizUtility.isGraphvizJavaAvailable;

import java.nio.file.Path;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

final class GraphJavaExecutor
  extends AbstractGraphProcessExecutor
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(GraphJavaExecutor.class.getName());

  GraphJavaExecutor(final Path dotFile,
                    final Path outputFile,
                    final DiagramOutputFormat diagramOutputFormat)
    throws SchemaCrawlerException
  {
    super(dotFile, outputFile, diagramOutputFormat);
  }

  @Override
  public Boolean call()
  {
    try
    {
      generateGraph(dotFile, outputFile, diagramOutputFormat);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.INFO,
                 String.format("Could not generate diagram from:%n%s", dotFile),
                 e);
      return false;
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Generated diagram <%s>", outputFile));

    return true;
  }

  @Override
  public boolean canGenerate()
  {
    return isGraphvizJavaAvailable(diagramOutputFormat);
  }

}
