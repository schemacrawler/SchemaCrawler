/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.tools.grep;


import javax.sql.DataSource;

import schemacrawler.crawl.CachingCrawlerHandler;
import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Schema;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaHTMLFormatter;
import schemacrawler.tools.schematext.SchemaTextFormatter;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class GrepExecutable
  extends Executable<GrepOptions>
{

  /**
   * Sets up default options.
   */
  public GrepExecutable()
  {
    toolOptions = new GrepOptions();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {

    final Schema schema = getEntireSchema(dataSource);

    CrawlHandler handler = null;
    final OutputOptions outputOptions = toolOptions.getOutputOptions();
    final OutputFormat outputFormatType = outputOptions.getOutputFormat();
    if (outputFormatType == OutputFormat.html)
    {
      handler = new SchemaHTMLFormatter(toolOptions);
    }
    else
    {
      handler = new SchemaTextFormatter(toolOptions);
    }

    // The GrepSchemaCrawler is capable of crawling a previously cached
    // schema.
    final GrepSchemaCrawler crawler = new GrepSchemaCrawler(schema,
                                                            toolOptions,
                                                            handler);
    crawler.crawl();
  }

  /**
   * Gets a subset of the entire schema, that is, the grep results.
   * 
   * @param dataSource
   * @throws Exception
   */
  public Schema getSchema(final DataSource dataSource)
    throws Exception
  {

    final Schema schema = getEntireSchema(dataSource);
    final CachingCrawlerHandler handler = new CachingCrawlerHandler();
    // The GrepSchemaCrawler is capable of crawling a previously cached
    // schema.
    final GrepSchemaCrawler crawler = new GrepSchemaCrawler(schema,
                                                            toolOptions,
                                                            handler);
    crawler.crawl();

    return handler.getSchema();
  }

  private Schema getEntireSchema(final DataSource dataSource)
  {
    // Force certain SchemaCrawler options
    schemaCrawlerOptions.setShowStoredProcedures(false);
    schemaCrawlerOptions.setTableInclusionRule(toolOptions
      .getTableInclusionRule());

    // Get the entire schema at once
    final Schema schema = SchemaCrawler.getSchema(dataSource, toolOptions
      .getSchemaTextDetailType().mapToInfoLevel(), schemaCrawlerOptions);
    return schema;
  }

}
