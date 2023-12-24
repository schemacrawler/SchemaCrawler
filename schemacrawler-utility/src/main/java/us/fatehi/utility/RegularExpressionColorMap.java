/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
