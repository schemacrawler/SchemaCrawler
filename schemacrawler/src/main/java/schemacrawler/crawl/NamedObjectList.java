/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import schemacrawler.schema.NamedObject;

/**
 * Ordered list of named objects, that can be searched associatively.
 * 
 * @author Sualeh Fatehi
 */
final class NamedObjectList<N extends AbstractNamedObject>
  implements Serializable, Iterable<N>
{

  enum NamedObjectSort
    implements Comparator<NamedObject>
  {

    /** Alphabetical sort. */
    alphabetical,
    /** Natural sort. */
    natural;

    /**
     * {@inheritDoc}
     * 
     * @see Comparator#compare(Object, Object)
     */
    public int compare(final NamedObject namedObject1,
                       final NamedObject namedObject2)
    {
      switch (this)
      {
        case alphabetical:
          return namedObject1.toString().compareToIgnoreCase(namedObject2
            .toString());
        case natural:
          return namedObject1.compareTo(namedObject2);
        default:
          return 0;
      }
    }

  }

  private static final long serialVersionUID = 3257847666804142128L;

  private NamedObjectSort sort;
  /**
   * Map of names and named objects. This map needs to be sorted so that
   * schema serialization does not break.
   */
  private final SortedMap<String, N> map;

  /**
   * Construct an initially empty ordered list of named objects, that
   * can be searched associatively.
   * 
   * @param sort
   *        Comparator for named objects, or null for no sorting
   */
  NamedObjectList(NamedObjectSort sort)
  {
    this.sort = sort;
    this.map = new TreeMap<String, N>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof NamedObjectList)) return false;
    final NamedObjectList<N> other = (NamedObjectList<N>) obj;
    if (map == null)
    {
      if (other.map != null) return false;
    }
    else if (!map.equals(other.map)) return false;
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((map == null)? 0: map.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<N> iterator()
  {
    return getAll().iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getAll().toString();
  }

  /**
   * Add a named object to the list.
   * 
   * @param namedObject
   *        Named object
   */
  void add(final N namedObject)
  {
    if (namedObject == null || namedObject.getName() == null)
    {
      throw new IllegalArgumentException("Cannot add an object to the list");
    }
    map.put(namedObject.getName(), namedObject);
  }

  /**
   * Gets all named objects in the list, in sorted order.
   * 
   * @return All named objects
   */
  List<N> getAll()
  {
    List<N> all = new ArrayList<N>(map.values());
    Collections.sort(all, sort);
    return Collections.unmodifiableList(all);
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
    return map.get(name);
  }

  N remove(final String namedObjectName)
  {
    return map.remove(namedObjectName);
  }

  void setSortOrder(NamedObjectSort sort)
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
    return map.size();
  }

}
