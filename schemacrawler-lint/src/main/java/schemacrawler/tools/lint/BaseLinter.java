/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public abstract class BaseLinter
  extends BaseLinterCatalog
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLinter.class.getName());

  private Catalog catalog;
  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;
  private TableTypesFilter tableTypesFilter;

  protected BaseLinter()
  {
    setTableTypesFilter(null);
    setTableInclusionRule(null);
    setColumnInclusionRule(null);
  }

  @Override
  public void configure(final LinterConfig linterConfig)
  {
    super.configure(linterConfig);
    if (linterConfig != null)
    {
      setTableInclusionRule(linterConfig.getTableInclusionRule());
      setColumnInclusionRule(linterConfig.getColumnInclusionRule());
    }
  }

  @Override
  public final void lint(final Catalog catalog, final Connection connection)
    throws SchemaCrawlerException
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");

    start();
    for (final Table table: catalog.getTables())
    {
      if (tableInclusionRule.test(table.getFullName())
          && tableTypesFilter.test(table))
      {
        lint(table, connection);
      }
      else
      {
        LOGGER
          .log(Level.FINE,
               String.format("Excluding table %s for lint %s", table, getId()));
      }
    }
    end();
    this.catalog = null;
  }

  protected final void addCatalogLint(final String message)
  {
    addLint(catalog, message, null);
  }

  protected final <V extends Serializable> void addCatalogLint(final String message,
                                                               final V value)
  {
    addLint(catalog, message, value);
  }

  protected final void addTableLint(final Table table, final String message)
  {
    addLint(table, message, null);
  }

  protected final <V extends Serializable> void addTableLint(final Table table,
                                                             final String message,
                                                             final V value)
  {
    addLint(table, message, value);
  }

  protected void end()
  {
  }

  protected List<Column> getColumns(final Table table)
  {
    if (table == null)
    {
      return Collections.emptyList();
    }

    final List<Column> columns = new ArrayList<>(table.getColumns());
    for (final Iterator<Column> iterator = columns.iterator(); iterator
      .hasNext();)
    {
      final Column column = iterator.next();
      if (!includeColumn(column))
      {
        iterator.remove();
      }
    }
    return columns;
  }

  protected CrawlInfo getCrawlInfo()
  {
    return catalog.getCrawlInfo();
  }

  protected TableTypesFilter getTableTypesFilter()
  {
    return tableTypesFilter;
  }

  protected boolean includeColumn(final Column column)
  {
    return column != null && columnInclusionRule.test(column.getFullName());
  }

  protected boolean includeTable(final Table table)
  {
    return table != null && tableInclusionRule.test(table.getFullName());
  }

  protected void lint(final Table table, final Connection connection)
    throws SchemaCrawlerException
  {
  }

  protected void setColumnInclusionRule(final InclusionRule columnInclusionRule)
  {
    if (columnInclusionRule == null)
    {
      this.columnInclusionRule = new IncludeAll();
    }
    else
    {
      this.columnInclusionRule = columnInclusionRule;
    }
  }

  protected void setTableInclusionRule(final InclusionRule tableInclusionRule)
  {
    if (tableInclusionRule == null)
    {
      this.tableInclusionRule = new IncludeAll();
    }
    else
    {
      this.tableInclusionRule = tableInclusionRule;
    }
  }

  protected void setTableTypesFilter(final TableTypesFilter tableTypesFilter)
  {
    if (tableTypesFilter == null)
    {
      this.tableTypesFilter = new TableTypesFilter();
    }
    else
    {
      this.tableTypesFilter = tableTypesFilter;
    }
  }

  protected void start()
  {
  }

}
