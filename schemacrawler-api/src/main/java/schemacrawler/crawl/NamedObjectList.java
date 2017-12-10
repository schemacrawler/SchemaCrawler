/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

/**
 * Ordered list of named objects, that can be searched associatively.
 *
 * @author Sualeh Fatehi
 */
class NamedObjectList<N extends NamedObject>
  implements Serializable, Collection<N>
{

  private static final long serialVersionUID = 3257847666804142128L;

  private static List<String> makeLookupKey(final NamedObject namedObject)
  {
    final List<String> key;
    if (namedObject == null)
    {
      key = null;
    }
    else
    {
      key = namedObject.toUniqueLookupKey();
    }
    return key;
  }

  private static List<String> makeLookupKey(final NamedObject namedObject,
                                            final String name)
  {
    final List<String> key = makeLookupKey(namedObject);
    key.add(name);
    return key;
  }

  private static List<String> makeLookupKey(final String fullName)
  {
    final List<String> key = new ArrayList<>();
    if (fullName == null)
    {
      return key;
    }
    final String[] lookupKey = fullName
      .split("\\.(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    for (final String string: lookupKey)
    {
      key.add(string.replace("\"", ""));
    }
    return key;
  }

  private final Map<List<String>, N> objects = new HashMap<>();

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
    final List<String> key = makeLookupKey(namedObject);
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
        final List<String> key = makeLookupKey(namedObject);
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
        final List<String> key = makeLookupKey(namedObject);
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
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(values());
  }

  /**
   * Looks up a named object by lookup key.
   *
   * @param fullName
   *        Fully qualified name
   * @return Named object
   */
  Optional<N> lookup(final List<String> lookupKey)
  {
    return internalGet(lookupKey);
  }

  Optional<N> lookup(final NamedObject namedObject, final String name)
  {
    final List<String> key = makeLookupKey(namedObject, name);
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
    return internalGet(makeLookupKey(fullName));
  }

  N remove(final N namedObject)
  {
    return objects.remove(makeLookupKey(namedObject));
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

  private Optional<N> internalGet(final List<String> key)
  {
    return Optional.ofNullable(objects.get(key));
  }

}
