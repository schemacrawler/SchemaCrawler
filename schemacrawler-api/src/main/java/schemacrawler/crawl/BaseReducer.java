/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

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
