package schemacrawler.vocabulary;

import java.util.List;

/**
 * The result of tokenizing one name.
 *
 * @param segmentCount Number of segments in the name, counted before letter runs are separated, so
 *     {@code address1} has one segment and {@code customer_id} has two
 * @param tokens Tokens of the name, in order, including repeats
 */
public record Tokenization(int segmentCount, List<Token> tokens) {
  public Tokenization {
    tokens = List.copyOf(tokens);
  }
}
