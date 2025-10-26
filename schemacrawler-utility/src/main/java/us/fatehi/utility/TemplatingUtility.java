/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

@UtilityMarker
public final class TemplatingUtility {

  private static final String DELIMITER_END = "}";
  private static final int DELIMITER_END_LENGTH = DELIMITER_END.length();
  private static final String DELIMITER_START = "${";
  private static final int DELIMITER_START_LENGTH = DELIMITER_START.length();

  /**
   * Expands a template using system properties. Variables in the template are in the form of
   * ${variable}.
   *
   * @param template Template to expand.
   * @return Expanded template
   */
  public static String expandTemplate(final String template) {
    return expandTemplate(template, PropertiesUtility.propertiesMap(System.getProperties()));
  }

  /**
   * Expands a template using variable values in the provided map. Variables in the template are in
   * the form of ${variable}.
   *
   * @param template Template to expand.
   * @param variablesMap Variables and values.
   * @return Expanded template
   */
  public static String expandTemplate(
      final String template, final Map<String, String> variablesMap) {
    if (isBlank(template) || variablesMap == null) {
      return template;
    }

    final StringBuilder buffer = new StringBuilder(template.length());
    int currentPosition = 0;
    int delimiterStartPosition;
    int delimiterEndPosition;

    while (true) {
      delimiterStartPosition = template.indexOf(DELIMITER_START, currentPosition);
      if (delimiterStartPosition == -1) {
        if (currentPosition == 0) {
          // No substitutions required at all
          return template;
        }
        // No more substitutions
        buffer.append(template.substring(currentPosition));
        return buffer.toString();
      }
      buffer.append(template, currentPosition, delimiterStartPosition);
      delimiterEndPosition = template.indexOf(DELIMITER_END, delimiterStartPosition);
      if (delimiterEndPosition > -1) {
        delimiterStartPosition = delimiterStartPosition + DELIMITER_START_LENGTH;
        final String key =
            trimToEmpty(template.substring(delimiterStartPosition, delimiterEndPosition));
        final String value = variablesMap.get(key);
        if (value != null) {
          buffer.append(value);
        } else {
          // Do not substitute
          buffer
              .append(DELIMITER_START)
              .append(template, delimiterStartPosition, delimiterEndPosition)
              .append(DELIMITER_END);
        }
        // Advance current position
        currentPosition = delimiterEndPosition + DELIMITER_END_LENGTH;
      } else {
        // End brace not found, so advance current position
        buffer.append(DELIMITER_START);
        currentPosition = delimiterStartPosition + DELIMITER_START_LENGTH;
      }
    }
  }

  /**
   * Extracts variables from the template. Variables are in the form of ${variable}.
   *
   * @param template Template to extract variables from.
   * @return Extracted variables
   */
  public static Set<String> extractTemplateVariables(final String template) {

    if (isBlank(template)) {
      return Collections.emptySet();
    }

    String shrunkTemplate = template;
    final Set<String> keys = new HashSet<>();
    for (int left; (left = shrunkTemplate.indexOf(DELIMITER_START)) >= 0; ) {
      final int right = shrunkTemplate.indexOf(DELIMITER_END, left + 2);
      if (right < 0) {
        // No ending bracket found
        break;
      }
      final String propertyKey = trimToEmpty(shrunkTemplate.substring(left + 2, right));
      keys.add(propertyKey);
      // Destroy key, so we can find the next one
      shrunkTemplate = shrunkTemplate.substring(0, left) + shrunkTemplate.substring(right + 1);
    }

    return keys;
  }

  private TemplatingUtility() {}
}
