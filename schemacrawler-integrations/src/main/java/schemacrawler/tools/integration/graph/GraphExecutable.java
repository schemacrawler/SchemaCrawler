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

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.tools.BaseExecutable;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.text.SchemaTextFactory;
import schemacrawler.tools.text.base.CrawlHandler;
import schemacrawler.tools.text.base.Crawler;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.util.HtmlFormattingHelper;
import sf.util.FileUtility;

/**
 * Main executor for the graphing integration.
 * 
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger.getLogger(GraphExecutable.class
    .getName());

  private static String dotError()
  {
    return sf.util.Utility.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/dot.error.txt"));
  }

  @Override
  public void execute(final Connection connection)
    throws ExecutionException
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }

    adjustSchemaInfoLevel();

    final File outputFile = outputOptions.getOutputFile();
    try
    {
      final String outputFormat = outputOptions.getOutputFormatValue();
      if (outputFormat.equalsIgnoreCase("dot"))
      {
        writeDotFile(connection, FileUtility.changeFileExtension(outputFile,
                                                                 ".dot"));
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
      writeDotFile(connection, FileUtility.changeFileExtension(outputFile,
                                                               ".dot"));
      System.out.println(dotError());
    }
  }

  private void writeDotFile(final Connection connection, final File dotFile)
  {
    try
    {
      final OutputOptions outputOptions = new OutputOptions();
      outputOptions.setOutputFormatValue(OutputFormat.dot.name());
      outputOptions.setOutputFileName(dotFile.getAbsolutePath());

      final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                        outputOptions,
                                                                        SchemaTextDetailType.standard_schema);
      schemaTextOptions.setOutputOptions(outputOptions);

      final CrawlHandler handler = SchemaTextFactory
        .createSchemaTextCrawlHandler(schemaTextOptions);
      final SchemaCrawler schemaCrawler = new SchemaCrawler(connection);
      final Database database = schemaCrawler.crawl(schemaCrawlerOptions);
      final Crawler crawler = new Crawler(database);
      crawler.crawl(handler);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, "Could not write diagram, " + dotFile, e);
    }
  }

}
