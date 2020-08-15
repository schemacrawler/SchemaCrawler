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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static us.fatehi.utility.html.TagBuilder.tableCell;

import org.junit.jupiter.api.Test;
import us.fatehi.utility.html.TableRow;
import us.fatehi.utility.html.Tag;
import us.fatehi.utility.html.TagOutputFormat;

public class TableRowTest
{

  @Test
  public void emptyRow()
  {
    final TableRow row = new TableRow();

    assertThat(row
                 .render(TagOutputFormat.html)
                 .replace(System.lineSeparator(), "~"), is("\t<tr>~\t</tr>"));
    assertThat(row.render(TagOutputFormat.text), is(""));
    assertThat(row.render(TagOutputFormat.tsv), is(""));

    assertThat(row.firstCell(), is(nullValue()));
    assertThat(row.lastCell(), is(nullValue()));
  }

  @Test
  public void tr()
  {
    final TableRow row = new TableRow();
    final Tag cell1 = newTableCell();

    row.add(cell1);
    row.add(cell1);

    assertThat(row.getTag(), is("tr"));
    assertThat(row.toString(), is("tr"));

    assertThat(row
                 .render(TagOutputFormat.html)
                 .replace(System.lineSeparator(), "~"),
               is(
                 "\t<tr>~\t\t<td>display text</td>~\t\t<td>display text</td>~\t</tr>"));
    assertThat(row.render(TagOutputFormat.text),
               is("display text  display text"));
    assertThat(row.render(TagOutputFormat.tsv),
               is("display text\tdisplay text"));
  }

  @Test
  public void endCells()
  {
    final TableRow row = new TableRow();
    final Tag cell1 = newTableCell();
    final Tag cell2 = newTableCell();
    final Tag cell3 = newTableCell();

    row.add(cell1);
    row.add(cell2);
    row.add(cell3);

    assertThat(row.firstCell(), is(cell1));
    assertThat(row.firstCell(), is(not(cell2)));

    assertThat(row.lastCell(), is(cell3));
    assertThat(row.lastCell(), is(not(cell2)));
  }

  private Tag newTableCell()
  {
    return tableCell()
      .withText("display text")
      .make();
  }

}
