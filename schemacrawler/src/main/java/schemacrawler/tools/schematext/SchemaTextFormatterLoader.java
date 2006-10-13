/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;

/**
 * Formats output as text.
 */
public final class SchemaTextFormatterLoader
{

  private SchemaTextFormatterLoader()
  {
  }

  /**
   * Checks if the text formatter mnemonic is valid.
   * 
   * @param textFormatterName
   *        Mnemonic name for a text formatter
   * @return True if the mnemonic is known
   */
  private static boolean canLoad(final SchemaTextOptions options)
  {
    return options.getSchemaTextDetailType() != null;
  }

  /**
   * Instantiates a text formatter from the mnemonic string.
   * 
   * @param options
   *        Options
   * @throws SchemaCrawlerException
   *         On an exception
   * @return CrawlHandler instance
   */
  public static CrawlHandler load(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {

    if (!canLoad(options))
    {
      return null;
    }

    CrawlHandler handler = null;
    final OutputOptions outputOptions = options.getOutputOptions();
    final OutputFormat outputFormatType = outputOptions.getOutputFormat();
    if (outputFormatType == OutputFormat.HTML)
    {
      handler = new SchemaHTMLFormatter(options);
    } else
    {
      handler = new SchemaTextFormatter(options);
    }

    return handler;

  }

}
