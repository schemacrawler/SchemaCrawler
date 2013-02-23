/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.tools.text.utility;


import schemacrawler.tools.options.OutputFormat;

class TableHeaderCell
  extends TableCell
{

  TableHeaderCell(final String text,
                  final int characterWidth,
                  final Alignment align,
                  final int colSpan,
                  final String styleClass,
                  final OutputFormat outputFormat)
  {
    super(text, characterWidth, align, colSpan, styleClass, outputFormat);
  }

  @Override
  protected String getCellTag()
  {
    return "th";
  }

}
