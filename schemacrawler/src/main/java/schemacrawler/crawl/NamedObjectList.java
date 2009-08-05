/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.utility.Utility;

/**
 * Ordered list of named objects, that can be searched associatively.
 * 
 * @author Sualeh Fatehi
 */
class NamedObjectList<N extends NamedObject>
  implements Serializable, Iterable<N>
{

  private static final long serialVersionUID = 3257847666804142128L;

  private NamedObjectSort sort;
  /** Needs to be sorted, so serialization does not break. */
  private final Set<N> objects = new LinkedHashSet<N>();

  /**
   * Construct an initially empty ordered list of named objects, that
   * can be searched associatively.
   * 
   * @param sort
   *        Comparator for named objects, or null for no sorting
   */
  NamedObjectList(final NamedObjectSort sort)
  {
    this.sort = sort;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<N> iterator()
  {
    return values().iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return values().toString();
  }

  /**
   * Add a named object to the list.
   * 
   * @param namedObject
   *        Named object
   */
  void add(final N namedObject)
  {
    if (namedObject == null)
    {
      throw new IllegalArgumentException("Cannot add an object to the list");
    }
    objects.add(namedObject);
  }

  N lookup(final DatabaseObject databaseObject, final String name)
  {
    return lookup(new AbstractDependantObject(databaseObject, name)
    {

      private static final long serialVersionUID = -6700397214465123353L;
    });
  }

  /**
   * Looks up a named object by name.
   * 
   * @param name
   *        Name
   * @return Named object
   */
  N lookup(final String name)
  {
    return lookup(new AbstractNamedObject(name)
    {

      private static final long serialVersionUID = 7241388569507782902L;

    });
  }

  void remove(final NamedObject namedObject)
  {
    if (namedObject != null)
    {
      objects.remove(namedObject);
    }
  }

  void setSortOrder(final NamedObjectSort sort)
  {
    this.sort = sort;
  }

  /**
   * Returns the number of elements in this list.
   * 
   * @return Number of elements in this list.
   */
  int size()
  {
    return objects.size();
  }

  /**
   * Gets all named objects in the list, in sorted order.
   * 
   * @return All named objects
   */
  List<N> values()
  {
    final List<N> all = new ArrayList<N>(objects);
    Collections.sort(all, sort);
    return Collections.unmodifiableList(all);
  }

  private N lookup(final NamedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }
    final boolean hasBlankName = Utility.isBlank(namedObject.getName());
    for (final N listItem: objects)
    {
      if (hasBlankName && Utility.isBlank(listItem.getName()))
      {
        return listItem;
      }
      else if (namedObject.equals(listItem))
      {
        return listItem;
      }
    }
    return null;
  }

}
