/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static us.fatehi.utility.html.TagBuilder.anchor;

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

    assertThat(
        anchor.render(TagOutputFormat.html),
        is(
            "<a sometag='customvalue' style='somestyle' href='http://www.schemacrawler.com' bgcolor='#FF0064' class='class'>display text</a>"));
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

    assertThat(
        anchor.render(TagOutputFormat.html),
        is(
            "<a sometag='custom&value' href='http://www.schemacrawler.com' align='right'><b><i>display &amp; text</i></b></a>"));
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
