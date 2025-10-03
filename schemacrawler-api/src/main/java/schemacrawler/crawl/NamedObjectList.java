/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.ReducibleCollection;
import us.fatehi.utility.ObjectToString;

/**
 * Ordered list of named objects, that can be searched associatively. NamedObjectList has the
 * ability to look up by dependant object which is not created yet. That is, by NamedObject +
 * String. Returns values sorted in natural sort order, and is iterable. The iterator does not allow
 * modifications to the underlying data structure.
 */
final class NamedObjectList<N extends NamedObject> implements Serializable, ReducibleCollection<N> {

  @Serial private static final long serialVersionUID = 3257847666804142128L;

  private static final String SCHEMACRAWLER_FILTERED_OUT = "schemacrawler.filtered_out";

  private static NamedObjectKey makeLookupKey(final NamedObject namedObject) {
    final NamedObjectKey key;
    if (namedObject == null) {
      key = null;
    } else {
      key = namedObject.key();
    }
    return key;
  }

  private static NamedObjectKey makeLookupKey(final NamedObject namedObject, final String name) {
    NamedObjectKey key = makeLookupKey(namedObject);
    if (key != null) {
      key = key.with(name);
    }
    return key;
  }

  private final Map<NamedObjectKey, N> objects = new ConcurrentHashMap<>();
  private final Map<NamedObjectKey, N> filteredObjects = new ConcurrentHashMap<>();

  /** {@inheritDoc} */
  @Override
  public synchronized void filter(final Predicate<? super N> predicate) {
    if (predicate == null) {
      return;
    }

    final Set<Entry<NamedObjectKey, N>> entrySet = objects.entrySet();
    for (final Iterator<Entry<NamedObjectKey, N>> iterator = entrySet.iterator();
        iterator.hasNext(); ) {
      final Entry<NamedObjectKey, N> entry = iterator.next();
      final NamedObjectKey namedObjectKey = entry.getKey();
      final N namedObject = entry.getValue();
      if (!predicate.test(namedObject)) {
        // Filter object by moving it to the filtered objects map
        iterator.remove();
        filteredObjects.put(namedObjectKey, namedObject);
        if (namedObject instanceof AttributedObject attributedObject) {
          attributedObject.setAttribute(SCHEMACRAWLER_FILTERED_OUT, true);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<N> iterator() {
    final class UnmodifiableIterator implements Iterator<N> {

      private final Iterator<N> iterator;

      UnmodifiableIterator(final Iterator<N> iterator) {
        this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public N next() {
        return iterator.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    return new UnmodifiableIterator(values().iterator());
  }

  /** {@inheritDoc} */
  @Override
  public synchronized void resetFilter() {
    final Set<Entry<NamedObjectKey, N>> entrySet = filteredObjects.entrySet();
    for (final Iterator<Entry<NamedObjectKey, N>> iterator = entrySet.iterator();
        iterator.hasNext(); ) {
      final Entry<NamedObjectKey, N> entry = iterator.next();
      final NamedObjectKey namedObjectKey = entry.getKey();
      final N namedObject = entry.getValue();
      objects.put(namedObjectKey, namedObject);
      iterator.remove();
      if (namedObject instanceof AttributedObject attributedObject) {
        attributedObject.removeAttribute(SCHEMACRAWLER_FILTERED_OUT);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(values());
  }

  /**
   * Add a named object to the list.
   *
   * @param namedObject Named object
   */
  boolean add(final N namedObject) {
    requireNonNull(namedObject, "Cannot add a null object to the list");
    final NamedObjectKey key = makeLookupKey(namedObject);
    objects.put(key, namedObject);
    return true;
  }

  boolean contains(final NamedObject namedObject) {
    return objects.containsKey(makeLookupKey(namedObject));
  }

  boolean isEmpty() {
    return objects.isEmpty();
  }

  Optional<N> lookup(final NamedObject namedObject, final String name) {
    final NamedObjectKey key = makeLookupKey(namedObject, name);
    return internalGet(key);
  }

  /**
   * Looks up a named object by lookup key.
   *
   * @param lookupKey Internal lookup key
   * @return Named object
   */
  Optional<N> lookup(final NamedObjectKey lookupKey) {
    return internalGet(lookupKey);
  }

  N remove(final N namedObject) {
    return objects.remove(makeLookupKey(namedObject));
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return Number of elements in this list.
   */
  int size() {
    return objects.size();
  }

  /**
   * Gets all named objects in the list, in sorted order.
   *
   * @return All named objects
   */
  List<N> values() {
    final List<N> all = new ArrayList<>(objects.values());
    all.sort(naturalOrder());
    return all;
  }

  private Optional<N> internalGet(final NamedObjectKey key) {
    return Optional.ofNullable(objects.get(key));
  }
}
