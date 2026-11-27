/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.stream.Collectors;
import schemacrawler.schema.NamedObjectKey;

/**
 * One name in the catalog.
 *
 * @param kind The kind of object
 * @param key Identifies the object, since names of different objects can repeat
 * @param parentKey The key of the table or routine that contains the object, if it is a column,
 *     trigger, constraint, index or routine parameter, otherwise null
 * @param tokenization The words in the name
 */
record NamedItem(
    ObjectKind kind, NamedObjectKey key, NamedObjectKey parentKey, Tokenization tokenization) {

  NamedItem {
    requireNonNull(kind, "No object kind provided");
    requireNonNull(key, "No key provided");
    parentKey = parentKey != null ? parentKey : key;
    requireNonNull(tokenization, "No tokenization provided");
  }

  /** The distinct stems of the words in the name. */
  Set<String> stems() {
    return tokenization.tokens().stream().map(Token::stem).collect(Collectors.toUnmodifiableSet());
  }
}
