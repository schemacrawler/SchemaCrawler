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
import sf.util.Utility;

/**
 * Methods to format entire rows of output as HTML.
 * 
 * @author Sualeh Fatehi
 */
public final class HtmlFormattingHelper
  extends BaseTextFormattingHelper
{

  /**
   * HTML footer.
   */
  private static final String HTML_FOOTER = "</body>" + NEWLINE + "</html>";
  /**
   * HTML header.
   */
  private static final String HTML_HEADER = htmlHeader();

  private static String htmlHeader()
  {
    final String styleSheet = Utility
      .readResourceFully("/schemacrawler-output.css");

    return "<!DOCTYPE html>" + NEWLINE
           + "<html xmlns='http://www.w3.org/1999/xhtml'>" + NEWLINE + "<head>"
           + NEWLINE + "  <title>SchemaCrawler Output</title>" + NEWLINE
           + "  <meta charset=\"utf-8\"/>" + NEWLINE + "  <style>" + NEWLINE
           + styleSheet + NEWLINE + "  </style>" + NEWLINE + "</head>"
           + NEWLINE + "<body>" + NEWLINE;
  }

  public HtmlFormattingHelper(final OutputFormat outputFormat)
  {
    super(outputFormat);
  }

  @Override
  public String createArrow()
  {
    return " \u2192 ";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createDocumentEnd()
  {
    return HTML_FOOTER;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createDocumentStart()
  {
    return HTML_HEADER;
  }

  @Override
  public String createHeader(final DocumentHeaderType type, final String header)
  {
    if (!isBlank(header))
    {
      final String prefix;
      final String headerTag;
      if (type == null)
      {
        prefix = "<p>&#160;</p>";
        headerTag = "h2";
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = "<p>&#160;</p>";
            headerTag = "h1";
            break;
          case subTitle:
            prefix = "<p>&#160;</p>";
            headerTag = "h2";
            break;
          case section:
            prefix = "";
            headerTag = "h3";
            break;
          default:
            prefix = "<p>&#160;</p>";
            headerTag = "h2";
            break;
        }
      }
      return String.format("%s%n<%s>%s</%s>%n",
                           prefix,
                           headerTag,
                           header,
                           headerTag);
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
    return "</table>" + NEWLINE + "<p>&#160;</p>" + NEWLINE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createObjectStart(final String name)
  {
    String objectStart = "<table>" + NEWLINE;
    if (!isBlank(name))
    {
      objectStart = objectStart + "  <caption>" + name + "</caption>" + NEWLINE;
    }
    return objectStart;
  }

}
