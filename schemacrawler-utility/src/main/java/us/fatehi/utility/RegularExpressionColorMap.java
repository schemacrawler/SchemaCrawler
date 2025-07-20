/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import us.fatehi.utility.string.StringFormat;

public class RegularExpressionColorMap {

  private static final Logger LOGGER = Logger.getLogger(RegularExpressionColorMap.class.getName());

  private final Map<Pattern, Color> colorMap;

  public RegularExpressionColorMap() {
    colorMap = new HashMap<>();
  }

  public RegularExpressionColorMap(final Map<String, String> properties) {
    this();
    if (properties == null || properties.isEmpty()) {
      return;
    }
    for (final Entry<String, String> map : properties.entrySet()) {
      if (map != null) {
        final String htmlColor = map.getKey();
        final String regExpPattern = map.getValue();
        if (!isBlank(regExpPattern) && !isBlank(htmlColor) && htmlColor.length() == 6) {
          put(regExpPattern, "#" + htmlColor);
        } else {
          LOGGER.log(
              Level.CONFIG,
              new StringFormat(
                  "Could not add color mapping for %s = %s", regExpPattern, htmlColor));
        }
      }
    }
  }

  public Optional<Color> match(final String value) {
    for (final Entry<Pattern, Color> regualarExpressColor : colorMap.entrySet()) {
      final Pattern pattern = regualarExpressColor.getKey();
      if (pattern.matcher(value).matches()) {
        return Optional.of(regualarExpressColor.getValue());
      }
    }
    return Optional.empty();
  }

  public void put(final String regExpPattern, final String htmlColor) {
    try {
      requireNotBlank(regExpPattern, "No regular expression pattern provided");

      final Pattern pattern = Pattern.compile(regExpPattern, 0);
      final Color color = Color.fromHexTriplet(htmlColor);
      colorMap.put(pattern, color);
    } catch (final Exception e) {
      LOGGER.log(
          Level.CONFIG,
          e,
          new StringFormat("Could not add color mapping for %s = %s", regExpPattern, htmlColor));
    }
  }

  public void putLiteral(final String literal, final Color color) {
    try {
      requireNotBlank(literal, "No literal key provided");

      final Pattern pattern = Pattern.compile(literal, Pattern.LITERAL);
      colorMap.put(pattern, color);
    } catch (final IllegalArgumentException e) {
      LOGGER.log(Level.FINE, e.getMessage());
    } catch (final Exception e) {
      LOGGER.log(
          Level.CONFIG,
          e,
          new StringFormat("Could not add literal color mapping for %s = %s", literal, color));
    }
  }

  public int size() {
    return colorMap.size();
  }

  @Override
  public String toString() {
    return Objects.toString(colorMap);
  }
}
