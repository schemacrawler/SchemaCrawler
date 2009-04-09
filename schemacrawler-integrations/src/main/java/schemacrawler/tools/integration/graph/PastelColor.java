package schemacrawler.tools.integration.graph;


import java.awt.Color;
import java.io.Serializable;

public final class PastelColor
  implements Serializable
{

  private static final long serialVersionUID = 7256039994498918504L;

  private static final double FACTOR = 0.87;

  private final Color color;

  public PastelColor()
  {
    color = new Color(colorValue(), colorValue(), colorValue());
  }

  private PastelColor(final Color color)
  {
    this.color = color;
  }

  public Color getColor()
  {
    return color;
  }

  public PastelColor shade()
  {
    return new PastelColor(new Color(Math.max((int) (color.getRed() * FACTOR),
                                              0), Math.max((int) (color
      .getGreen() * FACTOR), 0), Math.max((int) (color.getBlue() * FACTOR), 0)));
  }

  public PastelColor tint()
  {
    return new PastelColor(new Color(Math.min((int) (color.getRed() / FACTOR),
                                              255), Math.min((int) (color
      .getGreen() / FACTOR), 255), Math.min((int) (color.getBlue() / FACTOR),
                                            255)));
  }

  @Override
  public String toString()
  {
    return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
  }

  private int colorValue()
  {
    return (int) (Math.random() * 60 + 190);
  }

}
