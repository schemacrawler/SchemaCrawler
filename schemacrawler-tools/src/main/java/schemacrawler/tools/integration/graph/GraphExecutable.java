/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.readResourceFully;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.analysis.counts.CatalogWithCounts;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.InfoLevel;
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

  static final String COMMAND = "graph";

  private GraphOptions graphOptions;

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
      infoLevel = InfoLevel
        .valueOf(schemaCrawlerOptions.getSchemaInfoLevel().getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }
    final Catalog catalog;
    if (infoLevel == InfoLevel.maximum)
    {
      final Catalog catalogAssociations = new CatalogWithAssociations(db);
      catalog = new CatalogWithCounts(catalogAssociations,
                                      connection,
                                      schemaCrawlerOptions);
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
    traverser.setTablesComparator(NamedObjectSort
      .getNamedObjectSort(getGraphOptions().isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(NamedObjectSort
      .getNamedObjectSort(getGraphOptions().isAlphabeticalSortForRoutines()));

    traverser.traverse();

    // Create graph image
    final GraphOptions graphOptions = getGraphOptions();
    try
    {
      final GraphProcessExecutor graphProcessExecutor = new GraphProcessExecutor(dotFile,
                                                                                 outputOptions
                                                                                   .getOutputFile(),
                                                                                 graphOptions,
                                                                                 graphOutputFormat);
      graphProcessExecutor.call();
    }
    catch (final Exception e)
    {
      System.err.println(readResourceFully("/dot.error.txt"));
      throw e;
    }
  }

  public final GraphOptions getGraphOptions()
  {
    final GraphOptions graphOptions;
    if (this.graphOptions == null)
    {
      graphOptions = new GraphOptionsBuilder()
        .fromConfig(additionalConfiguration).toOptions();
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
