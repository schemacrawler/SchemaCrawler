/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.util;


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
  private static final String HTML_FOOTER = "</body>" + Utility.NEWLINE + "</html>";
  /**
   * HTML header.
   */
  private static final String HTML_HEADER = htmlHeader();

  private static String htmlHeader()
  {
    final String styleSheet = Utility.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/schemacrawler-output.css"));

    return ""
      + "<?xml version='1.0' encoding='UTF-8'?>"
      + Utility.NEWLINE
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
      + Utility.NEWLINE
      + "<html xmlns='http://www.w3.org/1999/xhtml'>"
      + Utility.NEWLINE + "<head>" + Utility.NEWLINE
      + "  <title>SchemaCrawler Output</title>" + Utility.NEWLINE
      + "  <style type='text/css'>" + Utility.NEWLINE + styleSheet
      + Utility.NEWLINE + "  </style>" + Utility.NEWLINE + "</head>"
      + Utility.NEWLINE + "<body>" + Utility.NEWLINE;
  }

  public HtmlFormattingHelper(final OutputFormat outputFormat)
  {
    super(outputFormat);
  }

  public String createArrow()
  {
    return " \u2192 ";
  }

  /**
   * {@inheritDoc}
   */
  public String createDocumentEnd()
  {
    return HTML_FOOTER;
  }

  /**
   * {@inheritDoc}
   */
  public String createDocumentStart()
  {
    return HTML_HEADER;
  }

  public String createHeader(final DocumentHeaderType type, final String header)
  {
    if (!sf.util
      .Utility
      .isBlank(header))
    {
      final String prefix;
      final String headerTag;
      if (type == null)
      {
        prefix = "<p>&nbsp;</p>";
        headerTag = "h2";
      }
      else
      {
        switch (type)
        {
          case title:
            prefix = "<p>&nbsp;</p>";
            headerTag = "h1";
            break;
          case subTitle:
            prefix = "<p>&nbsp;</p>";
            headerTag = "h2";
            break;
          case section:
            prefix = "";
            headerTag = "h3";
            break;
          default:
            prefix = "<p>&nbsp;</p>";
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
  public String createObjectEnd()
  {
    return "</table>" + Utility.NEWLINE + "<p></p>" + Utility.NEWLINE;
  }

  /**
   * {@inheritDoc}
   */
  public String createObjectStart(final String name)
  {
    String objectStart = "<table>" + Utility.NEWLINE;
    if (!sf.util
      .Utility
      .isBlank(name))
    {
      objectStart = objectStart + "  <caption>" + name + "</caption>" + Utility.NEWLINE;
    }
    return objectStart;
  }

  /**
   * {@inheritDoc}
   */
  public String createPreformattedText(final String id, final String text)
  {
    return String.format("<pre id=\'%s\'>%n%s</pre>", id, text);
  }
}
