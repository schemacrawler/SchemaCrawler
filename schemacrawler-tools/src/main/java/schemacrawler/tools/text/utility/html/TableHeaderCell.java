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


import schemacrawler.tools.options.TextOutputFormat;
import sf.util.Color;

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
