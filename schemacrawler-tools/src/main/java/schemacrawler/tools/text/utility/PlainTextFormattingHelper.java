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


import static sf.util.Utility.NEWLINE;
import static sf.util.Utility.isBlank;
import schemacrawler.tools.options.OutputFormat;

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
    if (!isBlank(header))
    {
      final String defaultSeparator = separator("=");

      final String prefix;
      final String separator;
      if (type == null)
      {
        prefix = NEWLINE;
        separator = defaultSeparator;
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = NEWLINE;
            separator = separator("_");
            break;
          case subTitle:
            prefix = NEWLINE;
            separator = defaultSeparator;
            break;
          case section:
            prefix = "";
            separator = separator("-=-");
            break;
          default:
            prefix = NEWLINE;
            separator = defaultSeparator;
            break;
        }
      }
      return NEWLINE + prefix + header + NEWLINE + separator + NEWLINE + prefix;
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
    return NEWLINE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectStart(final String name)
  {
    final String objectStart;
    if (!isBlank(name))
    {
      objectStart = NEWLINE + name + NEWLINE + DASHED_SEPARATOR;
    }
    else
    {
      objectStart = "";
    }
    return objectStart;
  }

}
