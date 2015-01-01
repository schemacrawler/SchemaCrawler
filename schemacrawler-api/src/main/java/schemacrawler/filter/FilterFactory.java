/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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
package schemacrawler.filter;


import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class FilterFactory
{

  public static NamedObjectFilter<Routine> routineFilter(final SchemaCrawlerOptions options)
  {
    final ChainedNamedObjectFilter<Routine> routineFilter = new ChainedNamedObjectFilter<>();
    routineFilter.add(new RoutineTypesFilter(options));
    routineFilter.add(new DatabaseObjectFilter<Routine>(options, options
      .getRoutineInclusionRule()));
    routineFilter.add(new RoutineGrepFilter(options));

    return routineFilter;
  }

  public static NamedObjectFilter<Table> tableFilter(final SchemaCrawlerOptions options)
  {
    final ChainedNamedObjectFilter<Table> tableFilter = new ChainedNamedObjectFilter<>();
    tableFilter.add(new TableTypesFilter(options));
    tableFilter.add(new DatabaseObjectFilter<Table>(options, options
      .getTableInclusionRule()));
    tableFilter.add(new TableGrepFilter(options));

    return tableFilter;
  }

  private FilterFactory()
  {
  }

}
