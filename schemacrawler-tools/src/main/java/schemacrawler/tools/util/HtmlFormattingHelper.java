/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.util;


import schemacrawler.tools.OutputFormat;
import sf.util.Utilities;

/**
 * Methods to format entire rows of output as HTML.
 * 
 * @author Sualeh Fatehi
 */
public final class HtmlFormattingHelper
  extends BaseTextFormattingHelper
{

  public HtmlFormattingHelper(OutputFormat outputFormat)
  {
    super(outputFormat);
  }

  public String createArrow()
  {
    return " &rarr; ";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDocumentEnd()
   */
  public String createDocumentEnd()
  {
    return FormatUtils.HTML_FOOTER;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDocumentStart()
   */
  public String createDocumentStart()
  {
    return FormatUtils.HTML_HEADER;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createObjectEnd()
   */
  public String createObjectEnd()
  {
    return "</table>" + Utilities.NEWLINE + "<p></p>" + Utilities.NEWLINE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createObjectStart(java.lang.String)
   */
  public String createObjectStart(final String name)
  {
    String objectStart = "<table>" + Utilities.NEWLINE;
    if (!Utilities.isBlank(name))
    {
      objectStart = objectStart + "  <caption>" + name + "</caption>"
                    + Utilities.NEWLINE;
    }
    return objectStart;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createPreformattedText(java.lang.String,
   *      java.lang.String)
   */
  public String createPreformattedText(final String id, final String text)
  {
    return String.format("<pre id=\'%s\'>%n%s</pre>", id, text);
  }

}
