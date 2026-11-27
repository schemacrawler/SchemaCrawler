/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import us.fatehi.utility.string.StringFormat;

/** The tokenized names of the objects in a catalog. */
final class Names {

  /** Collects names. */
  static final class Builder {

    private final List<NamedItem> items = new ArrayList<>();

    void add(final ObjectKind kind, final DependantObject<?> object) {
      add(kind, object.key(), object.getParent().key(), object.getName());
    }

    void add(final ObjectKind kind, final NamedObject object) {
      add(kind, object.key(), null, object.getName());
    }

    Names build() {
      return new Names(items);
    }

    /**
     * Adds a name.
     *
     * @param kind The kind of object
     * @param key The key that identifies the object
     * @param parentKey The key of the table or routine that contains the object, or null for a
     *     top-level object
     * @param name The name of the object, which may be blank
     */
    private void add(
        final ObjectKind kind,
        final NamedObjectKey key,
        final NamedObjectKey parentKey,
        final String name) {
      requireNotBlank(name, "No name provided");
      requireNonNull(key, new StringFormat("No key for %s <%s>", kind, name));
      final Tokenization tokenization = NameTokenizer.tokenize(name);
      items.add(new NamedItem(kind, key, parentKey, tokenization));
    }
  }

  static Builder builder() {
    return new Builder();
  }

  private final List<NamedItem> items;
  private final int tableCount;

  private Names(final List<NamedItem> items) {
    requireNonNull(items, "No items provided");
    this.items = List.copyOf(items);
    tableCount = itemsOfKind(ObjectKind.TABLE).size();
  }

  /** Every stem used in any name. */
  Set<String> allStems() {
    final Set<String> stems = new HashSet<>();
    for (final NamedItem item : items) {
      stems.addAll(item.stems());
    }
    return stems;
  }

  List<NamedItem> items() {
    return items;
  }

  List<NamedItem> itemsOfKind(final ObjectKind kind) {
    return items.stream().filter(item -> item.kind() == kind).toList();
  }

  int tableCount() {
    return tableCount;
  }
}
