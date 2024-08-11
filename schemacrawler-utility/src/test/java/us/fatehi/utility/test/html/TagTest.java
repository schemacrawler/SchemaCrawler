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

    final Element span = parseRenderedHtml(tag);

    assertThat(span.attributesSize(), is(5));
    assertThat(span.attr("sometag"), is("customvalue"));
    assertThat(span.attr("nulltag"), is(""));
    assertThat(span.attr("emptytag"), is(""));
    assertThat(span.attr("bgcolor"), is("#FF0064"));
    assertThat(span.attr("class"), is("class"));
    assertThat(span.text(), is("display text"));
  }

  @DisplayName("toHtmlString: escape text, emphasize, and allow free width left aligned")
  @Test
  public void toHtmlString_escapeEmphasize() {
    final Tag tag =
        TagBuilder.span()
            .withEscapedText("display & text")
            .withAlignment(Alignment.left)
            .withEmphasis()
            .make();
    tag.addAttribute("sometag", "custom&value");

    assertThat(tag.render(text), is("display & text"));
    assertThat(tag.render(tsv), is("display & text"));

    final Element span = parseRenderedHtml(tag);

    assertThat(span.attr("sometag"), is("custom&value"));
    assertThat(span.attr("align"), is("left"));
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

    final Element outerSpan = parseRenderedHtml(outerTag);
    final Element innerSpan = outerSpan.select("span").get(1);

    assertThat(outerSpan.attr("sometag"), is("customvalue"));
    assertThat(outerSpan.attr("bgcolor"), is("#FF0064"));
    assertThat(outerSpan.attr("class"), is("class"));
    assertThat(outerSpan.text(), is("outer text inner text"));

    assertThat(innerSpan.text(), is("inner text"));
  }

  @DisplayName("toHtmlString: bgcolor")
  @Test
  public void toHtmlString_bgcolor() {

    // Test with color

    final Tag tagBgColor =
        TagBuilder.span()
            .withText("text - color")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();

    assertThat(tagBgColor.render(text), is("text - color"));
    assertThat(tagBgColor.render(tsv), is("text - color"));

    final Element spanBgColor = parseRenderedHtml(tagBgColor);

    assertThat(spanBgColor.attributesSize(), is(1));
    assertThat(spanBgColor.attr("bgcolor"), is("#FF0064"));
    assertThat(spanBgColor.text(), is("text - color"));

    // Test with no color

    final Tag tagNoBg = TagBuilder.span().withText("text - no color").make();

    assertThat(tagNoBg.render(text), is("text - no color"));
    assertThat(tagNoBg.render(tsv), is("text - no color"));

    final Element spanNoBg = parseRenderedHtml(tagNoBg);

    assertThat(spanNoBg.attributesSize(), is(0));
    assertThat(spanNoBg.attr("bgcolor"), is(""));
    assertThat(spanNoBg.text(), is("text - no color"));

    // Test with white

    final Tag tagBgWhite =
        TagBuilder.span().withText("text - white").withBackground(Color.white).make();

    assertThat(tagBgWhite.render(text), is("text - white"));
    assertThat(tagBgWhite.render(tsv), is("text - white"));

    final Element spanBgWhite = parseRenderedHtml(tagBgWhite);

    assertThat(spanBgWhite.attributesSize(), is(0));
    assertThat(spanBgWhite.attr("bgcolor"), is(""));
    assertThat(spanBgWhite.text(), is("text - white"));
  }

  /**
   * Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without relying
   * on the order that they are generated
   *
   * @param tag Tag to render
   * @return Top level HTML element
   */
  private Element parseRenderedHtml(final Tag tag) {

    final String renderedHtml = tag.render(html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(renderedHtml);
    final Element span = doc.select(tag.getTagName()).first();
    return span;
  }
}
