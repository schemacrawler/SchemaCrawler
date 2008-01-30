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

package schemacrawler.tools.grep;


import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.InclusionRule;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * SchemaCrawler uses database metadata to get the details about the
 * schema.
 */
public final class GrepSchemaCrawler
{

  private final Schema schema;

  private final GrepOptions grepOptions;
  private final CrawlHandler handler;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param schema
   *        A schema.
   * @param crawlHandler
   *        A crawl handler instance
   * @param grepOptions
   *        Grep options
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public GrepSchemaCrawler(final Schema schema,
                           final GrepOptions grepOptions,
                           final CrawlHandler crawlHandler)
    throws SchemaCrawlerException
  {
    if (schema == null)
    {
      throw new SchemaCrawlerException("No schema specified");
    }
    this.schema = schema;

    if (grepOptions == null)
    {
      this.grepOptions = new GrepOptions();
    }
    else
    {
      this.grepOptions = grepOptions;
    }

    if (crawlHandler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }
    handler = crawlHandler;
  }

  /**
   * Crawls the schema.
   * 
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void crawl()
    throws SchemaCrawlerException
  {
    handler.begin();
    // handler.handle(schema.getDatabaseInfo());

    final Table[] tables = schema.getTables();
    for (final Table table: tables)
    {
      if (includesColumn(table))
      {
        handler.handle(table);
      }
    }

    // final Procedure[] procedures = schema.getProcedures();
    // for (final Procedure procedure: procedures)
    // {
    // handler.handle(procedure);
    // }

    handler.end();
  }

  /**
   * Gets the entire schema.
   * 
   * @return Schema
   */
  public Schema getSchema()
  {
    return schema;
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   * 
   * @param table
   *        Table to check
   * @param columnInclusionRule
   *        Inclusion rule for columns
   * @param invertMatch
   *        Whether to invert the table match
   * @return Whether the column should be included
   */
  private boolean includesColumn(final Table table)
  {
    final InclusionRule columnInclusionRule = grepOptions
      .getColumnInclusionRule();
    final boolean invertMatch = grepOptions.isInvertMatch();

    boolean handleTable = false;
    final Column[] columns = table.getColumns();
    for (final Column column: columns)
    {
      if (columnInclusionRule.include(column.getFullName()))
      {
        // We found a column that should be included, so handle the
        // table
        handleTable = true;
        break;
      }
    }
    if (invertMatch)
    {
      handleTable = !handleTable;
    }
    return handleTable;
  }

}
