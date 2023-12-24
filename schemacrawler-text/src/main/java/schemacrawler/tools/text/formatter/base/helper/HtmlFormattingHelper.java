/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.formatter.base.helper;

import static us.fatehi.utility.IOUtility.readResourceFully;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.html.TagBuilder.span;

import java.io.PrintWriter;

import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagBuilder;
import us.fatehi.utility.html.TagOutputFormat;

/** Methods to format entire rows of output as HTML. */
public final class HtmlFormattingHelper extends BaseTextFormattingHelper {

  private static final String HTML_HEADER = htmlHeader();
  private static final String HTML_FOOTER = "</body>" + System.lineSeparator() + "</html>";

  private static String htmlHeader() {
    final StringBuilder styleSheet = new StringBuilder(4096);
    styleSheet
        .append(System.lineSeparator())
        .append(readResourceFully("/sc.css"))
        .append(System.lineSeparator())
        .append(readResourceFully("/sc_output.css"))
        .append(System.lineSeparator());

    final String htmlHeaderTemplate = readResourceFully("/html.header.txt");
    final String htmlHeader = String.format(htmlHeaderTemplate, styleSheet);
    return htmlHeader;
  }

  public HtmlFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
    super(out, outputFormat);
  }

  @Override
  public String createLeftArrow() {
    return "\u2190";
  }

  @Override
  public String createRightArrow() {
    return "\u2192";
  }

  @Override
  public String createWeakLeftArrow() {
    return "\u21dc";
  }

  @Override
  public String createWeakRightArrow() {
    return "\u21dd";
  }

  /** {@inheritDoc} */
  @Override
  public void writeDocumentEnd() {
    out.println(HTML_FOOTER);
  }

  /** {@inheritDoc} */
  @Override
  public void writeDocumentStart() {
    out.println(HTML_HEADER);
  }

  @Override
  public void writeHeader(final DocumentHeaderType type, final String header) {
    if (!isBlank(header) && type != null) {
      out.println(
          String.format(
              "%s%n<%s>%s</%s>%n",
              type.getPrefix(), type.getHeaderTag(), header, type.getHeaderTag()));
    }
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectEnd() {
    out.append("</table>").println();
    out.println("<p>&#160;</p>");
    out.println();
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectNameRow(
      final String id, final String name, final String description, final Color backgroundColor) {

    final Tag caption =
        TagBuilder.caption()
            .withStyle(String.format("background-color: %s;", backgroundColor))
            .make();

    if (!isBlank(name)) {
      final Tag span = span().withEscapedText(name).withStyleClass("caption_name").make();
      if (!isBlank(id)) {
        span.addAttribute("id", id);
      }
      caption.addInnerTag(span);
    }
    if (!isBlank(description)) {
      final Tag span =
          span().withEscapedText(description).withStyleClass("caption_description").make();
      caption.addInnerTag(span);
    }

    final StringBuilder buffer = new StringBuilder(1024);
    buffer.append(caption.render(TagOutputFormat.html)).append(System.lineSeparator());

    out.println(buffer.toString());
  }

  /** {@inheritDoc} */
  @Override
  public void writeObjectStart() {
    out.println("<table>");
  }
}
