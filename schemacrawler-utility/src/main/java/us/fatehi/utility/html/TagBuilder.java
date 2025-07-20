/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.html;

import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import us.fatehi.utility.Color;

/** Builds an HTML tag. */
public final class TagBuilder {

  public static TagBuilder anchor() {
    return new TagBuilder("a");
  }

  public static TagBuilder caption() {
    return new TagBuilder("caption").withIndent(true);
  }

  public static TagBuilder span() {
    return new TagBuilder("span");
  }

  public static TagBuilder tableCell() {
    return new TagBuilder("td");
  }

  public static TagBuilder tableHeaderCell() {
    return new TagBuilder("th");
  }

  public static TagBuilder tableRow() {
    return new TagBuilder("tr").withIndent(true);
  }

  private final String tag;
  private final Map<String, String> attributes;
  private String styleClass;
  private int characterWidth;
  private Alignment align;
  private String text;
  private boolean escapeText;
  private Color bgColor;
  private boolean emphasizeText;
  private boolean indent;

  private TagBuilder(final String tag) {
    this.tag = tag;
    this.attributes = new HashMap<>();
  }

  public Tag make() {
    return new Tag(
        this.tag,
        text,
        escapeText,
        characterWidth,
        align,
        emphasizeText,
        styleClass,
        bgColor,
        indent,
        attributes);
  }

  public TagBuilder withAlignment(final Alignment align) {
    this.align = align;
    return this;
  }

  public TagBuilder withBackground(final Color bgColor) {
    this.bgColor = bgColor;
    return this;
  }

  public TagBuilder withColumnSpan(final int columnSpan) {
    attributes.put("colspan", String.valueOf(columnSpan));
    return this;
  }

  public TagBuilder withEmphasis() {
    this.emphasizeText = true;
    return this;
  }

  public TagBuilder withEmphasis(final boolean emphasizeText) {
    this.emphasizeText = emphasizeText;
    return this;
  }

  public TagBuilder withEscapedText(final String text) {
    this.text = text;
    this.escapeText = true;
    return this;
  }

  public TagBuilder withEscapedText(final String text, final boolean escapeText) {
    this.text = text;
    this.escapeText = escapeText;
    return this;
  }

  public TagBuilder withHyperlink(final String href) {
    if (isBlank(href)) {
      return this;
    }
    attributes.put("href", href);
    return this;
  }

  public TagBuilder withIndent(final boolean indent) {
    this.indent = indent;
    return this;
  }

  public TagBuilder withStyle(final String style) {
    if (style != null) {
      attributes.put("style", style);
    }
    return this;
  }

  public TagBuilder withStyleClass(final String styleClass) {
    this.styleClass = styleClass;
    return this;
  }

  public TagBuilder withText(final String text) {
    this.text = text;
    return this;
  }

  public TagBuilder withWidth(final int characterWidth) {
    this.characterWidth = characterWidth;
    return this;
  }
}
