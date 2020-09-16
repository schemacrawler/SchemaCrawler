/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.text.utility;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.PropertiesUtility.loadProperties;
import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.DatabaseObject;
import us.fatehi.utility.Color;
import us.fatehi.utility.RegularExpressionColorMap;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;

public class DatabaseObjectColorMap {

  public static final Color default_object_color = Color.fromHSV(0, 0, 0.95f);
  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(DatabaseObjectColorMap.class.getName());
  private static final String SCHEMACRAWLER_COLORMAP_PROPERTIES =
      "schemacrawler.colormap.properties";

  public static DatabaseObjectColorMap initialize(final boolean noColors) {
    final Properties properties = new Properties();
    if (noColors) {
      return new DatabaseObjectColorMap(properties, noColors);
    }

    // Load from classpath and also current directory, in that order
    try {
      final ClasspathInputResource classpathColorMap =
          new ClasspathInputResource("/" + SCHEMACRAWLER_COLORMAP_PROPERTIES);
      properties.putAll(loadProperties(classpathColorMap));
    } catch (final IOException e) {
      LOGGER.log(Level.CONFIG, "Could not load color map from CLASSPATH");
    }

    try {
      final FileInputResource fileColorMap =
          new FileInputResource(Paths.get("./" + SCHEMACRAWLER_COLORMAP_PROPERTIES));

      properties.putAll(loadProperties(fileColorMap));
    } catch (final IOException e) {
      LOGGER.log(Level.CONFIG, "Could not load color map from file");
    }

    return new DatabaseObjectColorMap(properties, noColors);
  }

  private final RegularExpressionColorMap colorMap;
  private final boolean noColors;

  private DatabaseObjectColorMap(final Properties properties, final boolean noColors) {
    this.noColors = noColors;
    colorMap = new RegularExpressionColorMap(properties);
  }

  public Color getColor(final DatabaseObject dbObject) {
    requireNonNull(dbObject, "No database object provided");
    if (noColors) {
      return default_object_color;
    }

    final Color tableColor;
    final String schemaName = dbObject.getSchema().getFullName();
    final Optional<Color> colorMatch = colorMap.match(schemaName);
    if (!colorMatch.isPresent()) {
      tableColor = generatePastelColor(schemaName);
      colorMap.putLiteral(schemaName, tableColor);
    } else {
      tableColor = colorMatch.get();
    }
    return tableColor;
  }

  private Color generatePastelColor(final String text) {
    final float hue;
    if (isBlank(text)) {
      hue = 0.123456f;
    } else {
      final int hash = new StringBuilder(text).reverse().toString().hashCode();
      hue = hash / 32771f % 1;
    }

    final float saturation = 0.15f;
    final float brightness = 0.95f;

    final Color color = Color.fromHSV(hue, saturation, brightness);
    return color;
  }
}
