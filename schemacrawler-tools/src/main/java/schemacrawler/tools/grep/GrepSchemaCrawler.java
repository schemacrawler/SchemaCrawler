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


import schemacrawler.crawl.CachedSchemaCrawler;
import schemacrawler.schema.Column;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * SchemaCrawler uses database metadata to get the details about the
 * schema.
 */
public final class GrepSchemaCrawler
  extends CachedSchemaCrawler
{

  private final GrepOptions grepOptions;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param schema
   *        A schema.
   * @param grepOptions
   *        Grep options
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public GrepSchemaCrawler(final Schema schema, final GrepOptions grepOptions)
    throws SchemaCrawlerException
  {
    super(schema);

    if (grepOptions == null)
    {
      this.grepOptions = new GrepOptions();
    }
    else
    {
      this.grepOptions = grepOptions;
    }
  }

  /**
   * Crawls the schema.
   * 
   * @param handler
   *        A crawl handler instance
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void crawl(final SchemaCrawlerOptions options,
                    final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    handler.begin();

    final Table[] tables = schema.getTables();
    for (final Table table: tables)
    {
      if (includesColumn(table))
      {
        handler.handle(table);
      }
    }
    final Procedure[] procedures = schema.getProcedures();
    for (final Procedure procedure: procedures)
    {
      if (includesColumn(procedure))
      {
        handler.handle(procedure);
      }
    }
    handler.end();
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
      .getTableColumnInclusionRule();
    final InclusionRule definitionTextInclusionRule = grepOptions
      .getDefinitionTextInclusionRule();
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
    if (handleTable)
    {
      if (table instanceof View)
      {
        View view = (View) table;
        if (!definitionTextInclusionRule.include(view.getDefinition()))
        {
          handleTable = false;
        }
      }
    }
    if (invertMatch)
    {
      handleTable = !handleTable;
    }
    return handleTable;
  }

  /**
   * Special case for "grep" like functionality. Handle procedure if a
   * procedure column inclusion rule is found, and at least one column
   * matches the rule.
   * 
   * @param procedure
   *        Procedure to check
   * @param columnInclusionRule
   *        Inclusion rule for columns
   * @param invertMatch
   *        Whether to invert the procedure match
   * @return Whether the column should be included
   */
  private boolean includesColumn(final Procedure procedure)
  {
    final InclusionRule columnInclusionRule = grepOptions
      .getProcedureColumnInclusionRule();
    final InclusionRule definitionTextInclusionRule = grepOptions
      .getDefinitionTextInclusionRule();
    final boolean invertMatch = grepOptions.isInvertMatch();

    boolean handleProcedure = false;
    final ProcedureColumn[] columns = procedure.getColumns();
    for (final ProcedureColumn column: columns)
    {
      if (columnInclusionRule.include(column.getFullName()))
      {
        // We found a column that should be included, so handle the
        // procedure
        handleProcedure = true;
        break;
      }
    }
    if (handleProcedure)
    {
      if (!definitionTextInclusionRule.include(procedure.getDefinition()))
      {
        handleProcedure = false;
      }
    }
    if (invertMatch)
    {
      handleProcedure = !handleProcedure;
    }
    return handleProcedure;
  }

}
