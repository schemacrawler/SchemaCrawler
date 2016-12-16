/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.schema.SchemaReference;

public class SchemaEqualsHashCodeTest
{

  @Test
  public void equalsContract1()
  {
    EqualsVerifier.forClass(SchemaReference.class).verify();
  }

  @Ignore
  @Test
  public void equalsContract2()
  {
    EqualsVerifier.forClass(MutableCatalog.class).verify();
    EqualsVerifier.forClass(MutableTable.class).verify();
    EqualsVerifier.forClass(MutablePrimaryKey.class).verify();
    EqualsVerifier.forClass(MutableColumn.class).verify();
    EqualsVerifier.forClass(MutableForeignKey.class).verify();
    EqualsVerifier.forClass(MutableForeignKeyColumnReference.class).verify();
    EqualsVerifier.forClass(MutablePrivilege.class).verify();
  }

}
