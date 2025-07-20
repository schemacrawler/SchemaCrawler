/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.lang.Character.isWhitespace;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@UtilityMarker
public final class Utility {

  public static String commonPrefix(final String string1, final String string2) {
    if (string1 == null || string2 == null) {
      return "";
    }
    final int index = indexOfDifference(string1, string2);
    if (index < 1) {
      return "";
    }
    return string1.substring(0, index).toLowerCase();
  }

  /**
   * Roughly converts database object names so that they can be compared with others in a
   * case-insensitive way. This code is not meant to "correct" from a Unicode perspective, but a
   * quick and dirty way of stripping out quote characters, and lower-casing them for comparison.
   *
   * @param text Text to convert
   * @return Text that can be compared
   */
  public static String convertForComparison(final String text) {
    if (text == null || text.length() == 0) {
      return "";
    }

    final StringBuilder builder = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      final char ch = text.charAt(i);
      if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '.') {
        builder.append(Character.toLowerCase(ch));
      }
    }

    final String textWithoutQuotes = builder.toString();
    return textWithoutQuotes;
  }

  /**
   * Checks if the text is all lowercase.
   *
   * @param text Text to check.
   * @return Whether the string is all lowercase.
   */
  public static boolean hasNoUpperCase(final String text) {
    return text != null && text.equals(text.toLowerCase());
  }

  /**
   * Checks if the text is null or empty.
   *
   * @param text Text to check.
   * @return Whether the string is blank.
   */
  public static boolean isBlank(final CharSequence text) {
    if (text == null || text.length() == 0) {
      return true;
    }

    for (int i = 0; i < text.length(); i++) {
      if (!isWhitespace(text.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if a class is available on the classpath.
   *
   * @param className Class to check
   * @return True if the class is available, false otherwise
   */
  public static boolean isClassAvailable(final String className) {
    try {
      Class.forName(className, false, Utility.class.getClassLoader());
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  /**
   * Checks if the text contains an integer only.
   *
   * @param text Text to check.
   * @return Whether the string is an integer.
   */
  public static boolean isIntegral(final CharSequence text) {
    if (text == null || text.length() == 0) {
      return false;
    }

    for (int i = 0; i < text.length(); i++) {
      final char ch = text.charAt(i);
      if (!Character.isDigit(ch) && ch != '+' && ch != '-') {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the string contains typical regex characters.
   *
   * @param input Input expression
   * @return Whether the string has regular expression characters
   */
  public static boolean isRegularExpression(final String input) {
    final String regexMetaChars = ".*[.*+?^$|{}()\\[\\]].*";
    if (!input.matches(regexMetaChars)) {
      return false;
    }

    // Attempt to compile the string as a regex,
    // and if successful, it's a valid regex
    try {
      Pattern.compile(input);
      return true;
    } catch (final PatternSyntaxException e) {
      return false;
    }
  }

  public static String join(final Collection<String> collection, final String separator) {
    if (collection == null || collection.isEmpty()) {
      return null;
    }

    final StringJoiner joiner = new StringJoiner(separator);
    joiner.setEmptyValue("");
    for (final String string : collection) {
      joiner.add(string);
    }

    return joiner.toString();
  }

  public static String join(final Map<?, ?> map, final String separator) {
    if (map == null || map.isEmpty()) {
      return null;
    }

    final StringJoiner joiner = new StringJoiner(separator);
    for (final Entry<?, ?> entry : map.entrySet()) {
      joiner.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
    }

    return joiner.toString();
  }

  /**
   * Checks if the text is null or empty, and throws an exception if it is.
   *
   * @param text Text to check.
   * @return Provided string, if not blank.
   * @throws IllegalArgumentException If the provided string is blank
   */
  public static String requireNotBlank(final String text, final String message) {
    if (isBlank(text)) {
      throw new IllegalArgumentException(message);
    }
    return text;
  }

  public static String stripEnd(final String text) {
    if (text == null || text.length() == 0) {
      return "";
    }
    int end = text.length();
    while (end > 0 && isWhitespace(text.charAt(end - 1))) {
      end--;
    }
    return text.substring(0, end);
  }

  public static String stripStart(final String text) {
    if (text == null || text.length() == 0) {
      return "";
    }
    final int length = text.length();
    int start = 0;
    while (start != length && isWhitespace(text.charAt(start))) {
      start++;
    }
    return text.substring(start);
  }

  public static String toSnakeCase(final String identifier) {
    if (isBlank(identifier)) {
      return identifier;
    }
    final Pattern identifyCamelCase = Pattern.compile("([A-Z])");
    final String snakeCaseIdentifier =
        identifyCamelCase.matcher(identifier).replaceAll("_$1").toLowerCase().replace(' ', '_');
    return snakeCaseIdentifier;
  }

  public static String trimToEmpty(final String text) {
    if (isBlank(text)) {
      return "";
    }
    return stripEnd(stripStart(text));
  }

  private static int indexOfDifference(final String string1, final String string2) {
    if (string1 == null || string2 == null) {
      return 0;
    }
    int i;
    for (i = 0; i < string1.length() && i < string2.length(); ++i) {
      if (string1.charAt(i) != string2.charAt(i)) {
        break;
      }
    }
    if (i < string2.length() || i < string1.length()) {
      return i;
    }
    return -1;
  }

  private Utility() {
    // Prevent instantiation
  }
}
