/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.util.Collection;
import java.util.HashSet;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class TableFilter
  implements NamedObjectFilter<Table>
{

  private final InclusionRule schemaInclusionRule;
  private final InclusionRule tableInclusionRule;
  private final Collection<String> tableTypes;

  public TableFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      schemaInclusionRule = options.getSchemaInclusionRule();
      tableInclusionRule = options.getTableInclusionRule();

      final Collection<String> tableTypesOptions = options.getTableTypes();
      if (tableTypesOptions == null)
        tableTypes = null;
      else
      {
        tableTypes = new HashSet<>();
        for (String tableType: tableTypesOptions)
        {
          tableTypes.add(tableType.toLowerCase());
        }
      }
    }
    else
    {
      schemaInclusionRule = null;
      tableInclusionRule = null;
      tableTypes = null;
    }
  }

  /**
   * Check for table limiting rules.
   *
   * @param table
   *        Table to check
   * @return Whether the table should be included
   */
  @Override
  public boolean include(final Table table)
  {
    boolean include = true;

    if (include && schemaInclusionRule != null)
    {
      include = schemaInclusionRule.include(table.getSchema().getFullName());
    }
    if (include && tableInclusionRule != null)
    {
      include = tableInclusionRule.include(table.getFullName());
    }
    if (include && tableTypes != null)
    {
      include = tableTypes.contains(table.getTableType().getTableType()
        .toLowerCase());
    }

    return include;
  }

}
