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
package schemacrawler.crawl;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.DatabaseObjectFilter;
import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.schema.Synonym;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class SynonymsReducer
  implements Reducer<Synonym>
{

  private final SchemaCrawlerOptions options;

  public SynonymsReducer(final SchemaCrawlerOptions options)
  {
    this.options = options;
  }

  @Override
  public void reduce(final Collection<? extends Synonym> allSynonyms)
  {
    if (allSynonyms == null)
    {
      return;
    }
    allSynonyms.retainAll(doReduce(allSynonyms));
  }

  private Collection<Synonym> doReduce(final Collection<? extends Synonym> allSynonyms)
  {
    final NamedObjectFilter<Synonym> synonymFilter = new DatabaseObjectFilter<Synonym>(options,
                                                                                       options
                                                                                         .getSynonymInclusionRule());

    final Set<Synonym> reducedSynonyms = new HashSet<>();
    for (final Synonym synonym: allSynonyms)
    {
      if (synonymFilter.include(synonym))
      {
        reducedSynonyms.add(synonym);
      }
    }

    return reducedSynonyms;
  }

}
