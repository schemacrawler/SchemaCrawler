/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import java.io.File;
import java.io.IOException;
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

    // Create dot file
    final File dotFile = File.createTempFile("schemacrawler.", ".dot");

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFile);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(catalog);
    traverser.setHandler(formatter);
    traverser.traverse();

    // Create graph image
    final GraphOptions graphOptions = getGraphOptions();
    final GraphOutputOptions graphOutputOptions = new GraphOutputOptions(outputOptions);
    try
    {
      generateDiagram(graphOptions, graphOutputOptions, dotFile);
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
                                            final GraphOutputOptions graphOutputOptions,
                                            final File dotFile)
  {
    final List<String> command = new ArrayList<>();
    command.add("dot");

    command.addAll(graphOptions.getGraphVizOpts());
    command.add("-T");
    command.add(graphOutputOptions.getOutputFormat().getFormat());
    command.add("-o");
    command.add(graphOutputOptions.getDiagramFile().getAbsolutePath());
    command.add(dotFile.getAbsolutePath());

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
                               final GraphOutputOptions graphOutputOptions,
                               final File dotFile)
    throws IOException
  {

    if (graphOutputOptions.getOutputFormat() == GraphOutputFormat.scdot)
    {
      copy(dotFile.toPath(),
           graphOutputOptions.getDiagramFile().toPath(),
           REPLACE_EXISTING);
      return;
    }

    final List<String> command = createDiagramCommand(graphOptions,
                                                      graphOutputOptions,
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

  private SchemaTraversalHandler getSchemaTraversalHandler(final File dotFile)
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
                                       new OutputOptions("dot", dotFile));

    return formatter;
  }

}
