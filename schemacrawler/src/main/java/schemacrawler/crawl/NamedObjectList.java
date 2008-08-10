/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;

/**
 * Ordered list of named objects, that can be searched associatively.
 * 
 * @author Sualeh Fatehi
 */
final class NamedObjectList<N extends NamedObject>
  implements Serializable, Iterable<N>
{

  private static final long serialVersionUID = 3257847666804142128L;

  private NamedObjectSort sort;
  /** Needs to be sorted, so serialization does not break. */
  private final SortedSet<N> objects;

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
    this.objects = new TreeSet<N>();
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
    objects.add(namedObject);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof NamedObjectList))
    {
      return false;
    }
    final NamedObjectList<N> other = (NamedObjectList<N>) obj;
    if (objects == null)
    {
      if (other.objects != null)
      {
        return false;
      }
    }
    else if (!objects.equals(other.objects))
    {
      return false;
    }
    return true;
  }

  /**
   * Gets all named objects in the list, in sorted order.
   * 
   * @return All named objects
   */
  List<N> getAll()
  {
    final List<N> all = new ArrayList<N>(objects);
    Collections.sort(all, sort);
    return Collections.unmodifiableList(all);
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
    result = prime * result + (objects == null? 0: objects.hashCode());
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

  N lookup(final DatabaseObject databaseObject, final String name)
  {
    final AbstractDatabaseObject parent = new AbstractDatabaseObject(databaseObject
      .getCatalogName(),
      databaseObject.getSchemaName(),
      databaseObject.getName())
    {

      private static final long serialVersionUID = 2521419524823080025L;

    };
    return lookup(new AbstractDependantObject(parent, name)
    {

      private static final long serialVersionUID = -6700397214465123353L;
    });
  }

  private N lookup(final NamedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }
    for (final N listItem: objects)
    {
      if (namedObject.equals(listItem))
      {
        return listItem;
      }
    }
    return null;
  }

  /**
   * Looks up a named object by name.
   * 
   * @param name
   *        Name
   * @return Named object
   */
  @SuppressWarnings("serial")
  N lookup(final String name)
  {
    return lookup(new AbstractNamedObject(name)
    {

    });
  }

  /**
   * Looks up a named object by name.
   * 
   * @param name
   *        Name
   * @return Named object
   */
  @SuppressWarnings("serial")
  N lookup(final String catalogName, final String schemaName, final String name)
  {
    return lookup(new AbstractDatabaseObject(catalogName, schemaName, name)
    {

    });
  }

  private N remove(final NamedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }
    for (final Iterator<N> iterator = objects.iterator(); iterator.hasNext();)
    {
      final N listItem = iterator.next();
      if (namedObject.equals(listItem))
      {
        iterator.remove();
        return listItem;
      }
    }
    return null;
  }

  @SuppressWarnings("serial")
  N remove(final String name)
  {
    return remove(new AbstractNamedObject(name)
    {

    });
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
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getAll().toString();
  }

}
