package schemacrawler.tools.integration.graph;


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.options.OutputFormat;

public enum GraphOutputFormat
  implements OutputFormat
{

  scdot("scdot", "SchemaCrawler generated format"),
  bmp("bmp", "Windows Bitmap Format"),
  canon("canon", "DOT"),
  dot("dot", "DOT"),
  gv("gv", "DOT"),
  xdot("xdot", "DOT"),
  xdot1_2("xdot1.2", "DOT"),
  xdot1_4("xdot1.4", "DOT"),
  cgimage("cgimage", "CGImage bitmap format"),
  cmap("cmap", "Client-side imagemap (deprecated)"),
  eps("eps", "Encapsulated PostScript"),
  exr("exr", "OpenEXR"),
  fig("fig", "FIG"),
  gd("gd", "GD/GD2 formats"),
  gd2("gd2", "GD/GD2 formats"),
  gif("gif", "GIF"),
  gtk("gtk", "GTK canvas"),
  ico("ico", "Icon Image File Format"),
  imap("imap", "Server-side and client-side imagemaps"),
  cmapx("cmapx", "Server-side and client-side imagemaps"),
  imap_np("imap_np", "Server-side and client-side imagemaps"),
  cmapx_np("cmapx_np", "Server-side and client-side imagemaps"),
  ismap("ismap", "Server-side imagemap (deprecated)"),
  jp2("jp2", "JPEG 2000"),
  jpg("jpg", "JPEG"),
  jpeg("jpeg", "JPEG"),
  jpe("jpe", "JPEG"),
  pct("pct", "PICT"),
  pict("pict", "PICT"),
  pdf("pdf", "Portable Document Format (PDF)"),
  pic("pic", "Kernighan's PIC graphics language"),
  plain("plain", "Simple text format"),
  plain_ext("plain-ext", "Simple text format"),
  png("png", "Portable Network Graphics format"),
  pov("pov", "POV-Ray markup language (prototype)"),
  ps("ps", "PostScript"),
  ps2("ps2", "PostScript for PDF"),
  psd("psd", "PSD"),
  sgi("sgi", "SGI"),
  svg("svg", "Scalable Vector Graphics"),
  svgz("svgz", "Scalable Vector Graphics"),
  tga("tga", "Truevision TGA"),
  tif("tif", "TIFF (Tag Image File Format)"),
  tiff("tiff", "TIFF (Tag Image File Format)"),
  tk("tk", "TK graphics"),
  vml("vml", "Vector Markup Language (VML)"),
  vmlz("vmlz", "Vector Markup Language (VML)"),
  vrml("vrml", "VRML"),
  wbmp("wbmp", "Wireless BitMap format"),
  webp("webp", "Image format for the Web"),
  xlib("xlib", "Xlib canvas"),
  x11("x11", "Xlib canvas"), ;

  /**
   * Gets the value from the format.
   *
   * @param format
   *        Graph format.
   * @return GraphOutputFormat
   */
  public static GraphOutputFormat fromFormat(final String format)
  {
    final GraphOutputFormat graphFormat = fromFormatOrNull(format);
    if (graphFormat == null)
    {
      LOGGER.log(Level.CONFIG, "Unknown format, " + format);
      return png;
    }
    else
    {
      return graphFormat;
    }
  }

  /**
   * Checks the value of the format.
   *
   * @return If the format is a graph output format
   */
  public static boolean isGraphOutputFormat(final String format)
  {
    return fromFormatOrNull(format) != null;
  }

  private static GraphOutputFormat fromFormatOrNull(final String format)
  {
    for (final GraphOutputFormat graphFormat: GraphOutputFormat.values())
    {
      if (graphFormat.getFormat().equalsIgnoreCase(format))
      {
        return graphFormat;
      }
    }
    return null;
  }

  private final String format;
  private final String description;

  private static final Logger LOGGER = Logger.getLogger(GraphOutputFormat.class
    .getName());

  private GraphOutputFormat(final String format, final String description)
  {
    this.format = format;
    this.description = description;
  }

  @Override
  public String getDescription()
  {
    return description;
  }

  @Override
  public String getFormat()
  {
    return format;
  }

  @Override
  public String toString()
  {
    return String.format("%s - %s", format, description);
  }

}
