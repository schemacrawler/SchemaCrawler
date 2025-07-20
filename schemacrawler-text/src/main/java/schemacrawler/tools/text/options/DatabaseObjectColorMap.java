/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.DatabaseObject;
import us.fatehi.utility.Color;
import us.fatehi.utility.RegularExpressionColorMap;

public class DatabaseObjectColorMap {

  private final RegularExpressionColorMap colorMap;

  /** Color map where all test maps to a grey color. */
  public DatabaseObjectColorMap() {
    final Map<String, String> properties = new HashMap<>();
    properties.put(Color.fromHSV(0, 0, 0.95f).toString().substring(1), ".*");
    colorMap = new RegularExpressionColorMap(properties);
  }

  /**
   * Color map with provided properties. Properties are loaded using key of the HTML color without
   * #, and the regular expression as the value.
   *
   * @param properties Color map
   */
  public DatabaseObjectColorMap(final Map<String, String> properties) {
    requireNonNull(properties, "No properties provided");
    colorMap = new RegularExpressionColorMap(properties);
  }

  public Color getColor(final DatabaseObject dbObject) {
    requireNonNull(dbObject, "No database object provided");

    final String schemaName = dbObject.getSchema().getFullName();
    final Color dbObjectColor =
        colorMap
            .match(schemaName)
            .orElseGet(
                () -> {
                  final Color color = generatePastelColor(schemaName);
                  colorMap.putLiteral(schemaName, color);
                  return color;
                });
    return dbObjectColor;
  }

  @Override
  public String toString() {
    return colorMap.toString();
  }

  private Color generatePastelColor(final String text) {
    final float hue;
    if (isBlank(text)) {
      hue = 0.123456f;
    } else {
      final int hash = new StringBuilder(text).reverse().toString().hashCode();
      hue = hash / 32771f % 1;
    }

    final float saturation = 0.20f;
    final float brightness = 0.95f;

    final Color color = Color.fromHSV(hue, saturation, brightness);
    return color;
  }
}
