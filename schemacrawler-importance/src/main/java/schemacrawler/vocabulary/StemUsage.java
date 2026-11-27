/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import schemacrawler.schema.NamedObjectKey;

/**
 * How one stem is used in the names of a catalog: the objects that use it, and the words in which
 * it is written. A stem can have more than one word, such as {@code order} and {@code orders}. The
 * owner of a name is the table or routine that contains the named object, or the object itself for
 * a top-level object, so an owner that uses the stem in many names is still one owner.
 */
final class StemUsage {

  private final Set<NamedObjectKey> owners = new HashSet<>();
  // In alphabetical order, so that the shown word does not depend on the order of the names
  private final Set<String> words = new TreeSet<>();

  /** Records that an owner uses the stem, in the given word. Used while the names are indexed. */
  void add(final NamedObjectKey owner, final String word) {
    owners.add(owner);
    words.add(word);
  }

  /** The number of distinct owners that use the stem, in any of its words. */
  int objectCount() {
    return owners.size();
  }

  /** The distinct owners that use the stem. The set cannot be modified. */
  Set<NamedObjectKey> owners() {
    return Collections.unmodifiableSet(owners);
  }

  /** The distinct words written for the stem, in alphabetical order. The set cannot be modified. */
  Set<String> words() {
    return Collections.unmodifiableSet(words);
  }
}
