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

import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Anchor;
import us.fatehi.utility.html.TagOutputFormat;

public class AnchorTest
{

  @Test
  public void anchor()
  {
    final Anchor anchor = new Anchor("display text",
                                     false,
                                     2,
                                     Alignment.right,
                                     false,
                                     "class",
                                     Color.fromRGB(255, 0, 100),
                                     "http://www.schemacrawler.com");
    anchor.addAttribute("sometag", "customvalue");

    assertThat(anchor.getTag(), is("a"));

    assertThat(anchor.render(TagOutputFormat.html),
               is(
                 "<a sometag='customvalue' href='http://www.schemacrawler.com' bgcolor='#FF0064' class='class'>display text</a>"));
    assertThat(anchor.render(TagOutputFormat.text), is("display text"));
    assertThat(anchor.render(TagOutputFormat.tsv), is("display text"));

  }

}
