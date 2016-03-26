/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.text.utility.html;


import static schemacrawler.tools.text.utility.html.Entities.escapeForXMLElement;
import static sf.util.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import schemacrawler.tools.options.TextOutputFormat;
import sf.util.Color;

/**
 * Represents an HTML anchor.
 *
 * @author Sualeh Fatehi
 */
abstract class BaseTag
{

  /**
   * Enclose the value in quotes and escape the quote and comma
   * characters that are inside.
   *
   * @param text
   *        Text that needs to be escaped and quoted
   * @return Text, escaped and quoted.
   */
  private static String escapeAndQuoteCsv(final String text)
  {
    final char QUOTE = '\"';
    final char SEPARATOR = ',';

    final String value = String.valueOf(text);
    final int length = value.length();
    if (length == 0)
    {
      return "\"\"";
    }

    if (value.indexOf(SEPARATOR) < 0 && value.indexOf(QUOTE) < 0)
    {
      return value;
    }

    final StringBuilder sb = new StringBuilder(length);
    sb.append(QUOTE);
    for (int i = 0; i < length; i++)
    {
      final char c = value.charAt(i);
      if (c == QUOTE)
      {
        sb.append(QUOTE).append(c);
      }
      else
      {
        sb.append(c);
      }
    }
    sb.append(QUOTE);

    return sb.toString();
  }

  private final TextOutputFormat outputFormat;
  private final String styleClass;
  private final int characterWidth;
  private final Alignment align;
  private final String text;
  private final boolean escapeText;
  private final Color bgColor;
  private final boolean emphasizeText;

  private final Map<String, String> attributes;

  protected BaseTag(final String text,
                    final boolean escapeText,
                    final int characterWidth,
                    final Alignment align,
                    final boolean emphasizeText,
                    final String styleClass,
                    final Color bgColor,
                    final TextOutputFormat outputFormat)
  {
    this.outputFormat = outputFormat;
    this.styleClass = styleClass;
    this.text = text == null? "NULL": text;
    this.escapeText = escapeText;
    this.characterWidth = characterWidth;
    this.align = align;
    this.bgColor = bgColor;
    this.emphasizeText = emphasizeText;
    attributes = new HashMap<>();
  }

  public String addAttribute(final String key, final String value)
  {
    return attributes.put(key, value);
  }

  /**
   * Converts the table cell to HTML.
   *
   * @return HTML
   */
  @Override
  public String toString()
  {
    if (outputFormat == TextOutputFormat.html)
    {
      return toHtmlString();
    }
    else
    {
      return toPlainTextString();
    }
  }

  protected abstract String getTag();

  /**
   * Converts the tag to HTML.
   *
   * @return HTML
   */
  private String toHtmlString()
  {
    final StringBuilder buffer = new StringBuilder(1024);
    buffer.append("<").append(getTag());
    for (final Entry<String, String> attribute: attributes.entrySet())
    {
      buffer.append(" ").append(attribute.getKey()).append("='")
        .append(attribute.getValue()).append("'");
    }
    if (bgColor != null && !bgColor.equals(Color.white))
    {
      buffer.append(" bgcolor='").append(bgColor).append("'");
    }
    if (!isBlank(styleClass))
    {
      buffer.append(" class='").append(styleClass).append("'");
    }
    else if (align != null && align != Alignment.inherit)
    {
      buffer.append(" align='").append(align).append("'");
    }
    buffer.append(">");
    if (emphasizeText)
    {
      buffer.append("<b><i>");
    }
    buffer.append(escapeText? escapeForXMLElement(text): text);
    if (emphasizeText)
    {
      buffer.append("</i></b>");
    }
    buffer.append("</").append(getTag()).append(">");

    return buffer.toString();
  }

  /**
   * Converts the tag to text.
   *
   * @return Text
   */
  private String toPlainTextString()
  {
    if (outputFormat == TextOutputFormat.csv)
    {
      return escapeText? escapeAndQuoteCsv(text): text;
    }
    else if (outputFormat == TextOutputFormat.tsv)
    {
      return text;
    }
    else
    {
      if (characterWidth > 0)
      {
        final String format = String
          .format("%%%s%ds", align == Alignment.right? "": "-", characterWidth);
        return String.format(format, text);
      }
      else
      {
        return text;
      }
    }
  }

}
