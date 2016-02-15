/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.text.utility.html;


import static sf.util.Utility.isBlank;

import schemacrawler.tools.options.TextOutputFormat;
import sf.util.Color;

/**
 * Represents an HTML link.
 *
 * @author Sualeh Fatehi
 */
public class Anchor
  extends BaseTag
{

  public Anchor(final String text,
                final boolean escapeText,
                final int characterWidth,
                final Alignment align,
                final boolean emphasizeText,
                final String styleClass,
                final Color bgColor,
                final String link,
                final TextOutputFormat outputFormat)
  {
    super(text,
          escapeText,
          characterWidth,
          align,
          emphasizeText,
          styleClass,
          bgColor,
          outputFormat);
    if (!isBlank(link))
    {
      addAttribute("href", link);
    }
  }

  @Override
  protected String getTag()
  {
    return "a";
  }

}
