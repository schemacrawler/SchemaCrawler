/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static us.fatehi.utility.Utility.isBlank;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import schemacrawler.schema.DatabaseObject;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class MarkdownFormattingHelper {

  public static String encodeFullName(final DatabaseObject databaseObject) {
    if (databaseObject == null) {
      return "";
    }
    return URLEncoder.encode(databaseObject.getFullName(), StandardCharsets.UTF_8)
        .replace("+", "%20");
  }

  public static String sentenceCase(final String text) {
    if (isBlank(text)) {
      return "";
    }
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
  }

  static String escapeMarkdown(final String input) {
    if (isBlank(input)) {
      return "";
    }

    final StringBuilder sb = new StringBuilder(input.length() * 2);
    input
        .replaceAll("\\R", " ")
        .codePoints()
        .forEach(
            cp -> {
              if (isMarkdownSpecial(cp)) {
                sb.append('\\');
              }
              sb.appendCodePoint(cp);
            });

    return sb.toString().replaceAll("\\R", "");
  }

  private static boolean isMarkdownSpecial(final int cp) {
    return cp == '\\'
        || cp == '`'
        || cp == '*'
        || cp == '_'
        || cp == '{'
        || cp == '}'
        || cp == '['
        || cp == ']'
        || cp == '('
        || cp == ')'
        || cp == '#'
        || cp == '+'
        || cp == '-'
        || cp == '.'
        || cp == '!'
        || cp == '|'
        || cp == '>';
  }

  private MarkdownFormattingHelper() {
    // Prevent instantiation
  }
}
