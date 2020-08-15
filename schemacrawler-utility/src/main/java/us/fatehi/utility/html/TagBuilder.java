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


import java.util.HashMap;
import java.util.Map;

import us.fatehi.utility.Color;

/**
 * Represents an HTML anchor.
 *
 * @author Sualeh Fatehi
 */
public final class TagBuilder
{

  public static TagBuilder anchor()
  {
    return new TagBuilder("a");
  }

  public static TagBuilder span()
  {
    return new TagBuilder("span");
  }

  public static TagBuilder tableHeaderCell()
  {
    return new TagBuilder("th");
  }

  public static TagBuilder tableCell()
  {
    return new TagBuilder("td");
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

  private TagBuilder(final String tag)
  {
    this.tag = tag;
    this.attributes = new HashMap<>();
  }

  public Tag make()
  {
    return new BaseTag(this.tag,
                       text,
                       escapeText,
                       characterWidth,
                       align,
                       emphasizeText,
                       styleClass,
                       bgColor,
                       attributes);
  }

  public TagBuilder withAttribute(final String key, final String value)
  {
    attributes.put(key, value);
    return this;
  }

  public TagBuilder withStyle(final String styleClass)
  {
    this.styleClass = styleClass;
    return this;
  }

  public TagBuilder withWidth(final int characterWidth)
  {
    this.characterWidth = characterWidth;
    return this;
  }

  public TagBuilder withAlignment(final Alignment align)
  {
    this.align = align;
    return this;
  }

  public TagBuilder withText(final String text)
  {
    this.text = text;
    return this;
  }

  public TagBuilder withEscapedText(final String text)
  {
    this.text = text;
    this.escapeText = true;
    return this;
  }

  public TagBuilder withBackground(final Color bgColor)
  {
    this.bgColor = bgColor;
    return this;
  }

  public TagBuilder withEmphasis()
  {
    this.emphasizeText = true;
    return this;
  }

}
