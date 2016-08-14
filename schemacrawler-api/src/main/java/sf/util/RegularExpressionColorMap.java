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
package sf.util;


import static sf.util.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RegularExpressionColorMap
{

  private static final Logger LOGGER = Logger
    .getLogger(RegularExpressionColorMap.class.getName());

  private final Map<Pattern, Color> colorMap;

  public RegularExpressionColorMap()
  {
    colorMap = new HashMap<>();
  }

  public Optional<Color> match(final String value)
  {
    for (final Pattern pattern: colorMap.keySet())
    {
      if (pattern.matcher(value).matches())
      {
        return Optional.of(colorMap.get(pattern));
      }
    }
    return Optional.empty();
  }

  public void put(final String regExpPattern, final String htmlColor)
  {
    try
    {
      if (isBlank(regExpPattern))
      {
        throw new IllegalArgumentException("No regular expression pattern provided");
      }

      final Pattern pattern = Pattern.compile(regExpPattern, 0);
      final Color color = Color.fromHTMLColor(htmlColor);
      colorMap.put(pattern, color);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.CONFIG,
                 e,
                 new StringFormat("Could not add color mapping for %s = %s",
                                  regExpPattern,
                                  htmlColor));
    }
  }

  public void putLiteral(final String literal, final Color color)
  {
    try
    {
      if (isBlank(literal))
      {
        throw new IllegalArgumentException("No literal key provided");
      }

      final Pattern pattern = Pattern.compile(literal, Pattern.LITERAL);
      colorMap.put(pattern, color);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.CONFIG,
                 e,
                 new StringFormat("Could not add literal color mapping for %s = %s",
                                  literal,
                                  color));
    }
  }

}
