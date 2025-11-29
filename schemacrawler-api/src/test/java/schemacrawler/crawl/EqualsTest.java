/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import schemacrawler.crawl.MutablePrivilege.PrivilegeGrant;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.graph.Vertex;
import us.fatehi.utility.property.AbstractProperty;

public class EqualsTest {

  private final Table table1 = new MutableTable(new SchemaReference("catalog", "schema"), "table1");
  private final Table table2 = new MutableTable(new SchemaReference("catalog", "schema"), "table2");

  @Test
  public void columnReference() {
    EqualsVerifier.forClass(ImmutableColumnReference.class)
        .withIgnoredFields("keySequence")
        .verify();
  }

  @Test
  public void databaseObject() {
    class TestDatabaseObject extends AbstractDatabaseObject {

      /** */
      private static final long serialVersionUID = 6661972079180914099L;

      TestDatabaseObject(final Schema schema, final String name) {
        super(schema, name);
      }
    }

    EqualsVerifier.forClass(TestDatabaseObject.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .withIgnoredFields("key", "attributeMap")
        .verify();
  }

  @Test
  public void grants() {

    final MutablePrivilege<Table> privilege1 =
        new MutablePrivilege<>(new TablePointer(table1), "privilege1");
    final MutablePrivilege<Table> privilege2 =
        new MutablePrivilege<>(new TablePointer(table2), "privilege2");

    EqualsVerifier.forClass(PrivilegeGrant.class)
        .withPrefabValues(MutablePrivilege.class, privilege1, privilege2)
        .verify();
  }

  @Test
  public void inclusionRules() {
    EqualsVerifier.forClass(IncludeAll.class).verify();
    EqualsVerifier.forClass(ExcludeAll.class).verify();
  }

  @Test
  public void namedObject() {
    EqualsVerifier.forClass(AbstractNamedObject.class)
        .withIgnoredFields("key")
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  public void namedObjectKey() {
    EqualsVerifier.forClass(NamedObjectKey.class).withNonnullFields("key").verify();
  }

  @Test
  public void namedObjectWithAttributes() {
    EqualsVerifier.forClass(AbstractNamedObjectWithAttributes.class)
        .withIgnoredFields("key", "attributeMap")
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  public void privilege() {

    EqualsVerifier.forClass(MutablePrivilege.class)
        .withIgnoredFields("key", "grants", "attributeMap")
        .withPrefabValues(
            DatabaseObjectReference.class, new TablePointer(table1), new TablePointer(table2))
        .suppress(Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  public void property() {
    EqualsVerifier.forClass(AbstractProperty.class).verify();
  }

  @Test
  public void schemaInfoLevel() {
    EqualsVerifier.forClass(SchemaInfoLevel.class).verify();
  }

  @Test
  public void schemaReference() {
    EqualsVerifier.forClass(SchemaReference.class)
        .withIgnoredFields("key", "attributeMap")
        .verify();
  }

  @Test
  public void tableType() {
    EqualsVerifier.forClass(TableType.class).verify();
  }

  @Test
  public void vertex() {
    EqualsVerifier.forClass(Vertex.class).withIgnoredFields("attributes").verify();
  }
}
