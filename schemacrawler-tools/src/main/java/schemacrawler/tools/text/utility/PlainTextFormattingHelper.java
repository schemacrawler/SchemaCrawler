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
import sf.util.Utility;

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

  @Override
  public String createArrow()
  {
    return " --> ";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createDocumentEnd()
  {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createDocumentStart()
  {
    return "";
  }

  @Override
  public String createHeader(final DocumentHeaderType type, final String header)
  {
    if (!sf.util.Utility.isBlank(header))
    {
      final String defaultSeparator = separator("=");

      final String prefix;
      final String separator;
      if (type == null)
      {
        prefix = Utility.NEWLINE;
        separator = defaultSeparator;
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = Utility.NEWLINE;
            separator = separator("_");
            break;
          case subTitle:
            prefix = Utility.NEWLINE;
            separator = defaultSeparator;
            break;
          case section:
            prefix = "";
            separator = separator("-=-");
            break;
          default:
            prefix = Utility.NEWLINE;
            separator = defaultSeparator;
            break;
        }
      }
      return Utility.NEWLINE + prefix + header + Utility.NEWLINE + separator
             + Utility.NEWLINE + prefix;
    }
    else
    {
      return "";
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectEnd()
  {
    return Utility.NEWLINE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectStart(final String name)
  {
    String objectStart = "";
    if (!sf.util.Utility.isBlank(name))
    {
      objectStart = objectStart + Utility.NEWLINE + name + Utility.NEWLINE
                    + DASHED_SEPARATOR;
    }
    return objectStart;
  }

}
