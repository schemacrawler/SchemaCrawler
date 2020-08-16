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
import static us.fatehi.utility.html.TagBuilder.caption;
import static us.fatehi.utility.html.TagBuilder.span;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class CaptionTest
{

  @DisplayName("caption: basic output")
  @Test
  public void caption1()
  {
    final Tag caption = caption()
      .withText("display text")
      .withStyleClass("class")
      .withBackground(Color.fromRGB(255, 0, 100))
      .make();
    caption.addAttribute("sometag", "customvalue");
    caption.addInnerTag(span().withText("display text").make());

    assertThat(caption.getTagName(), is("caption"));
    assertThat(caption.toString(), is("caption"));

    assertThat(caption.render(TagOutputFormat.html),
               is(
                 "\t<caption sometag='customvalue' bgcolor='#FF0064' class='class'>\r\n\t\t<span>display text</span>\r\n\t</caption>"));
    assertThat(caption.render(TagOutputFormat.text), is("display text"));
    assertThat(caption.render(TagOutputFormat.tsv), is("display text"));

  }

}
