/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import us.fatehi.utility.Color;

/**
 * Represents an HTML anchor.
 *
 * @author Sualeh Fatehi
 */
class BaseTag
  implements Tag
{
  private final String tag;
  private final String styleClass;
  private final int characterWidth;
  private final Alignment align;
  private final String text;
  private final boolean escapeText;
  private final Color bgColor;
  private final boolean emphasizeText;
  private final Map<String, String> attributes;

  protected BaseTag(final String tag,
                    final String text,
                    final boolean escapeText,
                    final int characterWidth,
                    final Alignment align,
                    final boolean emphasizeText,
                    final String styleClass,
                    final Color bgColor)
  {
    this.tag = requireNonNull(tag);
    this.styleClass = styleClass;
    this.text = text == null? "": text;
    this.escapeText = escapeText;
    this.characterWidth = characterWidth;
    this.align = align;
    this.bgColor = bgColor;
    this.emphasizeText = emphasizeText;
    attributes = new HashMap<>();
  }

  protected BaseTag(final String tag,
                    final String text,
                    final boolean escapeText,
                    final int characterWidth,
                    final Alignment align,
                    final boolean emphasizeText,
                    final String styleClass,
                    final Color bgColor,
                    final Map<String, String> attributes)
  {
    this.tag = requireNonNull(tag);
    this.styleClass = styleClass;
    this.text = text == null? "": text;
    this.escapeText = escapeText;
    this.characterWidth = characterWidth;
    this.align = align;
    this.bgColor = bgColor;
    this.emphasizeText = emphasizeText;
    this.attributes = attributes;
  }

  @Override
  public String getTag()
  {
    return tag;
  }

  /**
   * Converts the table cell to HTML.
   *
   * @return HTML
   */
  public String render(final TagOutputFormat tagOutputFormat)
  {
    switch (tagOutputFormat)
    {
      case text:
        return toPlainTextString();
      case tsv:
        return toTsvString();
      case html:
      default:
        return toHtmlString();
    }
  }

  public String addAttribute(final String key, final String value)
  {
    return attributes.put(key, value);
  }

  @Override
  public String toString()
  {
    return getTag();
  }

  /**
   * Escapes the characters in text for use in HTML.
   *
   * @param text
   *   Text to escape.
   * @return HTML-escaped text
   */
  private String escapeHtml(final String text)
  {
    final StringBuilder buffer = new StringBuilder(text.length() * 2);
    for (int i = 0; i < text.length(); i++)
    {
      final char ch = text.charAt(i);
      switch (ch)
      {
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
  private String toHtmlString()
  {
    final StringBuilder buffer = new StringBuilder(1024);
    buffer
      .append("<")
      .append(getTag());
    for (final Entry<String, String> attribute : attributes.entrySet())
    {
      buffer
        .append(" ")
        .append(attribute.getKey())
        .append("='")
        .append(attribute.getValue())
        .append("'");
    }
    if (bgColor != null && !bgColor.equals(Color.white))
    {
      buffer
        .append(" bgcolor='")
        .append(bgColor)
        .append("'");
    }
    if (!isBlank(styleClass))
    {
      buffer
        .append(" class='")
        .append(styleClass)
        .append("'");
    }
    else if (align != null && align != Alignment.inherit)
    {
      buffer
        .append(" align='")
        .append(align)
        .append("'");
    }
    buffer.append(">");
    if (emphasizeText)
    {
      buffer.append("<b><i>");
    }
    buffer.append(escapeText? escapeHtml(text): text);
    if (emphasizeText)
    {
      buffer.append("</i></b>");
    }
    buffer
      .append("</")
      .append(getTag())
      .append(">");

    return buffer.toString();
  }

  /**
   * Converts the tag to TSV.
   *
   * @return Text
   */
  private String toTsvString()
  {
    return text;
  }

  /**
   * Converts the tag to text.
   *
   * @return Text
   */
  private String toPlainTextString()
  {
    if (characterWidth > 0)
    {
      final String format = String.format("%%%s%ds",
                                          align == Alignment.right? "": "-",
                                          characterWidth);
      return String.format(format, text);
    }
    else
    {
      return text;
    }
  }

}
