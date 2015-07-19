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


import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import schemacrawler.schema.NamedObject;
import sf.util.ObjectToString;
import sf.util.Utility;

/**
 * Ordered list of named objects, that can be searched associatively.
 *
 * @author Sualeh Fatehi
 */
class NamedObjectList<N extends NamedObject>
  implements Serializable, Collection<N>
{

  private static final long serialVersionUID = 3257847666804142128L;

  private static String makeLookupKey(final NamedObject namedObject)
  {
    final String key;
    if (namedObject == null)
    {
      key = null;
    }
    else
    {
      key = namedObject.getLookupKey();
    }
    return key;
  }

  private static String makeLookupKey(final NamedObject namedObject,
                                      final String name)
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
      buffer.append('.');
    }
    buffer.append(name);

    key = buffer.toString();
    return key;
  }

  private static String makeLookupKey(final String fullName)
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

  private final Map<String, N> objects = new HashMap<>();

  /**
   * Add a named object to the list.
   *
   * @param namedObject
   *        Named object
   */
  @Override
  public boolean add(final N namedObject)
  {
    requireNonNull(namedObject, "Cannot add a null object to the list");
    final String key = makeLookupKey(namedObject);
    objects.put(key, namedObject);
    return true;
  }

  @Override
  public boolean addAll(final Collection<? extends N> c)
  {
    throw new UnsupportedOperationException("Bulk operations are not supported");
  }

  @Override
  public void clear()
  {
    objects.clear();
  }

  @Override
  public boolean contains(final Object object)
  {
    return objects.containsKey(makeLookupKey((N) object));
  }

  @Override
  public boolean containsAll(final Collection<?> c)
  {
    for (final Object e: c)
    {
      if (!contains(e))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isEmpty()
  {
    return objects.isEmpty();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<N> iterator()
  {
    return values().iterator();
  }

  @Override
  public boolean remove(final Object object)
  {
    final N removedObject = objects.remove(makeLookupKey((N) object));
    return removedObject != null;
  }

  @Override
  public boolean removeAll(final Collection<?> c)
  {
    if (c == null)
    {
      return false;
    }

    boolean modified = false;
    final Iterator<N> iterator = iterator();
    while (iterator.hasNext())
    {
      final N namedObject = iterator.next();
      if (namedObject != null && c.contains(namedObject))
      {
        final String key = makeLookupKey(namedObject);
        objects.remove(key);
        modified = true;
      }
    }
    return modified;
  }

  @Override
  public boolean retainAll(final Collection<?> c)
  {
    if (c == null)
    {
      return false;
    }

    boolean modified = false;
    final Iterator<N> iterator = iterator();
    while (iterator.hasNext())
    {
      final N namedObject = iterator.next();
      if (namedObject != null && !c.contains(namedObject))
      {
        final String key = makeLookupKey(namedObject);
        objects.remove(key);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return Number of elements in this list.
   */
  @Override
  public int size()
  {
    return objects.size();
  }

  @Override
  public Object[] toArray()
  {
    return values().toArray();
  }

  @Override
  public <T> T[] toArray(final T[] a)
  {
    return values().toArray(a);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(values());
  }

  Optional<N> lookup(final NamedObject namedObject, final String name)
  {
    final String key = makeLookupKey(namedObject, name);
    return internalGet(key);
  }

  /**
   * Looks up a named object by name.
   *
   * @param fullName
   *        Fully qualified name
   * @return Named object
   */
  Optional<N> lookup(final String fullName)
  {
    final String key = makeLookupKey(fullName);
    return internalGet(key);
  }

  N remove(final N namedObject)
  {
    return objects.remove(makeLookupKey(namedObject));
  }

  N remove(final String fullName)
  {
    return objects.remove(makeLookupKey(fullName));
  }

  /**
   * Gets all named objects in the list, in sorted order.
   *
   * @return All named objects
   */
  List<N> values()
  {
    final List<N> all = new ArrayList<>(objects.values());
    Collections.sort(all);
    return all;
  }

  private Optional<N> internalGet(final String key)
  {
    return Optional.ofNullable(objects.get(key));
  }

}
