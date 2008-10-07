/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.tools.grep;


import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Greps a schema.
 * 
 * @author Sualeh Fatehi
 */
public final class GrepCrawlHandler
  implements CrawlHandler
{

  private final GrepOptions grepOptions;
  private final CrawlHandler chainedCrawlHandler;

  /**
   * Constructs a crawl handler for grep.
   * 
   * @param grepOptions
   *        Grep options
   * @param chainedCrawlHandler
   *        Handler that handles result of the grep
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public GrepCrawlHandler(final GrepOptions grepOptions,
                          final CrawlHandler chainedCrawlHandler)
    throws SchemaCrawlerException
  {
    if (chainedCrawlHandler == null)
    {
      throw new SchemaCrawlerException("No chained crawl handler provided");
    }
    this.chainedCrawlHandler = chainedCrawlHandler;

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
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    chainedCrawlHandler.begin();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    chainedCrawlHandler.end();
  }

  public void handle(ColumnDataType dataType)
    throws SchemaCrawlerException
  {
    // Ignore
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SchemaCrawlerException
   */
  public void handle(final DatabaseInfo databaseInfo)
    throws SchemaCrawlerException
  {
    chainedCrawlHandler.handle(databaseInfo);
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SchemaCrawlerException
   * @see schemacrawler.schemacrawler.CrawlHandler#handle(schemacrawler.schema.JdbcDriverInfo)
   */
  public void handle(final JdbcDriverInfo driverInfo)
    throws SchemaCrawlerException
  {
    chainedCrawlHandler.handle(driverInfo);
  }

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void handle(final Procedure procedure)
    throws SchemaCrawlerException
  {
    if (include(procedure))
    {
      chainedCrawlHandler.handle(procedure);
    }
  }

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void handle(final Table table)
    throws SchemaCrawlerException
  {
    if (include(table))
    {
      chainedCrawlHandler.handle(table);
    }
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
  private boolean include(final Procedure procedure)
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
  private boolean include(final Table table)
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
        final View view = (View) table;
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

}
