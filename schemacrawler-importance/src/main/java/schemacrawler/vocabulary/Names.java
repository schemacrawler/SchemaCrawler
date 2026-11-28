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
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import us.fatehi.utility.string.StringFormat;

/**
 * The tokenized names of the objects in a catalog, indexed once by the kind of object and by stem,
 * so that each step of the analysis can look up what it needs.
 */
final class Names {

  /** Collects names. */
  static final class Builder {

    private final List<NamedItem> items = new ArrayList<>();

    void add(final ObjectKind kind, final DependantObject<?> object) {
      requireNonNull(kind, "No object kind provided");
      requireNonNull(object, "No object provided");
      add(kind, object.key(), object.getParent().key(), object.getName());
    }

    void add(final ObjectKind kind, final NamedObject object) {
      requireNonNull(kind, "No object kind provided");
      requireNonNull(object, "No object provided");
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
      requireNonNull(kind, "No object kind provided");
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
  private final Map<ObjectKind, List<NamedItem>> itemsByKind;
  private final Map<String, StemUsage> usageByStem;

  private Names(final List<NamedItem> items) {
    requireNonNull(items, "No items provided");
    this.items = List.copyOf(items);
    itemsByKind = groupByKind(this.items);
    usageByStem = indexByStem(this.items);
  }

  /** Every stem used in any name. The set cannot be modified. */
  Set<String> allStems() {
    return usageByStem.keySet();
  }

  List<NamedItem> items() {
    return items;
  }

  List<NamedItem> itemsOfKind(final ObjectKind kind) {
    return itemsByKind.getOrDefault(kind, List.of());
  }

  /** The distinct owners that use a stem in any of their names, or none if it is not used. */
  Set<NamedObjectKey> ownersOf(final String stem) {
    final StemUsage usage = usageByStem.get(stem);
    return usage == null ? Set.of() : usage.owners();
  }

  int tableCount() {
    return itemsOfKind(ObjectKind.TABLE).size();
  }

  /** How each stem is used, sorted by stem. The map cannot be modified. */
  Map<String, StemUsage> usageByStem() {
    return usageByStem;
  }

  private static Map<ObjectKind, List<NamedItem>> groupByKind(final List<NamedItem> items) {
    final Map<ObjectKind, List<NamedItem>> byKind = new EnumMap<>(ObjectKind.class);
    for (final NamedItem item : items) {
      byKind.computeIfAbsent(item.kind(), key -> new ArrayList<>()).add(item);
    }
    byKind.replaceAll((kind, kindItems) -> List.copyOf(kindItems));
    return Collections.unmodifiableMap(byKind);
  }

  private static Map<String, StemUsage> indexByStem(final List<NamedItem> items) {
    final Map<String, StemUsage> byStem = new TreeMap<>();
    for (final NamedItem item : items) {
      for (final Token token : item.tokenization().tokens()) {
        byStem
            .computeIfAbsent(token.stem(), key -> new StemUsage())
            .add(item.parentKey(), token.word());
      }
    }
    return Collections.unmodifiableMap(byStem);
  }
}
