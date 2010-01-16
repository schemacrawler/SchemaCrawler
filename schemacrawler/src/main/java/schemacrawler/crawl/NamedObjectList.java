/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.NamedObject;
import sf.util.Utility;

/**
 * Ordered list of named objects, that can be searched associatively.
 * 
 * @author Sualeh Fatehi
 */
class NamedObjectList<N extends NamedObject>
  implements Serializable, Iterable<N>
{

  private static final long serialVersionUID = 3257847666804142128L;

  private NamedObjectSort sort = NamedObjectSort.natural;
  /** Needs to be sorted, so serialization does not break. */
  private final Map<String, N> objects = new LinkedHashMap<String, N>();

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
      throw new IllegalArgumentException("Cannot add a null object to the list");
    }
    final String key = makeLookupKey(namedObject);
    objects.put(key, namedObject);
  }

  boolean isEmpty()
  {
    return objects.isEmpty();
  }

  N lookup(final NamedObject namedObject, final String name)
  {
    final String key = makeLookupKey(namedObject, name);
    return objects.get(key);
  }

  /**
   * Looks up a named object by name.
   * 
   * @param name
   *        Name
   * @return Named object
   */
  N lookup(final String fullName)
  {
    final String key = makeLookupKey(fullName);
    return objects.get(key);
  }

  void remove(final NamedObject namedObject)
  {
    if (namedObject != null)
    {
      objects.remove(makeLookupKey(namedObject));
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
    final List<N> all = new ArrayList<N>(objects.values());
    Collections.sort(all, sort);
    return Collections.unmodifiableList(all);
  }

  private String makeLookupKey(final NamedObject namedObject)
  {
    final String key;
    if (namedObject == null)
    {
      key = null;
    }
    else
    {
      key = namedObject.getFullName();
    }
    return key;
  }

  private String makeLookupKey(final NamedObject namedObject, final String name)
  {
    final StringBuilder buffer = new StringBuilder(256);

    final String key;
    final String namedObjectLookupKey = makeLookupKey(namedObject);

    if (namedObjectLookupKey != null)
    {
      buffer.append(namedObjectLookupKey);
    }
    if (buffer.length() > 0)
    {
      buffer.append(".");
    }
    buffer.append(name);

    key = buffer.toString();
    return key;
  }

  private String makeLookupKey(final String fullName)
  {
    final String key;
    if (Utility.isBlank(fullName))
    {
      key = null;
    }
    else
    {
      key = fullName;
    }
    return key;
  }

}
