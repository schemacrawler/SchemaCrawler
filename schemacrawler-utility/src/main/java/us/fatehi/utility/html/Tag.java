/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.html;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.html.TagOutputFormat.html;
import static us.fatehi.utility.html.TagOutputFormat.tsv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    if (!isBlank(key) && !isBlank(value)) {
      return attributes.put(key, value);
    } else {
      return value;
    }
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
    for (final Entry<String, String> attribute : attributes.entrySet()) {
      buffer
          .append(" ")
          .append(attribute.getKey())
          .append("='")
          .append(attribute.getValue())
          .append("'");
    }
    if (bgColor != null && !bgColor.equals(Color.white)) {
      buffer.append(" bgcolor='").append(bgColor).append("'");
    }
    if (!isBlank(styleClass)) {
      buffer.append(" class='").append(styleClass).append("'");
    } else if (align != null && align != Alignment.inherit) {
      buffer.append(" align='").append(align).append("'");
    }
    buffer.append(">");
    if (emphasizeText) {
      buffer.append("<b><i>");
    }

    if (innerTags.isEmpty()) {
      if (indent) {
        buffer.append(System.lineSeparator());
      }
      buffer.append(escapeText ? escapeHtml(text) : text);
    } else {
      buffer.append(System.lineSeparator());
      for (final Tag innerTag : innerTags) {
        if (indent) {
          buffer.append("\t");
        }
        buffer.append("\t").append(innerTag.render(html)).append(System.lineSeparator());
      }
    }

    if (emphasizeText) {
      buffer.append("</i></b>");
    }
    if (indent) {
      buffer.append("\t");
    }
    buffer.append("</").append(getTagName()).append(">");

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
    if (innerTags.isEmpty()) {
      if (characterWidth > 0) {
        final String format =
            String.format("%%%s%ds", align == Alignment.right ? "" : "-", characterWidth);
        return String.format(format, text);
      } else {
        return text;
      }
    } else {
      return toInnerTagsPlainTextString();
    }
  }

  /**
   * Converts the tag to TSV.
   *
   * @return Text
   */
  private String toTsvString() {
    if (innerTags.isEmpty()) {
      return text;
    } else {
      return toInnerTagsTsvString();
    }
  }
}
