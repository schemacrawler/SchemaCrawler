/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import schemacrawler.BaseProductVersion;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import sf.util.graph.DirectedEdge;
import sf.util.graph.Vertex;

public class EqualsTest
{

  @Test
  public void baseProductVersion()
  {
    EqualsVerifier.forClass(BaseProductVersion.class)
      .suppress(Warning.STRICT_INHERITANCE).verify();
  }

  @Test
  public void color()
  {
    EqualsVerifier.forClass(sf.util.Color.class).verify();
  }

  @Test
  public void columnReference()
  {
    EqualsVerifier.forClass(BaseColumnReference.class).verify();
  }

  @Test
  public void databaseServerType()
  {
    EqualsVerifier.forClass(DatabaseServerType.class)
      .withIgnoredFields("databaseSystemName").verify();
  }

  @Test
  public void directedEdge()
  {
    EqualsVerifier.forClass(DirectedEdge.class).verify();
  }

  @Test
  public void inclusionRules()
  {
    EqualsVerifier.forClass(IncludeAll.class).verify();
    EqualsVerifier.forClass(ExcludeAll.class).verify();
    EqualsVerifier.forClass(RegularExpressionInclusionRule.class).verify();
    EqualsVerifier.forClass(RegularExpressionExclusionRule.class).verify();
  }

  @Test
  public void namedObject()
  {
    EqualsVerifier.forClass(AbstractNamedObject.class)
      .suppress(Warning.STRICT_INHERITANCE).verify();
  }

  @Test
  public void namedObjectWithAttributes()
  {
    EqualsVerifier.forClass(AbstractNamedObjectWithAttributes.class)
      .withIgnoredFields("remarks", "attributeMap")
      .suppress(Warning.STRICT_INHERITANCE).verify();
  }

  @Test
  public void privilege()
  {
    final Table table1 = new MutableTable(new SchemaReference("catalog",
                                                              "schema"),
                                          "table1");
    final Table table2 = new MutableTable(new SchemaReference("catalog",
                                                              "schema"),
                                          "table2");

    EqualsVerifier.forClass(MutablePrivilege.class)
      .withIgnoredFields("remarks", "grants", "parent", "attributeMap")
      .withPrefabValues(DatabaseObjectReference.class,
                        new TableReference(table1),
                        new TableReference(table2))
      .suppress(Warning.STRICT_INHERITANCE).verify();
  }

  @Test
  public void schemaInfoLevel()
  {
    EqualsVerifier.forClass(SchemaInfoLevel.class).verify();
  }

  @Test
  public void schemaReference()
  {
    EqualsVerifier.forClass(SchemaReference.class)
      .withIgnoredFields("attributeMap").verify();
  }

  @Test
  public void tableType()
  {
    EqualsVerifier.forClass(TableType.class).verify();
  }

  @Test
  public void vertex()
  {
    EqualsVerifier.forClass(Vertex.class).withIgnoredFields("attributes")
      .verify();
  }

}
