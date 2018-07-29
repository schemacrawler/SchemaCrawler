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


import static sf.util.IOUtility.createTempFilePath;
import static sf.util.IOUtility.readResourceFully;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.analysis.counts.CatalogWithCounts;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaDotFormatter;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import schemacrawler.utility.NamedObjectSort;

/**
 * Main executor for the graphing integration.
 */
public final class GraphRenderer
  extends BaseSchemaCrawlerCommand
{

  private GraphOptions graphOptions;

  public GraphRenderer(final String command)
  {
    super(command);
  }

  @Override
  public void checkAvailibility()
    throws Exception
  {
    super.checkAvailibility();
    // Check if graph executor is available
    final Path dotFile = createTempFilePath("schemacrawler.", "dot");
    Files.write(dotFile, "TEMP".getBytes());
    getGraphExecutor(dotFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    // Determine what decorators to apply to the database
    Catalog aCatalog = catalog;
    if (graphOptions.isShowWeakAssociations())
    {
      aCatalog = new CatalogWithAssociations(aCatalog);
    }
    if (graphOptions.isShowRowCounts()
        || schemaCrawlerOptions.isHideEmptyTables())
    {
      aCatalog = new CatalogWithCounts(aCatalog,
                                       connection,
                                       schemaCrawlerOptions);
    }

    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    // Set the format, in case we are using the default
    outputOptions = new OutputOptionsBuilder(outputOptions)
      .withOutputFormat(graphOutputFormat)
      .withOutputFormatValue(graphOutputFormat.getFormat()).toOptions();

    // Create dot file
    final Path dotFile = createTempFilePath("schemacrawler.", "dot");
    final OutputOptions dotFileOutputOptions;
    if (graphOutputFormat == GraphOutputFormat.scdot)
    {
      dotFileOutputOptions = outputOptions;
    }
    else
    {
      dotFileOutputOptions = OutputOptionsBuilder
        .newOutputOptions(GraphOutputFormat.dot, dotFile);
    }

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFileOutputOptions);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(aCatalog);
    traverser.setHandler(formatter);
    traverser.setTablesComparator(NamedObjectSort
      .getNamedObjectSort(graphOptions.isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(NamedObjectSort
      .getNamedObjectSort(graphOptions.isAlphabeticalSortForRoutines()));

    traverser.traverse();

    final GraphExecutor graphExecutor = getGraphExecutor(dotFile);
    graphExecutor.call();
  }

  @Override
  public void initialize()
    throws Exception
  {
    super.initialize();
    loadGraphOptions();
  }

  public final void setGraphOptions(final GraphOptions graphOptions)
  {
    this.graphOptions = graphOptions;
  }

  private GraphExecutor getGraphExecutor(final Path dotFile)
    throws SchemaCrawlerException, IOException
  {
    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    // Set the format, in case we are using the default
    outputOptions = new OutputOptionsBuilder(outputOptions)
      .withOutputFormat(graphOutputFormat)
      .withOutputFormatValue(graphOutputFormat.getFormat()).toOptions();

    final Path outputFile = outputOptions.getOutputFile();

    GraphExecutor graphExecutor;
    if (graphOutputFormat != GraphOutputFormat.scdot)
    {
      final List<String> graphvizOpts = graphOptions.getGraphvizOpts();
      boolean graphExecutorAvailable = false;

      // Try 1: Use Graphviz
      graphExecutor = new GraphProcessExecutor(dotFile,
                                               outputFile,
                                               graphOutputFormat,
                                               graphvizOpts);
      graphExecutorAvailable = graphExecutor.canGenerate();

      // Try 2: Use Java library for Graphviz
      if (!graphExecutorAvailable)
      {
        graphExecutor = new GraphJavaExecutor(dotFile,
                                              outputFile,
                                              graphOutputFormat);
        graphExecutorAvailable = graphExecutor.canGenerate();
      }

      if (!graphExecutorAvailable)
      {
        final String message = readResourceFully("/dot.error.txt");
        throw new SchemaCrawlerCommandLineException(message);
      }

    }
    else
    {
      graphExecutor = new GraphNoOpExecutor(graphOutputFormat);
    }

    return graphExecutor;
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
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();

    final String identifierQuoteString = identifiers.getIdentifierQuoteString();
    formatter = new SchemaDotFormatter(schemaTextDetailType,
                                       graphOptions,
                                       outputOptions,
                                       identifierQuoteString);

    return formatter;
  }

  private void loadGraphOptions()
  {
    if (graphOptions == null)
    {
      graphOptions = new GraphOptionsBuilder()
        .fromConfig(additionalConfiguration).toOptions();
    }
  }

}
