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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.NamedObject;
import schemacrawler.util.SerializableComparator;

/**
 * Ordered list of named objects, that can be searched associatively.
 * 
 * @author sfatehi
 */
public final class NamedObjectList
  implements Serializable
{

  private static final long serialVersionUID = 3257847666804142128L;

  private static final Logger LOGGER = Logger.getLogger(NamedObjectList.class
    .getName());

  private SerializableComparator comparator;
  private final List sortedList;
  private final Map map;

  /**
   * Construct an initially empty ordered list of named objects, that
   * can be searched associately.
   * 
   * @param serializableComparator
   *        Comparator for named objects, or null for no sorting
   */
  public NamedObjectList(final SerializableComparator serializableComparator)
  {
    comparator = serializableComparator;
    sortedList = new LinkedList();
    map = new TreeMap();
  }

  /**
   * Add a named object to the list.
   * 
   * @param namedObject
   *        Named object
   */
  public void add(final NamedObject namedObject)
  {
    if (namedObject == null || namedObject.getName() == null)
    {
      throw new IllegalArgumentException("Cannot add an object to the list");
    }

    remove(namedObject.getName());

    map.put(namedObject.getName(), namedObject);
    int index = Collections.binarySearch(sortedList, namedObject, comparator);
    if (index < 0)
    {
      index = -index - 1;
      sortedList.add(index, namedObject);
    }
    else
    {
      sortedList.add(namedObject);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (o == null)
    {
      return false;
    }

    if (o.getClass() != getClass())
    {
      return false;
    }

    final NamedObjectList other = (NamedObjectList) o;
    if (sortedList == null)
    {
      if (other.sortedList != null)
      {
        return false;
      }
    }
    else
    {
      if (!sortedList.equals(other.sortedList))
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Gets the object at a given index.
   * 
   * @see java.util.List#get(int)
   * @param index
   *        Index of the requested object
   * @return Named object
   */
  public NamedObject get(final int index)
  {
    return (NamedObject) sortedList.get(index);
  }

  /**
   * Gets the map of named objects.
   * 
   * @return Map
   */
  public Map getMap()
  {
    return new HashMap(map);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 1000003;
    int result = 0;
    if (sortedList != null)
    {
      result = prime * result + sortedList.hashCode();
    }

    return result;
  }

  /**
   * Looks up a named object by name.
   * 
   * @param name
   *        Name
   * @return Named object
   */
  public NamedObject lookup(final String name)
  {
    return (NamedObject) map.get(name);
  }

  /**
   * Returns the number of elements in this list.
   * 
   * @return Number of elements in this list.
   */
  public int size()
  {
    return sortedList.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return sortedList.toString();
  }

  /**
   * Add a named object at a given ordinal position. If the ordinal
   * position is beyond the end of the list, add the object to the end.
   * 
   * @param ordinalPosition
   *        Position to add at, starting from 1
   * @param namedObject
   *        Named object to add
   */
  void add(final int ordinalPosition, final NamedObject namedObject)
  {
    if (namedObject == null || namedObject.getName() == null)
    {
      throw new IllegalArgumentException("Cannot add an object to the list");
    }

    final int size = sortedList.size();
    int index = ordinalPosition - 1;
    if (index < 0)
    {
      index = 0;
    }
    else if (index > size)
    {
      index = size;
    }
    // Add the object in a new position
    if (LOGGER.isLoggable(Level.FINEST))
    {
      String message = "Adding \"" + namedObject + "\" at position #" + index;
      if (index != ordinalPosition - 1)
      {
        message = message + " (instead of at position #"
                  + (ordinalPosition - 1) + ")";
      }
      LOGGER.log(Level.FINEST, message);
    }
    remove(namedObject.getName());
    sortedList.add(index, namedObject);
    map.put(namedObject.getName(), namedObject);
  }

  /**
   * Gets all named objects in the list, in sorted order.
   * 
   * @return All named objects
   */
  List getAll()
  {
    return new ArrayList(sortedList);
  }

  /**
   * Remove a named object by name.
   * 
   * @param name
   *        Name
   * @return Object that was removed
   */
  NamedObject remove(final String name)
  {
    if (map.containsKey(name))
    {
      final NamedObject namedObject = (NamedObject) map.remove(name);
      sortedList.remove(namedObject);
      return namedObject;
    }
    return null;
  }

  /**
   * Sets the comparator, and re-sorts the list.
   * 
   * @param comparator
   *        Comparator
   */
  void setComparator(final SerializableComparator comparator)
  {
    this.comparator = comparator;
    Collections.sort(sortedList, comparator);
  }

}
