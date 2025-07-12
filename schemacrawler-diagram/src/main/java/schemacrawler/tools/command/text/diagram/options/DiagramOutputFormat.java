/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

public enum DiagramOutputFormat implements OutputFormat {
  htmlx("SchemaCrawler diagram embedded in HTML5"),
  scdot("SchemaCrawler generated format"),
  //
  bmp("Windows Bitmap Format"),
  canon("DOT"),
  dot("DOT"),
  gv("DOT"),
  xdot("DOT", "xdot1.2", "xdot1.4"),
  cgimage("CGImage bitmap format"),
  cmap("Client-side imagemap (deprecated)"),
  eps("Encapsulated PostScript"),
  exr("OpenEXR"),
  fig("FIG"),
  gd("GD/GD2 formats", "gd2"),
  gif("GIF"),
  gtk("GTK canvas"),
  ico("Icon Image File Format"),
  imap("Server-side and client-side imagemaps", "cmapx", "imap_np", "cmapx_np"),
  jp2("JPEG 2000"),
  jpg("JPEG", "jpeg", "jpe"),
  pct("PICT", "pict"),
  pdf("Portable Document Format (PDF)"),
  pic("Kernighan's PIC graphics language"),
  plain("Simple text format", "plain-ext"),
  png("Portable Network Graphics format"),
  pov("POV-Ray markup language (prototype)"),
  ps("PostScript"),
  ps2("PostScript for PDF"),
  psd("PSD"),
  sgi("SGI"),
  svg("Scalable Vector Graphics"),
  svgz("Scalable Vector Graphics"),
  tga("Truevision TGA"),
  tiff("TIFF (Tag Image File Format)", "tif"),
  tk("TK graphics"),
  vml("Vector Markup Language (VML)"),
  vmlz("Vector Markup Language (VML)"),
  vrml("VRML"),
  wbmp("Wireless BitMap format"),
  webp("Image format for the Web"),
  xlib("Xlib canvas", "x11"),
  ;

  private static final Logger LOGGER = Logger.getLogger(DiagramOutputFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Diagram output format.
   * @return DiagramOutputFormat
   */
  public static DiagramOutputFormat fromFormat(final String format) {
    final DiagramOutputFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null) {
      LOGGER.log(Level.CONFIG, new StringFormat("Unknown format <%s>, using default", format));
      return png;
    } else {
      return outputFormat;
    }
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a diagram output format
   */
  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  private static DiagramOutputFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final DiagramOutputFormat outputFormat : DiagramOutputFormat.values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  private final OutputFormatState outputFormatState;

  DiagramOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
  }

  DiagramOutputFormat(final String description, final String... additionalFormatSpecifiers) {
    outputFormatState = new OutputFormatState(name(), description, additionalFormatSpecifiers);
  }

  @Override
  public String getDescription() {
    return outputFormatState.getDescription();
  }

  @Override
  public String getFormat() {
    return outputFormatState.getFormat();
  }

  @Override
  public List<String> getFormats() {
    return outputFormatState.getFormats();
  }

  @Override
  public String toString() {
    return outputFormatState.toString();
  }
}
