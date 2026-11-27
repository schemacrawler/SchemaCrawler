/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.util.ArrayList;
import java.util.List;
import us.fatehi.utility.Inflection;

/**
 * Breaks a name into word tokens. The name is first split into segments with {@link
 * us.fatehi.utility.Utility#toSnakeCase(String)}, which also lowercases it. Each segment is then
 * broken into runs of letters, so that digits and punctuation separate words instead of joining the
 * letters on either side. Runs that are too short are dropped, and the rest are stemmed with {@link
 * Inflection#singularize(String)}. The class is generic: it works on any text name and knows
 * nothing about where the name came from.
 */
public final class NameTokenizer {

  /** Words shorter than this number of characters are dropped. */
  static final int MIN_TOKEN_LENGTH = 4;

  public static Tokenization tokenize(final String name) {
    if (isBlank(name)) {
      return new Tokenization(0, List.of());
    }
    final String[] segments = toSnakeCase(name).split("_");
    int segmentCount = 0;
    final List<Token> tokens = new ArrayList<>();
    for (final String segment : segments) {
      if (isBlank(segment)) {
        continue;
      }
      segmentCount++;
      for (final String word : letterRuns(segment)) {
        if (word.codePointCount(0, word.length()) >= MIN_TOKEN_LENGTH) {
          tokens.add(new Token(word, Inflection.singularize(word), segmentCount - 1));
        }
      }
    }
    return new Tokenization(segmentCount, tokens);
  }

  private static boolean isWordCharacter(final int codePoint) {
    if (Character.isLetter(codePoint)) {
      return true;
    }
    final int type = Character.getType(codePoint);
    return type == Character.NON_SPACING_MARK
        || type == Character.COMBINING_SPACING_MARK
        || type == Character.ENCLOSING_MARK;
  }

  private static List<String> letterRuns(final String segment) {
    final List<String> runs = new ArrayList<>();
    final StringBuilder run = new StringBuilder();
    int index = 0;
    while (index < segment.length()) {
      final int codePoint = segment.codePointAt(index);
      index += Character.charCount(codePoint);
      if (isWordCharacter(codePoint)) {
        run.appendCodePoint(codePoint);
      } else if (run.length() > 0) {
        runs.add(run.toString());
        run.setLength(0);
      }
    }
    if (run.length() > 0) {
      runs.add(run.toString());
    }
    return runs;
  }

  private NameTokenizer() {
    // Utility class
  }
}
