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
package schemacrawler.crawl;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class SchemasReducer
  implements Reducer<Schema>
{

  private final InclusionRuleFilter<Schema> schemaFilter;

  public SchemasReducer(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      schemaFilter = new InclusionRuleFilter<>(null, true);
    }
    else
    {
      schemaFilter = new InclusionRuleFilter<>(options.getSchemaInclusionRule(),
                                               true);
    }
  }

  @Override
  public void reduce(final Collection<? extends Schema> allSchemas)
  {
    if (allSchemas == null)
    {
      return;
    }
    allSchemas.retainAll(doReduce(allSchemas));
  }

  private Collection<Schema> doReduce(final Collection<? extends Schema> allSchemas)
  {
    final Set<Schema> reducedSchemas = new HashSet<>();
    for (final Schema schema: allSchemas)
    {
      if (schemaFilter.test(schema))
      {
        reducedSchemas.add(schema);
      }
    }

    return reducedSchemas;
  }

}
