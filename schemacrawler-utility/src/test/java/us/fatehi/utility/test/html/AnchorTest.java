/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static us.fatehi.utility.html.TagBuilder.anchor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class AnchorTest {

  @DisplayName("anchor: basic output")
  @Test
  public void anchor1() {
    final Tag anchor =
        anchor()
            .withText("display text")
            .withWidth(2)
            .withAlignment(Alignment.right)
            .withStyleClass("class")
            .withStyle("somestyle")
            .withBackground(Color.fromRGB(255, 0, 100))
            .withHyperlink("http://www.schemacrawler.com")
            .make();
    anchor.addAttribute("sometag", "customvalue");

    assertThat(anchor.getTagName(), is("a"));
    assertThat(anchor.toString(), is("a"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = anchor.render(TagOutputFormat.html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element link = doc.select("a").first();

    assertThat(link.attr("href"), is("http://www.schemacrawler.com"));
    assertThat(link.attr("style"), is("somestyle"));
    assertThat(link.attr("sometag"), is("customvalue"));
    assertThat(link.attr("bgcolor"), is("#FF0064"));
    assertThat(link.attr("class"), is("class"));
    assertThat(link.text(), is("display text"));

    assertThat(anchor.render(TagOutputFormat.text), is("display text"));
    assertThat(anchor.render(TagOutputFormat.tsv), is("display text"));
  }

  @DisplayName("anchor: escape text, emphasize, and allow free width")
  @Test
  public void anchor2() {
    final Tag anchor =
        anchor()
            .withEscapedText("display & text")
            .withAlignment(Alignment.right)
            .withEmphasis()
            .withHyperlink("http://www.schemacrawler.com")
            .make();
    anchor.addAttribute("sometag", "custom&value");

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = anchor.render(TagOutputFormat.html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element link = doc.select("a").first();

    assertThat(link.attr("href"), is("http://www.schemacrawler.com"));
    assertThat(link.attr("align"), is("right"));
    assertThat(link.attr("sometag"), is("custom&value"));
    assertThat(link.text(), is("display & text"));
    assertThat(link.select("b").first().outerHtml(), is("<b><i>display &amp; text</i></b>"));

    assertThat(anchor.render(TagOutputFormat.text), is("display & text"));
    assertThat(anchor.render(TagOutputFormat.tsv), is("display & text"));
  }

  @DisplayName("anchor: empty href")
  @Test
  public void anchor3() {
    final Tag anchor =
        anchor()
            .withEscapedText("display & text", true)
            .withEmphasis(false)
            .withHyperlink(" ")
            .make();
    anchor.addAttribute(null, "value");

    assertThat(anchor.render(TagOutputFormat.html), is("<a>display &amp; text</a>"));
    assertThat(anchor.render(TagOutputFormat.text), is("display & text"));
    assertThat(anchor.render(TagOutputFormat.tsv), is("display & text"));
  }
}
