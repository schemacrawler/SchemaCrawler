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

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;

import java.sql.JDBCType;
import java.sql.SQLType;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.JavaSqlTypeGroup;

public class JavaSqlTypeTest {

  @Test
  public void bad() {
    final JavaSqlType javaSqlType =
        new JavaSqlType(
            new SQLType() {

              @Override
              public String getName() {
                return null;
              }

              @Override
              public String getVendor() {
                return null;
              }

              @Override
              public Integer getVendorTypeNumber() {
                return null;
              }
            },
            Object.class,
            JavaSqlTypeGroup.unknown);

    assertThat(javaSqlType.getName(), is(nullValue()));
    assertThat(javaSqlType.getVendor(), is(nullValue()));
    assertThat(javaSqlType.getVendorTypeNumber(), is(Integer.MIN_VALUE));
    assertThat(javaSqlType.getDefaultMappedClass(), is(Object.class));
    assertThat(javaSqlType.getJavaSqlTypeGroup(), is(JavaSqlTypeGroup.unknown));
    assertThat(javaSqlType.toString(), is("null\tnull\tunknown"));
  }

  @Test
  public void javaSqlType_compareTo() {

    final JavaSqlType javaSqlType =
        new JavaSqlType(JDBCType.INTEGER, Integer.class, JavaSqlTypeGroup.integer);

    assertThat(javaSqlType.compareTo(null), is(lessThan(0)));
    assertThat(JavaSqlType.UNKNOWN.compareTo(null), is(lessThan(0)));

    assertThat(javaSqlType.compareTo(javaSqlType), is(0));
    assertThat(JavaSqlType.UNKNOWN.compareTo(JavaSqlType.UNKNOWN), is(0));

    assertThat(javaSqlType.compareTo(JavaSqlType.UNKNOWN), is(lessThan(0)));
    assertThat(JavaSqlType.UNKNOWN.compareTo(javaSqlType), is(greaterThan(0)));
  }

  @Test
  public void javaSqlType_equals() {
    EqualsVerifier.forClass(JavaSqlType.class)
        .withIgnoredFields("javaSqlTypeGroup", "defaultMappedClass")
        .withNonnullFields("sqlType")
        .withPrefabValues(SQLType.class, JDBCType.INTEGER, JDBCType.VARCHAR)
        .verify();
  }

  @Test
  public void known() {
    final JavaSqlType javaSqlType =
        new JavaSqlType(JDBCType.INTEGER, Integer.class, JavaSqlTypeGroup.integer);

    assertThat(javaSqlType.getName(), is("INTEGER"));
    assertThat(javaSqlType.getVendor(), is("java.sql"));
    assertThat(javaSqlType.getVendorTypeNumber(), is(JDBCType.INTEGER.getVendorTypeNumber()));
    assertThat(javaSqlType.getDefaultMappedClass(), is(Integer.class));
    assertThat(javaSqlType.getJavaSqlTypeGroup(), is(JavaSqlTypeGroup.integer));
    assertThat(javaSqlType.toString(), is("INTEGER\t4\tinteger"));
  }

  @Test
  public void unknown() {
    final JavaSqlType javaSqlType = JavaSqlType.UNKNOWN;

    assertThat(javaSqlType.getName(), is("UNKNOWN"));
    assertThat(javaSqlType.getVendor(), is("us.fatehi.schemacrawler"));
    assertThat(javaSqlType.getVendorTypeNumber(), is(Integer.MIN_VALUE));
    assertThat(javaSqlType.getDefaultMappedClass(), is(Object.class));
    assertThat(javaSqlType.getJavaSqlTypeGroup(), is(JavaSqlTypeGroup.unknown));
    assertThat(javaSqlType.toString(), is("UNKNOWN\t-2147483648\tunknown"));
  }
}
