/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.integration.IntegrationsExecutable;
import schemacrawler.tools.main.HelpOptions;
import schemacrawler.tools.main.HelpOptions.CommandHelpType;
import schemacrawler.tools.schematext.SchemaTextFactory;
import schemacrawler.tools.util.HtmlFormattingHelper;
import sf.util.FileUtility;

/**
 * Main executor for the graphing integration.
 * 
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends IntegrationsExecutable
{

  private static final Logger LOGGER = Logger.getLogger(GraphExecutable.class
    .getName());

  private static String dotError()
  {
    return sf.util.Utility.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/dot.error.txt"));
  }

  public GraphExecutable()
  {
    super(GraphExecutable.class.getSimpleName());
  }

  @Override
  public void execute(final Connection connection)
    throws Exception
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }

    initialize();

    final OutputOptions outputOptions = toolOptions.getOutputOptions();
    final File outputFile = outputOptions.getOutputFile();

    try
    {
      final String outputFormat = outputOptions.getOutputFormatValue();
      if (outputFormat.equalsIgnoreCase("dot"))
      {
        writeDotFile(connection, FileUtility
          .changeFileExtension(outputFile, ".dot"));
      }
      else
      {
        final File dotFile = File.createTempFile("schemacrawler.", ".dot");
        dotFile.deleteOnExit();

        final GraphGenerator dot = new GraphGenerator();
        writeDotFile(connection, dotFile);
        dot.generateDiagram(dotFile, outputFormat, outputFile);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not write diagram", e);
      writeDotFile(connection, FileUtility.changeFileExtension(outputFile, ".dot"));
      System.out.println(dotError());
    }
  }

  @Override
  protected HelpOptions getHelpOptions()
  {
    final HelpOptions helpOptions = new HelpOptions("SchemaCrawler - Graphing");
    helpOptions.setCommandHelpType(CommandHelpType.without_operations);
    helpOptions.setResourceOutputOptions("/help/OutputOptions.dot.txt");

    return helpOptions;
  }

  private void writeDotFile(final Connection connection, final File dotFile)
  {
    try
    {
      final OutputOptions outputOptions = new OutputOptions();
      outputOptions.setOutputFormatValue(OutputFormat.dot.name());
      outputOptions.setOutputFileName(dotFile.getAbsolutePath());
      toolOptions.setOutputOptions(outputOptions);

      final CrawlHandler handler = SchemaTextFactory
        .createSchemaTextCrawlHandler(toolOptions);
      final SchemaCrawler crawler = new DatabaseSchemaCrawler(connection);
      crawler.crawl(schemaCrawlerOptions, handler);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, "Could not write diagram, " + dotFile, e);
    }
  }

}
