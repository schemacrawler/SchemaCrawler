/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.schematext;


import javax.sql.DataSource;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerExecutable
  extends Executable<SchemaTextOptions>
{

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
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

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, handler);
    crawler.crawl(schemaCrawlerOptions);
  }

}
