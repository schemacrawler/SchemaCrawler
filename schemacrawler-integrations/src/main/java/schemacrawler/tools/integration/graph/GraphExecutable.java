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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.main.HelpOptions;
import schemacrawler.main.HelpOptions.CommandHelpType;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.integration.IntegrationsExecutable;
import schemacrawler.tools.schematext.SchemaTextFactory;
import schemacrawler.tools.util.HtmlFormattingHelper;
import sf.util.Utility;

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
    return schemacrawler.utility.Utility.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/dot.error.txt"));
  }

  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    if (dataSource == null)
    {
      throw new IllegalArgumentException("No data-source provided");
    }

    final OutputOptions outputOptions = toolOptions.getOutputOptions();
    final File outputFile = outputOptions.getOutputFile();

    try
    {
      final String outputFormat = outputOptions.getOutputFormatValue();
      if (outputFormat.equalsIgnoreCase("dot"))
      {
        writeDotFile(dataSource, Utility
          .changeFileExtension(outputFile, ".dot"));
      }
      else
      {
        final File dotFile = File.createTempFile("schemacrawler.", ".dot");
        dotFile.deleteOnExit();

        final GraphGenerator dot = new GraphGenerator();
        writeDotFile(dataSource, dotFile);
        dot.generateDiagram(dotFile, outputFormat, outputFile);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not write diagram", e);
      writeDotFile(dataSource, Utility.changeFileExtension(outputFile, ".dot"));
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

  private void writeDotFile(final DataSource dataSource, final File dotFile)
  {
    try
    {
      final OutputOptions outputOptions = new OutputOptions();
      outputOptions.setOutputFormatValue(OutputFormat.dot.name());
      outputOptions.setOutputFileName(dotFile.getAbsolutePath());
      toolOptions.setOutputOptions(outputOptions);

      schemaCrawlerOptions.setSchemaInfoLevel(toolOptions.getSchemaInfoLevel());

      final CrawlHandler handler = SchemaTextFactory
        .createSchemaTextCrawlHandler(toolOptions);
      final SchemaCrawler crawler = new DatabaseSchemaCrawler(dataSource
        .getConnection());
      crawler.crawl(schemaCrawlerOptions, handler);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, "Could not write diagram, " + dotFile, e);
    }
  }

}
