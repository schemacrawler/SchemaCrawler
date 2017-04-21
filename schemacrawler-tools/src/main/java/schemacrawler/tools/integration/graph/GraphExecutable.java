/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.nio.file.Path;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.analysis.counts.CatalogWithCounts;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaDotFormatter;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import schemacrawler.utility.NamedObjectSort;

/**
 * Main executor for the graphing integration.
 */
public final class GraphExecutable
  extends BaseStagedExecutable
{

  private GraphOptions graphOptions;

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
    loadGraphOptions();

    // Determine what decorators to apply to the database
    Catalog catalog = db;
    if (graphOptions.isShowWeakAssociations())
    {
      catalog = new CatalogWithAssociations(catalog);
    }
    if (graphOptions.isShowRowCounts()
        || schemaCrawlerOptions.isHideEmptyTables())
    {
      catalog = new CatalogWithCounts(catalog,
                                      connection,
                                      schemaCrawlerOptions);
    }

    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    // Set the format, in case we are using the default
    outputOptions.setOutputFormatValue(graphOutputFormat.getFormat());

    // Create dot file
    final Path dotFile = createTempFilePath("schemacrawler.", "dot");
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
    traverser.setTablesComparator(NamedObjectSort
      .getNamedObjectSort(getGraphOptions().isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(NamedObjectSort
      .getNamedObjectSort(getGraphOptions().isAlphabeticalSortForRoutines()));

    traverser.traverse();

    if (graphOutputFormat != GraphOutputFormat.scdot)
    {
      // Create graph image
      final GraphOptions graphOptions = getGraphOptions();
      final GraphProcessExecutor graphProcessExecutor = new GraphProcessExecutor(dotFile,
                                                                                 outputOptions
                                                                                   .getOutputFile(),
                                                                                 graphOptions,
                                                                                 graphOutputFormat);
      graphProcessExecutor.call();
    }
  }

  public final GraphOptions getGraphOptions()
  {
    loadGraphOptions();
    return graphOptions;
  }

  public final void setGraphOptions(final GraphOptions graphOptions)
  {
    this.graphOptions = graphOptions;
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
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();

    formatter = new SchemaDotFormatter(schemaTextDetailType,
                                       graphOptions,
                                       outputOptions);

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
