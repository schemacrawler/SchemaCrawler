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
import us.fatehi.utility.html.TableCell;
import us.fatehi.utility.html.TagOutputFormat;

public class TableCellTest
{

  @DisplayName("tablecell: basic output")
  @Test
  public void tablecell1()
  {
    final TableCell tablecell = new TableCell("display text",
                                     false,
                                     2,
                                     Alignment.right,
                                     false,
                                     "class",
                                     Color.fromRGB(255, 0, 100), 1);
    tablecell.addAttribute("sometag", "customvalue");

    assertThat(tablecell.getTag(), is("td"));
    assertThat(tablecell.toString(), is("td"));

    assertThat(tablecell.render(TagOutputFormat.html),
               is(
                 "<td sometag='customvalue' bgcolor='#FF0064' class='class'>display text</td>"));
    assertThat(tablecell.render(TagOutputFormat.text), is("display text"));
    assertThat(tablecell.render(TagOutputFormat.tsv), is("display text"));

  }

  @DisplayName("tablecell: escape text, emphasize, and allow free width")
  @Test
  public void tablecell2()
  {
    final TableCell tablecell = new TableCell("display & text",
                                     true,
                                     -1,
                                     null,
                                     true,
                                     "class",
                                     null,
                                     2);
    tablecell.addAttribute("sometag", "custom&value");

    assertThat(tablecell.render(TagOutputFormat.html),
               is(
                 "<td colspan='2' sometag='custom&value' class='class'><b><i>display &amp; text</i></b></td>"));
    assertThat(tablecell.render(TagOutputFormat.text), is("display & text"));
    assertThat(tablecell.render(TagOutputFormat.tsv), is("display & text"));

  }

}
