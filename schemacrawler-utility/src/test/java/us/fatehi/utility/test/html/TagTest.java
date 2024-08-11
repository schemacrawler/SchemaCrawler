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

package us.fatehi.utility.test.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static us.fatehi.utility.html.TagOutputFormat.html;
import static us.fatehi.utility.html.TagOutputFormat.text;
import static us.fatehi.utility.html.TagOutputFormat.tsv;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagBuilder;

public class TagTest {

  @DisplayName("toHtmlString: basic output")
  @Test
  public void toHtmlString_basic() {
    final Tag tag =
        TagBuilder.span()
            .withText("display text")
            .withWidth(2)
            .withAlignment(Alignment.right)
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    tag.addAttribute("sometag", "customvalue");
    tag.addAttribute(null, "nullvalue");
    tag.addAttribute("nulltag", null);
    tag.addAttribute("emptytag", "");

    assertThat(tag.render(text), is("display text"));
    assertThat(tag.render(tsv), is("display text"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = tag.render(html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element span = doc.select("span").first();

    assertThat(span.attr("sometag"), is("customvalue"));
    assertThat(span.attr("nulltag"), is(""));
    assertThat(span.attr("emptytag"), is(""));
    assertThat(span.attr("bgcolor"), is("#FF0064"));
    assertThat(span.attr("class"), is("class"));
    assertThat(span.text(), is("display text"));
  }

  @DisplayName("toHtmlString: escape text, emphasize, and allow free width")
  @Test
  public void toHtmlString_escapeEmphasize() {
    final Tag tag =
        TagBuilder.span()
            .withEscapedText("display & text")
            .withAlignment(Alignment.right)
            .withEmphasis()
            .make();
    tag.addAttribute("sometag", "custom&value");

    assertThat(tag.render(text), is("display & text"));
    assertThat(tag.render(tsv), is("display & text"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = tag.render(html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element span = doc.select("span").first();

    assertThat(span.attr("sometag"), is("custom&value"));
    assertThat(span.attr("align"), is("right"));
    assertThat(span.text(), is("display & text"));
    assertThat(span.select("b").first().outerHtml(), is("<b><i>display &amp; text</i></b>"));
  }

  @DisplayName("toHtmlString: inner tags")
  @Test
  public void toHtmlString_innerTags() {
    final Tag outerTag =
        TagBuilder.span()
            .withText("outer text")
            .withWidth(2)
            .withAlignment(Alignment.right)
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    outerTag.addAttribute("sometag", "customvalue");

    final Tag innerTag = TagBuilder.span().withText("inner text").make();
    outerTag.addInnerTag(innerTag);

    // Test adding a null inner tag
    outerTag.addInnerTag(null);

    assertThat(outerTag.render(text), is("inner text"));
    assertThat(outerTag.render(tsv), is("inner text"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = outerTag.render(html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element outerSpan = doc.select("span").first();
    final Element innerSpan = outerSpan.select("span").get(1);

    assertThat(outerSpan.attr("sometag"), is("customvalue"));
    assertThat(outerSpan.attr("bgcolor"), is("#FF0064"));
    assertThat(outerSpan.attr("class"), is("class"));
    assertThat(outerSpan.text(), is("outer text inner text"));

    assertThat(innerSpan.text(), is("inner text"));
  }
}
