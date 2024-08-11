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
import static us.fatehi.utility.html.TagBuilder.caption;
import static us.fatehi.utility.html.TagBuilder.span;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class CaptionTest {

  @DisplayName("caption: basic output")
  @Test
  public void caption1() {
    final Tag caption =
        caption()
            .withText("display text")
            .withStyleClass("class")
            .withBackground(Color.fromRGB(255, 0, 100))
            .make();
    caption.addAttribute("sometag", "customvalue");
    caption.addInnerTag(span().withText("display text").make());

    assertThat(caption.getTagName(), is("caption"));
    assertThat(caption.toString(), is("caption"));

    // Use jsoup to ensure that the rendered HTML can be parsed, and check attributes without
    // relying on the order that they are generated
    final String renderedHtml = caption.render(TagOutputFormat.html);
    assertThat(renderedHtml, is(not(nullValue())));

    final Document doc = Jsoup.parseBodyFragment(String.format("<table>%s</table>", renderedHtml));
    final Element captionElement = doc.select("caption").first();

    assertThat(captionElement.attr("sometag"), is("customvalue"));
    assertThat(captionElement.attr("bgcolor"), is("#FF0064"));
    assertThat(captionElement.attr("class"), is("class"));
    assertThat(captionElement.text(), is("display text display text"));
    assertThat(captionElement.select("span").text(), is("display text"));

    assertThat(caption.render(TagOutputFormat.text), is("display text"));
    assertThat(caption.render(TagOutputFormat.tsv), is("display text"));
  }
}
