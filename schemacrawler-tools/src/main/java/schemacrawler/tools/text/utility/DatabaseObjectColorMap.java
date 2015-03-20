package schemacrawler.tools.text.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.DatabaseObject;

public class DatabaseObjectColorMap
{

  public static String getHtmlColor(final Color color)
  {
    final String htmlColor = "#"
                             + Integer.toHexString(color.getRGB()).substring(2)
                               .toUpperCase();
    return htmlColor;
  }

  private final Map<String, Color> colorMap;

  public DatabaseObjectColorMap()
  {
    colorMap = new HashMap<>();
  }

  public Color getColor(final DatabaseObject dbObject)
  {
    requireNonNull(dbObject, "No database object provided");
    final Color tableColor;
    final String schemaName = dbObject.getSchema().getFullName();
    if (!colorMap.containsKey(schemaName))
    {
      tableColor = generatePastelColor(schemaName);
      colorMap.put(schemaName, tableColor);
    }
    else
    {
      tableColor = colorMap.get(schemaName);
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
      final int hash = new StringBuffer().append(text).reverse().toString()
        .hashCode();
      hue = hash / 32771f % 1;
    }

    final float saturation = 0.15f;
    final float luminance = 0.95f;

    final Color color = Color.getHSBColor(hue, saturation, luminance);
    return color;
  }
}
