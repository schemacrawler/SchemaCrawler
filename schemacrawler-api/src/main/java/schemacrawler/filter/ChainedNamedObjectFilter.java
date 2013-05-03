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
package schemacrawler.filter;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.NamedObject;

class ChainedNamedObjectFilter<N extends NamedObject>
  implements NamedObjectFilter<N>
{

  private final List<NamedObjectFilter<N>> filters = new ArrayList<>();

  @Override
  public boolean include(final N namedObject)
  {
    for (final NamedObjectFilter<N> filter: filters)
    {
      if (filter.include(namedObject))
      {
        return true;
      }
    }
    return false;
  }

  void add(final NamedObjectFilter<N> filter)
  {
    if (filter != null)
    {
      filters.add(filter);
    }
  }

}
