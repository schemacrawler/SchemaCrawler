/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static sf.util.PropertiesUtility.loadProperties;
import static sf.util.Utility.isBlank;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import schemacrawler.schema.DatabaseObject;
import sf.util.Color;
import sf.util.RegularExpressionColorMap;

public class DatabaseObjectColorMap
{

  private static final String SCHEMACRAWLER_COLORMAP_PROPERTIES = "schemacrawler.colormap.properties";

  public static Color default_object_color = Color.fromHSV(0, 0, 0.95f);

  public static DatabaseObjectColorMap initialize(final boolean noColors)
  {
    final Properties properties = new Properties();
    if (noColors)
    {
      return new DatabaseObjectColorMap(properties, noColors);
    }

    // Load from classpath and also current directory, in that order
    properties.putAll(loadProperties("/" + SCHEMACRAWLER_COLORMAP_PROPERTIES));
    properties.putAll(loadProperties(Paths
      .get("./" + SCHEMACRAWLER_COLORMAP_PROPERTIES)));

    return new DatabaseObjectColorMap(properties, noColors);
  }

  private final RegularExpressionColorMap colorMap;
  private final boolean noColors;

  private DatabaseObjectColorMap(final Properties properties,
                                 final boolean noColors)
  {
    this.noColors = noColors;
    colorMap = new RegularExpressionColorMap(properties);
  }

  public Color getColor(final DatabaseObject dbObject)
  {
    requireNonNull(dbObject, "No database object provided");
    if (noColors)
    {
      return default_object_color;
    }

    final Color tableColor;
    final String schemaName = dbObject.getSchema().getFullName();
    final Optional<Color> colorMatch = colorMap.match(schemaName);
    if (!colorMatch.isPresent())
    {
      tableColor = generatePastelColor(schemaName);
      colorMap.putLiteral(schemaName, tableColor);
    }
    else
    {
      tableColor = colorMatch.get();
    }
    return tableColor;
  }

  private Color generatePastelColor(final String text)
  {
    final float hue;
    if (isBlank(text))
    {
      hue = 0.123456f;
    }
    else
    {
      final int hash = new StringBuilder(text).reverse().toString().hashCode();
      hue = hash / 32771f % 1;
    }

    final float saturation = 0.15f;
    final float brightness = 0.95f;

    final Color color = Color.fromHSV(hue, saturation, brightness);
    return color;
  }

}
