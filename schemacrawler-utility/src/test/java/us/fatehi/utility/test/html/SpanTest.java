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
import static us.fatehi.utility.html.TagBuilder.span;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.fatehi.utility.Color;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class SpanTest {

  @DisplayName("span: basic output")
  @Test
  public void span1() {
    final Tag span =
        span()
            .withText("display text")
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    span.addAttribute("sometag", "customvalue");

    assertThat(span.getTagName(), is("span"));
    assertThat(span.toString(), is("span"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = span.render(TagOutputFormat.html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element spanElement = doc.select("span").first();

    assertThat(spanElement.attr("sometag"), is("customvalue"));
    assertThat(spanElement.attr("bgcolor"), is("#FF0064"));
    assertThat(spanElement.attr("class"), is("class"));
    assertThat(spanElement.text(), is("display text"));

    assertThat(span.render(TagOutputFormat.text), is("display text"));
    assertThat(span.render(TagOutputFormat.tsv), is("display text"));
  }
}
