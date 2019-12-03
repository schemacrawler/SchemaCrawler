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

package schemacrawler.crawl;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.TableType;

public class CompareToTest
{

  /**
   * See: <a href=
   * "https://github.com/schemacrawler/SchemaCrawler/issues/228">Inconsistent
   * table comparison</a>
   */
  @Test
  public void compareTables()
  {
    final MutableTable tbl = new MutableTable(new SchemaReference(null,
                                                                  "public"),
                                              "booking_detail");
    tbl.setTableType(new TableType("table"));

    final MutableView view = new MutableView(new SchemaReference(null,
                                                                 "public"),
                                             "blog_monthly_stat_fa");
    view.setTableType(new TableType("materialized view"));

    assertThat(view, lessThan(null));
    assertThat(tbl, lessThan(null));

    assertThat(tbl, comparesEqualTo(tbl));
    assertThat(view, comparesEqualTo(view));

    assertThat(tbl, lessThan(view));
    assertThat(view, greaterThan(tbl));
  }

}
