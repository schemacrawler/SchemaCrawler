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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.TableHeaderCell;
import us.fatehi.utility.html.TagOutputFormat;

public class TableHeaderCellTest
{

  @DisplayName("th: basic output")
  @Test
  public void th1()
  {
    final TableHeaderCell th = new TableHeaderCell("display text",
                                     2,
                                     Alignment.right,
                                     false,
                                     "class",
                                     Color.fromRGB(255, 0, 100), 1);
    th.addAttribute("sometag", "customvalue");

    assertThat(th.getTag(), is("th"));
    assertThat(th.toString(), is("th"));

    assertThat(th.render(TagOutputFormat.html),
               is(
                 "<th sometag='customvalue' bgcolor='#FF0064' class='class'>display text</th>"));
    assertThat(th.render(TagOutputFormat.text), is("display text"));
    assertThat(th.render(TagOutputFormat.tsv), is("display text"));

  }

  @DisplayName("th: escape text, emphasize, and allow free width")
  @Test
  public void th2()
  {
    final TableHeaderCell th = new TableHeaderCell("display & text",
                                     -1,
                                     null,
                                     true,
                                     "class",
                                     null,
                                     2);
    th.addAttribute("sometag", "custom&value");

    assertThat(th.render(TagOutputFormat.html),
               is(
                 "<th colspan='2' sometag='custom&value' class='class'><b><i>display &amp; text</i></b></th>"));
    assertThat(th.render(TagOutputFormat.text), is("display & text"));
    assertThat(th.render(TagOutputFormat.tsv), is("display & text"));

  }

}
