package schemacrawler.vocabulary;

import us.fatehi.utility.Inflection;

/**
 * A word taken from a name.
 *
 * @param word One word as it is written in the name, lowercased. It is a single string, not a list:
 *     the name {@code Orders_Archive} gives two tokens, one with the word {@code orders} and one
 *     with the word {@code archive}.
 * @param stem The singular form of the NFKC-normalized word, from {@link Inflection#singularize},
 *     so that full-width and half-width spellings of a word have the same stem. The word itself is
 *     not normalized. For {@code orders} the stem is {@code order}; for {@code status} it is {@code
 *     status}. It is the key used to count {@code order} and {@code orders} as the same word.
 * @param segment Zero-based position of the segment the word comes from, so the first word of
 *     {@code customer_order} has segment 0 and the second has segment 1. Digits and punctuation
 *     inside a segment do not change it, so {@code abcd1efgh} gives two tokens in segment 0.
 */
public record Token(String word, String stem, int segment) {

  /**
   * Whether the word comes from the first segment of the name, which is the position of a prefix
   * such as {@code proc} in {@code proc_orders}. A short first segment that was dropped does not
   * count, so the first token of {@code no_customer} is not in the first segment.
   */
  public boolean isFirstSegment() {
    return segment == 0;
  }
}
