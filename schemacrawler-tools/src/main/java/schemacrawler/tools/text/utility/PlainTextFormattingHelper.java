/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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

package schemacrawler.tools.text.utility;


import static sf.util.Utility.isBlank;
import schemacrawler.tools.options.TextOutputFormat;

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
  public PlainTextFormattingHelper(final TextOutputFormat outputFormat)
  {
    super(outputFormat);
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
        prefix = System.lineSeparator();
        separator = defaultSeparator;
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = System.lineSeparator();
            separator = separator("_");
            break;
          case subTitle:
            prefix = System.lineSeparator();
            separator = defaultSeparator;
            break;
          case section:
            prefix = "";
            separator = separator("-=-");
            break;
          default:
            prefix = System.lineSeparator();
            separator = defaultSeparator;
            break;
        }
      }
      return System.lineSeparator() + prefix + header + System.lineSeparator()
             + separator + System.lineSeparator() + prefix;
    }
    else
    {
      return "";
    }
  }

  @Override
  public String createLeftArrow()
  {
    return "<--";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectEnd()
  {
    return System.lineSeparator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectStart()
  {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectNameRow(String name, String description)
  {
    final String row = createNameRow(name, description)
                       + System.lineSeparator() + DASHED_SEPARATOR;
    return row;
  }

  @Override
  public String createRightArrow()
  {
    return "-->";
  }

  @Override
  public String createWeakLeftArrow()
  {
    return "<~~";
  }

  @Override
  public String createWeakRightArrow()
  {
    return "~~>";
  }

}
