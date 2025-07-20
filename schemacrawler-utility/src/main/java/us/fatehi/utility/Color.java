/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import java.util.Objects;
import java.util.regex.Pattern;
import static us.fatehi.utility.Utility.requireNotBlank;

/**
 * Color breaks the dependency on java.awt.Color. The AWT comes with a lot of baggage, and is not
 * part of Java Compact Profile 2.
 */
public final class Color {

  public static final Color white = new Color(255, 255, 255);

  private static Pattern htmlColorPattern = Pattern.compile("^#[0-9A-Fa-f]{6}$");

  public static Color fromHexTriplet(final String htmlColor) {
    requireNotBlank(htmlColor, "No color provided");

    if (!htmlColorPattern.matcher(htmlColor).matches()) {
      throw new IllegalArgumentException(String.format("Bad color provided <%s>", htmlColor));
    }

    // Parse color
    final int r = Integer.parseInt(htmlColor.substring(1, 3), 16);
    final int g = Integer.parseInt(htmlColor.substring(3, 5), 16);
    final int b = Integer.parseInt(htmlColor.substring(5, 7), 16);

    return new Color(r, g, b);
  }

  /**
   * <a href= "http://stackoverflow.com/questions/7896280"> Converting from HSV (HSB in Java) to RGB
   * without using java.awt.Color</a>
   */
  public static Color fromHSV(final float hue, final float saturation, final float value) {
    final float normalizedHue = abs(hue - (float) floor(hue));
    final int h = (int) (normalizedHue * 6);
    final float f = normalizedHue * 6 - h;
    final float p = value * (1 - saturation);
    final float q = value * (1 - f * saturation);
    final float t = value * (1 - (1 - f) * saturation);

    switch (h) {
      case 0:
        return fromRGB(value, t, p);
      case 1:
        return fromRGB(q, value, p);
      case 2:
        return fromRGB(p, value, t);
      case 3:
        return fromRGB(p, q, value);
      case 4:
        return fromRGB(t, p, value);
      case 5:
        return fromRGB(value, p, q);
      default:
        // This case is not expected
        throw new IllegalArgumentException();
    }
  }

  public static Color fromRGB(final int r, final int g, final int b) {
    return new Color(r, g, b);
  }

  private static Color fromRGB(final float r, final float g, final float b) {
    return new Color((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
  }

  private final int b;
  private final int g;
  private final int r;

  private Color(final int r, final int g, final int b) {
    if (r < 0) {
      this.r = 0;
    } else {
      this.r = Math.min(r, 255);
    }

    if (g < 0) {
      this.g = 0;
    } else {
      this.g = Math.min(g, 255);
    }

    if (b < 0) {
      this.b = 0;
    } else {
      this.b = Math.min(b, 255);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    final Color other = (Color) obj;
    if ((b != other.b) || (g != other.g)) {
      return false;
    }
    return r == other.r;
  }

  @Override
  public int hashCode() {
    return Objects.hash(b, g, r);
  }

  @Override
  public String toString() {
    final int rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;

    final String htmlColor = "#" + String.format("%06x", rgb).toUpperCase();
    return htmlColor;
  }
}
