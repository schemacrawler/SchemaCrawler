/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.Serializable;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Property;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;

public class EqualsTest
{

  @Test
  public void namedObjectEquals()
  {
    final class TestNamedObject
      extends AbstractNamedObject
    {
      static final long serialVersionUID = 1L;

      private TestNamedObject(final String name)
      {
        super(name);
      }
    }

    final NamedObject testObject1 = new TestNamedObject("test");
    final NamedObject testObject2 = new TestNamedObject("test");
    final NamedObject testObject3 = new TestNamedObject("test 2");

    final EqualsTester equalsTester = new EqualsTester()
      .addEqualityGroup(testObject1, testObject2).addEqualityGroup(testObject3);
    equalsTester.testEquals();
  }

  @Test
  public void namedObjectWithAttributesEquals()
  {
    final class TestNamedObject
      extends AbstractNamedObjectWithAttributes
    {
      static final long serialVersionUID = 1L;

      private TestNamedObject(final String name)
      {
        super(name);
      }
    }

    final NamedObject testObject1 = new TestNamedObject("test");
    final NamedObject testObject2 = new TestNamedObject("test");
    final NamedObject testObject3 = new TestNamedObject("test 2");

    final EqualsTester equalsTester = new EqualsTester()
      .addEqualityGroup(testObject1, testObject2).addEqualityGroup(testObject3);
    equalsTester.testEquals();
  }

  @Test
  public void privilegeEquals()
  {
    final Table table1 = new MutableTable(new SchemaReference("catalog",
                                                              "schema"),
                                          "table1");
    final Table table2 = new MutableTable(new SchemaReference("catalog",
                                                              "schema"),
                                          "table2");

    final Privilege<Table> testPrivilege1 = new MutablePrivilege<>(new TableReference(table1),
                                                                   "privilegeA");
    final Privilege<Table> testPrivilege2 = new MutablePrivilege<>(new TableReference(table1),
                                                                   "privilegeA");
    final Privilege<Table> testPrivilege3 = new MutablePrivilege<>(new TableReference(table1),
                                                                   "privilegeB");
    final Privilege<Table> testPrivilege4 = new MutablePrivilege<>(new TableReference(table2),
                                                                   "privilegeB");

    final EqualsTester equalsTester = new EqualsTester()
      .addEqualityGroup(testPrivilege1, testPrivilege2)
      .addEqualityGroup(testPrivilege3).addEqualityGroup(testPrivilege4);
    equalsTester.testEquals();
  }

  @Test
  public void propertyEquals()
  {
    final class TestProperty
      extends AbstractProperty
    {
      static final long serialVersionUID = 1L;

      private TestProperty(final String name, final Serializable value)
      {
        super(name, value);
      }

      @Override
      public int compareTo(final Property o)
      {
        return 0;
      }

      @Override
      public String getDescription()
      {
        return getName();
      }
    }

    final Property testObject1 = new TestProperty("test", "value");
    final Property testObject2 = new TestProperty("test", "value");
    final Property testObject3 = new TestProperty("test 2", "value 2");

    final EqualsTester equalsTester = new EqualsTester()
      .addEqualityGroup(testObject1, testObject2).addEqualityGroup(testObject3);
    equalsTester.testEquals();
  }

}
