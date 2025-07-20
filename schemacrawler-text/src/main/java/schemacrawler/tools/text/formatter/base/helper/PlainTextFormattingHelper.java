/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.base.helper;

import static us.fatehi.utility.Utility.isBlank;

import java.io.PrintWriter;

import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.utility.Color;

/** Methods to format entire rows of output as text. */
public class PlainTextFormattingHelper extends BaseTextFormattingHelper {

  public PlainTextFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
    super(out, outputFormat);
  }

  @Override
  public String createLeftArrow() {
    return "<--";
  }

  @Override
  public String createRightArrow() {
    return "-->";
  }

  @Override
  public String createWeakLeftArrow() {
    return "<~~";
  }

  @Override
  public String createWeakRightArrow() {
    return "~~>";
  }

  /** {@inheritDoc} */
  @Override
  public void writeDocumentEnd() {
    // No output required
  }

  /** {@inheritDoc} */
  @Override
  public void writeDocumentStart() {
    // No output required
  }

  @Override
  public void writeHeader(final DocumentHeaderType type, final String header) {
    if (!isBlank(header)) {
      final String defaultSeparator = separator("=");

      final String prefix;
      final String separator;
      if (type == null) {
        prefix = System.lineSeparator();
        separator = defaultSeparator;
      } else {
        switch (type) {
          case title:
            prefix = System.lineSeparator();
            separator = separator("_");
            break;
          case section:
            prefix = "";
            separator = separator("-=-");
            break;
          case subTitle: // Fall-through
          default:
            prefix = System.lineSeparator();
            separator = defaultSeparator;
            break;
        }
      }
      out.println(
          System.lineSeparator() + prefix + header + System.lineSeparator() + separator + prefix);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectEnd() {
    out.println();
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectNameRow(
      final String id, final String name, final String description, final Color backgroundColor) {
    writeNameRow(name, description);
    out.println(DASHED_SEPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectStart() {
    // No output required
  }
}
