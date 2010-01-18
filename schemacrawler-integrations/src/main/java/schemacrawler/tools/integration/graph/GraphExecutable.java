/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import sf.util.Utility;

/**
 * Main executor for the graphing integration.
 *
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends BaseExecutable
{

  public GraphExecutable()
  {
    super("graph");
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.integration.SchemaRenderer#render(schemacrawler.schema.Database, java.sql.Connection,
   *      java.lang.String, java.io.Writer)
   */
  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    try
    {
      final File dotFile = File.createTempFile("schemacrawler.", ".dot");
      final DotWriter dotWriter = new DotWriter(dotFile);
      if (database != null)
      {
        dotWriter.open();
        dotWriter.print(database.getSchemaCrawlerInfo(), database
          .getDatabaseInfo(), database.getJdbcDriverInfo());
        for (final Schema schema : database.getSchemas())
        {
          for (final Table table : schema.getTables())
          {
            dotWriter.print(table);
          }
        }
        dotWriter.close();
      }

      final String graphOutputFormat = getGraphOutputFormat();
      final File outputFile = getOutputFile(graphOutputFormat);
      final GraphGenerator dot = new GraphGenerator();
      dot.generateDiagram(dotFile, graphOutputFormat, outputFile);
    }
    catch (final IOException e)
    {
      throw new ExecutionException("Could not write dot file", e);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not write dot file", e);
    }
  }

  private String getGraphOutputFormat()
  {
    String graphOutputFormat = outputOptions.getOutputFormatValue();
    final List<String> outputFormats = Arrays.asList(
      "canon",
      "cmap",
      "cmapx",
      "cmapx_np",
      "dot",
      "eps",
      "fig",
      "gd",
      "gd2",
      "gif",
      "gv",
      "imap",
      "imap_np",
      "ismap",
      "jpe",
      "jpeg",
      "jpg",
      "pdf",
      "plain",
      "plain-ext",
      "png",
      "ps",
      "ps2",
      "svg",
      "svgz",
      "tk",
      "vml",
      "vmlz",
      "vrml",
      "wbmp",
      "xdot"
    );
    if (Utility.isBlank(graphOutputFormat)
      || !outputFormats.contains(graphOutputFormat))
    {
      graphOutputFormat = "png";
    }
    return graphOutputFormat;
  }

  private File getOutputFile(final String graphOutputFormat)
  {
    File outputFile = outputOptions.getOutputFile();
    if (outputFile == null)
    {
      outputFile = new File(".", "schemacrawler." + UUID.randomUUID() + "."
        + graphOutputFormat);
    }
    return outputFile;
  }

}
