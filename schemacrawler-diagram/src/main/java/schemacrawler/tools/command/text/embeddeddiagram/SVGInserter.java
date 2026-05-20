/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.embeddeddiagram;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.regex.Pattern;
import us.fatehi.utility.ioresource.FileInputResource;

class SVGInserter {

  private enum SvgParseState {
    PREAMBLE,
    SVG_BODY
  }

  private static final Pattern svgStart = Pattern.compile("<svg.*");

  private final FileInputResource svgResource;

  SVGInserter(final Path svgFilePath) {
    requireNonNull(svgFilePath, "No SVG file path provided");
    svgResource = new FileInputResource(svgFilePath);
  }

  void insert(final Writer writer) throws IOException {
    requireNonNull(writer, "No writer provided");
    writer.write(System.lineSeparator());
    SvgParseState state = SvgParseState.PREAMBLE;
    try (final BufferedReader svgReader = svgResource.openNewInputReader(UTF_8)) {
      String line;
      while ((line = svgReader.readLine()) != null) {
        switch (state) {
          case PREAMBLE:
            if (svgStart.matcher(line).find()) {
              writer.write("<svg");
              writer.write(System.lineSeparator());
              state = SvgParseState.SVG_BODY;
            }
            break;
          case SVG_BODY:
            writer.write(line);
            writer.write(System.lineSeparator());
            break;
        }
      }
    }
    writer.write(System.lineSeparator());
  }
}
