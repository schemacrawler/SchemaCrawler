/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.graph;


import static schemacrawler.tools.integration.graph.GraphvizJavaExecutorUtility.canMap;
import static schemacrawler.tools.integration.graph.GraphvizJavaExecutorUtility.generateGraph;

import java.nio.file.Path;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

final class GraphJavaExecutor
  extends AbstractGraphProcessExecutor
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(GraphJavaExecutor.class.getName());

  GraphJavaExecutor(final Path dotFile,
                    final Path outputFile,
                    final GraphOutputFormat graphOutputFormat)
    throws SchemaCrawlerException
  {
    super(dotFile, outputFile, graphOutputFormat);
  }

  @Override
  public Boolean call()
  {
    try
    {
      generateGraph(dotFile, outputFile, graphOutputFormat);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.INFO,
                 String.format("Could not generate diagram from:\n%s", dotFile),
                 e);
      return false;
    }
    return true;
  }

  @Override
  public boolean canGenerate()
  {

    final String className = "guru.nidi.graphviz.engine.Graphviz";

    boolean hasClass;
    boolean supportsFormat;
    try
    {
      Class.forName(className);
      hasClass = true;
      supportsFormat = hasClass && canMap(graphOutputFormat);
    }
    catch (final Exception e)
    {
      LOGGER
        .log(Level.INFO, new StringFormat("Could not load <%s>", className), e);
      hasClass = false;
      supportsFormat = false;
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Checking if diagram can be generated - "
                                + " can load <%s> = <%b>, "
                                + " can generate format <%s> = <%b>",
                                className,
                                hasClass,
                                graphOutputFormat.getDescription(),
                                supportsFormat));

    return hasClass && supportsFormat;

  }

}
