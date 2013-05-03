/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.crawl.filter;


import schemacrawler.schema.Column;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class FilterFactory
{

  public static NamedObjectFilter<Column> columnInclusionFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      final ChainedNamedObjectFilter<Column> filter = new ChainedNamedObjectFilter<Column>();
      filter.add(new InclusionRuleFilter<Column>(options
        .getColumnInclusionRule()));
      return filter;
    }
    else
    {
      return new PassthroughFilter<Column>();
    }
  }

  public static NamedObjectFilter<Table> grepTablesFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      return new TableGrepFilter(options);
    }
    else
    {
      return new PassthroughFilter<Table>();
    }
  }

  public static NamedObjectFilter<Routine> grepRoutinesFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      return new RoutineGrepFilter(options);
    }
    else
    {
      return new PassthroughFilter<Routine>();
    }
  }

  public static NamedObjectFilter<Table> tableInclusionFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      final ChainedNamedObjectFilter<Table> filter = new ChainedNamedObjectFilter<Table>();
      filter
        .add(new InclusionRuleFilter<Table>(options.getTableInclusionRule()));
      return filter;
    }
    else
    {
      return new PassthroughFilter<Table>();
    }
  }

  private FilterFactory()
  {
  }

}
