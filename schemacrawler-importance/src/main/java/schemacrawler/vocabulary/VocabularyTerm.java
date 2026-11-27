/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.Serial;
import java.io.Serializable;

/**
 * A word used in the names of catalog objects. A term is serializable, and holds only strings and a
 * number.
 *
 * @param word The word shown for the stem, lowercased. It is the alphabetically first word that
 *     differs from the stem, which is usually the plural, or the stem itself when that is the only
 *     word written. It always occurs in a name, and is the same as the stem when only the singular
 *     is written.
 * @param stem The singular form of the word, used as the key that groups {@code order} and {@code
 *     orders} together.
 * @param objectCount Number of distinct catalog objects whose names contain the word.
 */
public record VocabularyTerm(String word, String stem, int objectCount) implements Serializable {

  @Serial private static final long serialVersionUID = 4837192056731420915L;

  public VocabularyTerm {
    word = trimToEmpty(word);
    stem = trimToEmpty(stem);
    objectCount = Math.max(0, objectCount);
  }
}
