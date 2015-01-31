/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaDotFormatter;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;

/**
 * Main executor for the graphing integration.
 *
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends BaseStagedExecutable
{

  static final String COMMAND = "graph";

  private GraphOptions graphOptions;

  private static final Logger LOGGER = Logger.getLogger(GraphExecutable.class
    .getName());

  public GraphExecutable()
  {
    super(COMMAND);
  }

  public GraphExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeOn(final Catalog db, final Connection connection)
    throws Exception
  {
    // Determine what decorators to apply to the database
    InfoLevel infoLevel;
    try
    {
      infoLevel = InfoLevel.valueOf(schemaCrawlerOptions.getSchemaInfoLevel()
        .getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }
    final Catalog catalog;
    if (infoLevel == InfoLevel.maximum)
    {
      catalog = new CatalogWithAssociations(db);
    }
    else
    {
      catalog = db;
    }

    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    // Set the format, in case we are using the default
    outputOptions.setOutputFormatValue(graphOutputFormat.getFormat());

    // Create dot file
    final Path dotFile = Files.createTempFile("schemacrawler.", ".dot")
      .normalize().toAbsolutePath();
    final OutputOptions dotFileOutputOptions;
    if (graphOutputFormat == GraphOutputFormat.scdot)
    {
      dotFileOutputOptions = outputOptions;
    }
    else
    {
      dotFileOutputOptions = new OutputOptions(GraphOutputFormat.dot, dotFile);
    }
    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFileOutputOptions);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(catalog);
    traverser.setHandler(formatter);
    traverser.traverse();

    // Create graph image
    final GraphOptions graphOptions = getGraphOptions();
    try
    {
      generateDiagram(graphOptions, graphOutputFormat, dotFile);
    }
    catch (final Exception e)
    {
      System.out.println(readResourceFully("/dot.error.txt"));
      throw e;
    }
  }

  public final GraphOptions getGraphOptions()
  {
    final GraphOptions graphOptions;
    if (this.graphOptions == null)
    {
      graphOptions = new GraphOptions(additionalConfiguration);
    }
    else
    {
      graphOptions = this.graphOptions;
    }
    return graphOptions;
  }

  public final void setGraphOptions(final GraphOptions graphOptions)
  {
    this.graphOptions = graphOptions;
  }

  private List<String> createDiagramCommand(final GraphOptions graphOptions,
                                            final GraphOutputFormat graphOutputFormat,
                                            final Path dotFile)
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");

    command.addAll(graphOptions.getGraphVizOpts());
    command.add("-T");
    command.add(graphOutputFormat.getFormat());
    command.add("-o");
    command.add(outputOptions.getOutputFile().toString());
    command.add(dotFile.toString());

    final Iterator<String> iterator = command.iterator();
    while (iterator.hasNext())
    {
      if (isBlank(iterator.next()))
      {
        iterator.remove();
      }
    }

    return command;
  }

  private void generateDiagram(final GraphOptions graphOptions,
                               final GraphOutputFormat graphOutputFormat,
                               final Path dotFile)
    throws IOException
  {

    if (graphOutputFormat == GraphOutputFormat.scdot)
    {
      return;
    }

    final List<String> command = createDiagramCommand(graphOptions,
                                                      graphOutputFormat,
                                                      dotFile);

    final ProcessExecutor processExecutor = new ProcessExecutor(command);
    final int exitCode = processExecutor.execute();

    final String processOutput = processExecutor.getProcessOutput();
    if (!isBlank(processOutput))
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
    if (!isBlank(processError))
    {
      LOGGER.log(Level.WARNING, processError);
    }
  }

  private SchemaTextDetailType getSchemaTextDetailType()
  {
    SchemaTextDetailType schemaTextDetailType;
    try
    {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      schemaTextDetailType = null;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    final SchemaTraversalHandler formatter;
    final GraphOptions graphOptions = getGraphOptions();

    SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();
    if (schemaTextDetailType == null)
    {
      schemaTextDetailType = graphOptions.getSchemaTextDetailType();
    }

    formatter = new SchemaDotFormatter(schemaTextDetailType,
                                       graphOptions,
                                       outputOptions);

    return formatter;
  }

}
