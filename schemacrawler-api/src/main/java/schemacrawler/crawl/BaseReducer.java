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
package schemacrawler.crawl;


import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Reducer;

abstract class BaseReducer<N extends NamedObject>
  implements Reducer<N>
{

  private final Predicate<N> filter;

  protected BaseReducer(final Predicate<N> filter)
  {
    this.filter = requireNonNull(filter);
  }

  @Override
  public void reduce(final Collection<? extends N> allNamedObjects)
  {
    if (allNamedObjects != null)
    {
      final Collection<N> keepList = new HashSet<>();
      for (final N namedObject: allNamedObjects)
      {
        if (filter.test(namedObject))
        {
          keepList.add(namedObject);
        }
      }
      allNamedObjects.retainAll(keepList);
    }
  }

}
