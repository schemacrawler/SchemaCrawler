/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.loader.weakassociations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Column;
import schemacrawler.test.utility.crawl.LightTable;

public class ProposedWeakAssociationTest {

  @Test
  public void proposedWeakAssociation() {

    final LightTable table1 = new LightTable("Table1");
    final Column col1 = table1.addColumn("Id");
    final Column col2 = table1.addColumn("ColA");

    assertThrows(NullPointerException.class, () -> new ProposedWeakAssociation(null, col2));
    assertThrows(NullPointerException.class, () -> new ProposedWeakAssociation(col1, null));
    assertThrows(NullPointerException.class, () -> new ProposedWeakAssociation(null, null));

    final ProposedWeakAssociation proposedWeakAssociation = new ProposedWeakAssociation(col1, col2);
    assertThrows(
        UnsupportedOperationException.class, () -> proposedWeakAssociation.compareTo(null));
    assertThrows(
        UnsupportedOperationException.class, () -> proposedWeakAssociation.getKeySequence());

    assertThat(proposedWeakAssociation.getPrimaryKeyColumn(), is(col2));
    assertThat(proposedWeakAssociation.getForeignKeyColumn(), is(col1));
    assertThat(proposedWeakAssociation.toString(), is("Table1.Id ~~> Table1.ColA"));
    assertThat(proposedWeakAssociation.isValid(), is(false));

    assertThat(new ProposedWeakAssociation(col1, col1).isValid(), is(false));
  }
}
