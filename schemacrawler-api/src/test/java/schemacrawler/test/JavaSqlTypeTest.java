/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.JDBCType;
import java.sql.SQLType;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.schema.JavaSqlType;

public class JavaSqlTypeTest {

  @Test
  public void javaSqlType_equals() {
    EqualsVerifier.forClass(JavaSqlType.class)
        .withIgnoredFields("javaSqlTypeGroup", "defaultMappedClass")
        .withNonnullFields("sqlType")
        .withPrefabValues(SQLType.class, JDBCType.INTEGER, JDBCType.VARCHAR)
        .verify();
  }

  @Test
  public void unknown() {
    assertThat(JavaSqlType.UNKNOWN.getName(), is("UNKNOWN"));
    assertThat(JavaSqlType.UNKNOWN.getVendor(), is("us.fatehi.schemacrawler"));
    assertThat(JavaSqlType.UNKNOWN.getVendorTypeNumber(), is(Integer.MIN_VALUE));
  }
}
