/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.io.File;
import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.DatabaseWithAssociations;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaDotFormatter;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import sf.util.Utility;

/**
 * Main executor for the graphing integration.
 * 
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends BaseExecutable
{

  static final String COMMAND = "graph";

  private GraphOptions graphOptions;

  public GraphExecutable()
  {
    super(COMMAND);
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    // Determine what decorators to apply to the database
    InfoLevel infoLevel;
    try
    {
      infoLevel = InfoLevel.valueOf(getSchemaCrawlerOptions()
        .getSchemaInfoLevel().getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }
    final Database database;
    if (infoLevel == InfoLevel.maximum)
    {
      database = new DatabaseWithAssociations(db);
    }
    else
    {
      database = db;
    }

    // Create dot file
    final File dotFile = File.createTempFile("schemacrawler.", ".dot");

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFile);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setDatabase(database);
    traverser.setFormatter(formatter);
    traverser.traverse();

    // Create graph image
    final GraphOptions graphOptions = getGraphOptions();
    final GraphGenerator dot = new GraphGenerator(graphOptions.getGraphVizOpts(),
                                                  dotFile,
                                                  outputOptions
                                                    .getOutputFormatValue(),
                                                  outputOptions.getOutputFile());
    try
    {
      dot.generateDiagram();
    }
    catch (final Exception e)
    {
      System.out.println(Utility.readResourceFully("/dot.error.txt"));
      throw e;
    }
  }

  private SchemaTraversalHandler getSchemaTraversalHandler(final File dotFile)
    throws SchemaCrawlerException
  {
    final SchemaTraversalHandler formatter;
    final GraphOptions graphOptions = getGraphOptions();
    final SchemaTextDetailType schemaTextDetailType = graphOptions
      .getSchemaTextDetailType();

    formatter = new SchemaDotFormatter(schemaTextDetailType,
                                       graphOptions,
                                       new OutputOptions("dot", dotFile));

    return formatter;
  }

}
