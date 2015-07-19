/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.awt.Color;

import schemacrawler.tools.options.TextOutputFormat;

public final class TableHeaderCell
  extends TableCell
{

  public TableHeaderCell(final String text,
                         final int characterWidth,
                         final Alignment align,
                         final boolean emphasizeText,
                         final String styleClass,
                         final Color bgColor,
                         final int colSpan,
                         final TextOutputFormat outputFormat)
  {
    super(text,
          true,
          characterWidth,
          align,
          emphasizeText,
          styleClass,
          bgColor,
          colSpan,
          outputFormat);
  }

  @Override
  protected String getTag()
  {
    return "th";
  }

}
