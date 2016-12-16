/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import sf.util.Color;
import sf.util.RegularExpressionColorMap;

public class TestRegularExpressionColorMap
{

  private static final Color test_color = Color.fromRGB(26, 59, 92);

  @Test
  public void badColors()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC.*", "1A3B5");
    assertFalse(colorMap.match("SCH").isPresent());

    colorMap.put("SC.*", test_color.toString().substring(1) + "A");
    assertFalse(colorMap.match("SCH").isPresent());

    colorMap.put("SC.*", test_color.toString().substring(1));
    assertFalse(colorMap.match("SCH").isPresent());
  }

  @Test
  public void badPatterns()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC(H", test_color.toString());
    assertFalse(colorMap.match("SCH").isPresent());

  }

  @Test
  public void fromProperties()
  {
    final Properties properties = new Properties();
    properties.put(test_color.toString().substring(1), "SC.*");
    properties.put(test_color.toString().substring(1) + "A", "SC.*");
    properties.put("000000", "QW.*");
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap(properties);

    assertEquals(2, colorMap.size());
    assertTrue(colorMap.match("SCH").isPresent());
    assertTrue(colorMap.match("SCH").get().equals(test_color));
    assertFalse(colorMap.match("SHC").isPresent());
    assertTrue(colorMap.match("QW").isPresent());
  }

  @Test
  public void happyPath()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC.*", test_color.toString());
    assertTrue(colorMap.match("SCH").isPresent());
    assertTrue(colorMap.match("SCH").get().equals(test_color));
    assertTrue(colorMap.match("SC.*").isPresent());
    assertFalse(colorMap.match("SHC").isPresent());
  }

  @Test
  public void literals()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.putLiteral("SC.*", test_color);
    assertFalse(colorMap.match("SCH").isPresent());
    assertTrue(colorMap.match("SC.*").isPresent());

  }

}
