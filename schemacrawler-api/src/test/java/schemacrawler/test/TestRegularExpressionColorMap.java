package schemacrawler.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sf.util.Color;
import sf.util.RegularExpressionColorMap;

public class TestRegularExpressionColorMap
{

  private static final Color test_color = Color.fromRGB(26, 59, 92);

  @Test
  public void badPatterns()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC(H", test_color.toString());
    assertFalse(colorMap.match("SCH").isPresent());

  }

  @Test
  public void badColors()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC.*", "1A3B5C");
    assertFalse(colorMap.match("SCH").isPresent());

    colorMap.put("SC.*", test_color.toString() + "A");
    assertFalse(colorMap.match("SCH").isPresent());

  }

  @Test
  public void happyPath()
  {
    final RegularExpressionColorMap colorMap = new RegularExpressionColorMap();

    colorMap.put("SC.*", test_color.toString());
    assertTrue(colorMap.match("SCH").isPresent());
    assertTrue(colorMap.match("SCH").get().equals(test_color));
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
