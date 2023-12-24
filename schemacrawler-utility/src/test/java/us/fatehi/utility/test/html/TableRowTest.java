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
import static us.fatehi.utility.html.TagBuilder.tableCell;
import static us.fatehi.utility.html.TagBuilder.tableRow;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class TableRowTest {

  @Test
  public void emptyRow() {
    final Tag row = tableRow().make();

    assertThat(
        row.render(TagOutputFormat.html).replace(System.lineSeparator(), "~"),
        is("\t<tr>~\t</tr>"));
    assertThat(row.render(TagOutputFormat.text), is(""));
    assertThat(row.render(TagOutputFormat.tsv), is(""));

    assertThat(row.firstInnerTag(), is(nullValue()));
    assertThat(row.lastInnerTag(), is(nullValue()));
  }

  @Test
  public void endCells() {
    final Tag row = tableRow().make();
    final Tag cell1 = newTableCell();
    final Tag cell2 = newTableCell();
    final Tag cell3 = newTableCell();

    row.addInnerTag(cell1);
    row.addInnerTag(cell2);
    row.addInnerTag(cell3);

    assertThat(row.firstInnerTag(), is(cell1));
    assertThat(row.firstInnerTag(), is(not(cell2)));

    assertThat(row.lastInnerTag(), is(cell3));
    assertThat(row.lastInnerTag(), is(not(cell2)));
  }

  @Test
  public void tr() {
    final Tag row = tableRow().make();
    final Tag cell1 = newTableCell();

    row.addInnerTag(cell1);
    row.addInnerTag(cell1);

    assertThat(row.getTagName(), is("tr"));
    assertThat(row.toString(), is("tr"));

    assertThat(
        row.render(TagOutputFormat.html).replace(System.lineSeparator(), "~"),
        is("\t<tr>~\t\t<td>display text</td>~\t\t<td>display text</td>~\t</tr>"));
    assertThat(row.render(TagOutputFormat.text), is("display text  display text"));
    assertThat(row.render(TagOutputFormat.tsv), is("display text\tdisplay text"));
  }

  private Tag newTableCell() {
    return tableCell().withText("display text").make();
  }
}
