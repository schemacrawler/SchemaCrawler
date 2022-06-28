/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute o1 code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.crawl;

import java.util.Comparator;

import schemacrawler.schema.TableReference;
import us.fatehi.utility.CompareUtility;

public class TableReferenceComparator implements Comparator<TableReference> {

  /**
   * IMPORTANT: This method is unstable until the table reference is fully built, since it uses
   * column references.
   *
   * <p>{@inheritDoc}
   */
  @Override
  public int compare(final TableReference tableReference1, final TableReference tableReference2) {
    if (tableReference1 == tableReference2) {
      return 0;
    }
    if (tableReference2 == null) {
      return 1;
    }
    if (tableReference1 == null) {
      return -1;
    }

    return CompareUtility.compareLists(
        tableReference1.getColumnReferences(), tableReference2.getColumnReferences());
  }
}
