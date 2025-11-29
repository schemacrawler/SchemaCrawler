/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.html;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static us.fatehi.utility.html.TagBuilder.tableCell;
import static us.fatehi.utility.html.TagBuilder.tableHeaderCell;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class TableCellTest {

  @DisplayName("td: basic output")
  @Test
  public void td1() {
    final Tag tablecell =
        tableCell()
            .withText("display text")
            .withWidth(2)
            .withAlignment(Alignment.right)
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    tablecell.addAttribute("sometag", "customvalue");

    assertThat(tablecell.getTagName(), is("td"));
    assertThat(tablecell.toString(), is("td"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = tablecell.render(TagOutputFormat.html);
    assertThat(renderedHtml, not(nullValue()));

    final Document doc =
        Jsoup.parseBodyFragment("<table><tr>%s</tr></table>".formatted(renderedHtml));
    final Element td = doc.select("td").first();

    assertThat(td.attr("sometag"), is("customvalue"));
    assertThat(td.attr("bgcolor"), is("#FF0064"));
    assertThat(td.attr("class"), is("class"));
    assertThat(td.text(), is("display text"));

    assertThat(tablecell.render(TagOutputFormat.text), is("display text"));
    assertThat(tablecell.render(TagOutputFormat.tsv), is("display text"));
  }

  @DisplayName("td: escape text, emphasize, and allow free width")
  @Test
  public void td2() {
    final Tag tablecell =
        tableCell()
            .withEscapedText("display & text")
            .withAlignment(Alignment.right)
            .withEmphasis()
            .withColumnSpan(2)
            .make();
    tablecell.addAttribute("sometag", "custom&value");

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = tablecell.render(TagOutputFormat.html);
    final Document doc =
        Jsoup.parseBodyFragment("<table><tr>%s</tr></table>".formatted(renderedHtml));
    final Element td = doc.select("td").first();

    assertThat(renderedHtml, not(nullValue()));
    assertThat(td.attr("sometag"), is("custom&value"));
    assertThat(td.attr("colspan"), is("2"));
    assertThat(td.attr("align"), is("right"));
    assertThat(td.text(), is("display & text"));
    assertThat(td.select("b").first().outerHtml(), is("<b><i>display &amp; text</i></b>"));

    assertThat(tablecell.render(TagOutputFormat.text), is("display & text"));
    assertThat(tablecell.render(TagOutputFormat.tsv), is("display & text"));
  }

  @DisplayName("th: basic output")
  @Test
  public void th1() {
    final Tag tableheader =
        tableHeaderCell()
            .withEscapedText("<escaped & text>")
            .withWidth(2)
            .withAlignment(Alignment.right)
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    tableheader.addAttribute("sometag", "customvalue");

    assertThat(tableheader.getTagName(), is("th"));
    assertThat(tableheader.toString(), is("th"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = tableheader.render(TagOutputFormat.html);
    assertThat(renderedHtml, not(nullValue()));

    final Document doc =
        Jsoup.parseBodyFragment("<table><tr>%s</tr></table>".formatted(renderedHtml));
    final Element th = doc.select("th").first();

    assertThat(th.attr("sometag"), is("customvalue"));
    assertThat(th.attr("bgcolor"), is("#FF0064"));
    assertThat(th.attr("class"), is("class"));
    assertThat(th.text(), is("<escaped & text>"));

    assertThat(renderedHtml, containsString("&lt;escaped &amp; text&gt;"));

    assertThat(tableheader.render(TagOutputFormat.text), is("<escaped & text>"));
    assertThat(tableheader.render(TagOutputFormat.tsv), is("<escaped & text>"));
  }
}
