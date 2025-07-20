/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.html;

import static us.fatehi.utility.html.TagOutputFormat.html;
import static us.fatehi.utility.html.TagOutputFormat.tsv;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import us.fatehi.utility.Color;

public class Tag {

  private final String tagName;
  private final String styleClass;
  private final int characterWidth;
  private final Alignment align;
  private final String text;
  private final boolean escapeText;
  private final Color bgColor;
  private final boolean emphasizeText;
  private final List<Tag> innerTags;
  private final Map<String, String> attributes;
  private final boolean indent;

  protected Tag(
      final String tagName,
      final String text,
      final boolean escapeText,
      final int characterWidth,
      final Alignment align,
      final boolean emphasizeText,
      final String styleClass,
      final Color bgColor,
      final boolean indent,
      final Map<String, String> attributes) {
    this.tagName = requireNonNull(tagName);
    this.styleClass = styleClass;
    this.text = text == null ? "" : text;
    this.escapeText = escapeText;
    this.characterWidth = characterWidth;
    this.align = align;
    this.bgColor = bgColor;
    this.emphasizeText = emphasizeText;
    this.indent = indent;
    innerTags = new ArrayList<>();
    this.attributes = attributes;
  }

  public String addAttribute(final String key, final String value) {
    if (!isBlank(key)) {
      return attributes.put(key, value);
    }
    return value;
  }

  public Tag addInnerTag(final Tag tag) {
    if (tag != null) {
      innerTags.add(tag);
    }
    return this;
  }

  public Tag firstInnerTag() {
    if (innerTags.isEmpty()) {
      return null;
    }
    return innerTags.get(0);
  }

  public String getTagName() {
    return tagName;
  }

  public Tag lastInnerTag() {
    if (innerTags.isEmpty()) {
      return null;
    }
    return innerTags.get(innerTags.size() - 1);
  }

  /**
   * Converts the table cell to HTML.
   *
   * @return HTML
   */
  public String render(final TagOutputFormat tagOutputFormat) {
    switch (tagOutputFormat) {
      case text:
        return toPlainTextString();
      case tsv:
        return toTsvString();
      case html:
      default:
        return toHtmlString();
    }
  }

  @Override
  public String toString() {
    return getTagName();
  }

  private void appendAttributes(final StringBuilder buffer) {
    for (final Entry<String, String> attribute : attributes.entrySet()) {
      final String value = attribute.getValue();
      buffer.append(" ").append(attribute.getKey());
      if (value != null) {
        buffer.append("='").append(value).append("'");
      }
    }
  }

  private void appendBgColor(final StringBuilder buffer) {
    if (bgColor != null && !bgColor.equals(Color.white)) {
      buffer.append(" bgcolor='").append(bgColor).append("'");
    }
  }

  private void appendClosingTag(final StringBuilder buffer) {
    if (emphasizeText) {
      buffer.append("</i></b>");
    }
    if (indent) {
      buffer.append("\t");
    }
    buffer.append("</").append(getTagName()).append(">");
  }

  private void appendEmphasizedText(final StringBuilder buffer) {
    if (emphasizeText) {
      buffer.append("<b><i>");
    }
  }

  private void appendInnerTags(final StringBuilder buffer) {
    for (final Tag innerTag : innerTags) {
      if (indent) {
        buffer.append("\t");
      }
      buffer.append("\t").append(innerTag.render(html)).append(System.lineSeparator());
    }
  }

  private void appendStyleClass(final StringBuilder buffer) {
    if (!isBlank(styleClass)) {
      buffer.append(" class='").append(styleClass).append("'");
    } else if (align != null && align != Alignment.inherit) {
      buffer.append(" align='").append(align).append("'");
    }
  }

  /**
   * Escapes the characters in text for use in HTML.
   *
   * @param text Text to escape.
   * @return HTML-escaped text
   */
  private String escapeHtml(final String text) {
    final StringBuilder buffer = new StringBuilder(text.length() * 2);
    for (int i = 0; i < text.length(); i++) {
      final char ch = text.charAt(i);
      switch (ch) {
        case 62:
          buffer.append("&gt;");
          break;
        case 38:
          buffer.append("&amp;");
          break;
        case 60:
          buffer.append("&lt;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  /**
   * Converts the tag to HTML.
   *
   * @return HTML
   */
  private String toHtmlString() {
    final StringBuilder buffer = new StringBuilder(1024);
    if (indent) {
      buffer.append("\t");
    }
    buffer.append("<").append(getTagName());
    appendAttributes(buffer);
    appendBgColor(buffer);
    appendStyleClass(buffer);
    buffer.append(">");
    appendEmphasizedText(buffer);

    if (indent) {
      buffer.append(System.lineSeparator());
    }
    buffer.append(escapeText ? escapeHtml(text) : text);

    if (!innerTags.isEmpty()) {
      appendInnerTags(buffer);
    }

    appendClosingTag(buffer);

    return buffer.toString();
  }

  private String toInnerTagsPlainTextString() {
    return toInnerTagsPlainTextString(TagOutputFormat.text, "  ");
  }

  /**
   * Converts the table row to text.
   *
   * @return Text
   */
  private String toInnerTagsPlainTextString(
      final TagOutputFormat tagOutputFormat, final String fieldSeparator) {
    final StringBuilder buffer = new StringBuilder(1024);

    for (int i = 0; i < innerTags.size(); i++) {
      final Tag cell = innerTags.get(i);
      if (i > 0) {
        buffer.append(fieldSeparator);
      }
      buffer.append(cell.render(tagOutputFormat));
    }

    return buffer.toString();
  }

  private String toInnerTagsTsvString() {
    return toInnerTagsPlainTextString(tsv, "\t");
  }

  /**
   * Converts the tag to text.
   *
   * @return Text
   */
  private String toPlainTextString() {
    if (!innerTags.isEmpty()) {
      return toInnerTagsPlainTextString();
    }
    if (characterWidth > 0) {
      final String format =
          String.format("%%%s%ds", align == Alignment.right ? "" : "-", characterWidth);
      return String.format(format, text);
    }
    return text;
  }

  /**
   * Converts the tag to TSV.
   *
   * @return Text
   */
  private String toTsvString() {
    if (innerTags.isEmpty()) {
      return text;
    }
    return toInnerTagsTsvString();
  }
}
