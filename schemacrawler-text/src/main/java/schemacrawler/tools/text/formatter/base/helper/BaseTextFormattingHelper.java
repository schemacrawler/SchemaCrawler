/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.base.helper;

import static us.fatehi.utility.ObjectToString.arrayToList;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.html.TagBuilder.anchor;
import static us.fatehi.utility.html.TagBuilder.tableCell;
import static us.fatehi.utility.html.TagBuilder.tableHeaderCell;
import static us.fatehi.utility.html.TagBuilder.tableRow;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.utility.BinaryData;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagBuilder;
import us.fatehi.utility.html.TagOutputFormat;

/** Methods to format entire rows of output as HTML. */
abstract class BaseTextFormattingHelper implements TextFormattingHelper {

  /** System specific line separator character. */
  static final String DASHED_SEPARATOR = separator("-");

  static String separator(final String pattern) {
    final StringBuilder dashedSeparator = new StringBuilder(72);
    for (int i = 0; i < 72 / pattern.length(); i++) {
      dashedSeparator.append(pattern);
    }
    return dashedSeparator.toString();
  }

  private static String toString(final Object dataElement) {
    if (dataElement == null) {
      return "NULL";
    }
    if (dataElement.getClass().isArray()) {
      return arrayToList(dataElement).toString();
    } else if (dataElement instanceof Number number) {
      if (number.doubleValue() == number.longValue()) {
        return number.toString();
      } else {
        // Avoid floating-point imprecision across operating systems
        final int scale = 2;
        BigDecimal roundedNumber = new BigDecimal(number.toString());
        roundedNumber = roundedNumber.setScale(scale, RoundingMode.HALF_UP);
        return roundedNumber.toString();
      }
    }

    return dataElement.toString();
  }

  protected final PrintWriter out;

  private final TextOutputFormat outputFormat;

  public BaseTextFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
    this.out = out;
    this.outputFormat = outputFormat;
  }

  @Override
  public TextFormattingHelper append(final String text) {
    out.write(text);
    out.flush();

    return this;
  }

  @Override
  public String createAnchor(final String text, final String link) {
    return anchor()
        .withEscapedText(text)
        .withHyperlink(link)
        .make()
        .render(TagOutputFormat.valueOf(outputFormat.name()));
  }

  @Override
  public void println() {
    out.println();
  }

  /** {@inheritDoc} */
  @Override
  public void writeDescriptionRow(final String description) {
    final Tag row = tableRow().make();
    row.addInnerTag(tableCell().withStyleClass("spacer").make());
    row.addInnerTag(tableCell().withEscapedText(description).withColumnSpan(2).make());
    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /** {@inheritDoc} */
  @Override
  public void writeDetailRow(final String text1, final String text2, final String text3) {
    writeDetailRow(text1, text2, text3, true, false, "");
  }

  /** {@inheritDoc} */
  @Override
  public void writeDetailRow(
      final String text1,
      final String text2,
      final String text3,
      final boolean escapeText,
      final boolean emphasize,
      final String style) {
    final int text2Width = 32;
    final int text3Width = 28;
    final String text3Sytle;
    if (!isBlank(style)) {
      text3Sytle = " " + style;
    } else {
      text3Sytle = "";
    }

    final Tag row = tableRow().make();
    if (isBlank(text1)) {
      row.addInnerTag(tableCell().withStyleClass("spacer").make());
    } else {
      row.addInnerTag(
          tableCell().withEscapedText(text1).withWidth(2).withStyleClass("spacer").make());
    }

    row.addInnerTag(
        tableCell()
            .withEscapedText(text2, escapeText)
            .withWidth(text2Width)
            .withEmphasis(emphasize)
            .withStyleClass("minwidth")
            .make());

    row.addInnerTag(
        tableCell()
            .withEscapedText(text3)
            .withWidth(text3Width)
            .withAlignment(Alignment.inherit)
            .withStyleClass("minwidth" + text3Sytle)
            .make());

    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeEmptyRow()
   */
  @Override
  public void writeEmptyRow() {
    final Tag tableRow = tableRow().make();
    tableRow.addInnerTag(tableCell().withColumnSpan(3).make());
    out.println(tableRow.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /**
   * {@inheritDoc}
   *
   * @see
   *     schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper#writeNameRow(java.lang.String,
   *     java.lang.String)
   */
  @Override
  public void writeNameRow(final String name, final String description) {
    int nameWidth = 34;
    int descriptionWidth = 36;
    // Adjust widths
    if (name.length() > nameWidth && description.length() < descriptionWidth) {
      descriptionWidth =
          Math.max(description.length(), descriptionWidth - (name.length() - nameWidth));
    }
    if (description.length() > descriptionWidth && name.length() < nameWidth) {
      nameWidth = Math.max(name.length(), nameWidth - (description.length() - descriptionWidth));
    }

    final Tag row = tableRow().make();
    row.addInnerTag(
        tableCell()
            .withEscapedText(name)
            .withWidth(nameWidth)
            .withStyleClass("name")
            .withColumnSpan(2)
            .make());
    row.addInnerTag(
        tableCell()
            .withEscapedText(description)
            .withWidth(descriptionWidth)
            .withAlignment(Alignment.right)
            .withStyleClass("description right")
            .make());

    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeNameValueRow(java.lang.String, java.lang.String, Alignment)
   */
  @Override
  public void writeNameValueRow(
      final String name, final String value, final Alignment valueAlignment) {
    final int nameWidth = 40;
    final int valueWidth = 70 - nameWidth;

    final Alignment alignmentForValue = valueAlignment == null ? Alignment.inherit : valueAlignment;
    final String valueStyle =
        "property_value" + (alignmentForValue == Alignment.inherit ? "" : " right");

    final Tag row = tableRow().make();
    row.addInnerTag(
        tableCell()
            .withEscapedText(name)
            .withWidth(nameWidth)
            .withStyleClass("property_name")
            .make());
    row.addInnerTag(
        tableCell()
            .withEscapedText(value)
            .withWidth(valueWidth)
            .withAlignment(alignmentForValue)
            .withStyleClass(valueStyle)
            .make());

    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /**
   * Called to handle the row output.
   *
   * @param columnData Column data
   */
  @Override
  public void writeRow(final Object... columnData) {
    TextOutputFormat outputFormat = this.outputFormat;
    if (outputFormat == TextOutputFormat.text) {
      outputFormat = TextOutputFormat.tsv;
    }
    final Tag row = tableRow().make();
    for (final Object element : columnData) {
      final TagBuilder tableCell = tableCell().withEscapedText(toString(element));
      if (element == null) {
        tableCell.withStyleClass("data_null");
      } else if (element instanceof BinaryData) {
        tableCell.withStyleClass("data_binary");
      } else if (element instanceof Number) {
        tableCell.withStyleClass("data_number");
      }
      row.addInnerTag(tableCell.make());
    }

    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  /**
   * Called to handle the header output. Handler to be implemented by subclass.
   *
   * @param columnNames Column names
   */
  @Override
  public void writeRowHeader(final String... columnNames) {
    TextOutputFormat outputFormat = this.outputFormat;
    if (outputFormat == TextOutputFormat.text) {
      outputFormat = TextOutputFormat.tsv;
    }
    final Tag row = tableRow().make();
    for (final String columnName : columnNames) {
      final Tag headerCell = tableHeaderCell().withText(columnName).make();
      row.addInnerTag(headerCell);
    }

    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }

  @Override
  public void writeWideRow(final String definition, final String style) {
    final Tag row = tableRow().make();
    row.addInnerTag(
        tableCell().withEscapedText(definition).withStyleClass(style).withColumnSpan(3).make());
    out.println(row.render(TagOutputFormat.valueOf(outputFormat.name())));
  }
}
