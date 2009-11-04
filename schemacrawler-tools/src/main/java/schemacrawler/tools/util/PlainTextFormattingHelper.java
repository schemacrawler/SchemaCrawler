/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

/**
 * Methods to format entire rows of output as text.
 * 
 * @author Sualeh Fatehi
 */
public class PlainTextFormattingHelper
  extends BaseTextFormattingHelper
{

  /**
   * Constructor.
   * 
   * @param outputFormat
   *        Output format - text or CSV.
   */
  public PlainTextFormattingHelper(final OutputFormat outputFormat)
  {
    super(outputFormat);
  }

  public String createArrow()
  {
    return " --> ";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDocumentEnd()
   */
  public String createDocumentEnd()
  {
    return "";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDocumentStart()
   */
  public String createDocumentStart()
  {
    return "";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createObjectEnd()
   */
  public String createObjectEnd()
  {
    return NEWLINE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createObjectStart(java.lang.String)
   */
  public String createObjectStart(final String name)
  {
    String objectStart = "";
    if (!schemacrawler.utility.Utility.isBlank(name))
    {
      objectStart = objectStart + NEWLINE + name + NEWLINE + DASHED_SEPARATOR;
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
    return NEWLINE + text;
  }

  public String createSectionHeader(final SectionHeaderType type,
                                    final String sectionHeader)
  {
    if (!schemacrawler.utility.Utility.isBlank(sectionHeader))
    {
      final String DOUBLE_DASHED_SEPARATOR = separator("-=-");

      final String prefix;
      final String separator;
      if (type == null)
      {
        prefix = NEWLINE;
        separator = DOUBLE_DASHED_SEPARATOR;
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = NEWLINE;
            separator = separator("=");
            break;
          case heading1:
            prefix = NEWLINE;
            separator = DOUBLE_DASHED_SEPARATOR;
            break;
          case heading2:
            prefix = "";
            separator = separator("--=-");
            break;
          default:
            prefix = NEWLINE;
            separator = DOUBLE_DASHED_SEPARATOR;
            break;
        }
      }
      return NEWLINE + NEWLINE + prefix + sectionHeader + NEWLINE + separator
             + NEWLINE + NEWLINE;
    }
    else
    {
      return "";
    }
  }

}
