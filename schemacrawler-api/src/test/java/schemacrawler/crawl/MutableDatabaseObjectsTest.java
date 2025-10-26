/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;

public class MutableDatabaseObjectsTest {

  @Test
  public void testMutableFunctionNoName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MutableFunction(new SchemaReference(), null, null));
  }

  @Test
  public void testMutableProcedureNoName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MutableProcedure(new SchemaReference(), null, null));
  }

  @Test
  public void testMutableSequenceNoName() {
    assertThrows(
        IllegalArgumentException.class, () -> new MutableSequence(new SchemaReference(), null));
  }

  @Test
  public void testMutableSynonymNoName() {
    assertThrows(
        IllegalArgumentException.class, () -> new MutableSynonym(new SchemaReference(), null));
  }

  @Test
  public void testMutableSynonymWithoutReference() {
    final Synonym synonym = new MutableSynonym(new SchemaReference(), "SYNONYM");

    assertThat(synonym.getReferencedObject(), is(nullValue()));
    assertThat(synonym.getReferencedObject(), is(nullValue()));
    assertThat(synonym.getReferencedObjects(), contains(nullValue()));
  }

  @Test
  public void testMutableSynonymWithReference() {
    final MutableSynonym synonym = new MutableSynonym(new SchemaReference(), "SYNONYM");
    final Table table = new MutableTable(new SchemaReference(), "REFERENCED_OBJECT");
    synonym.setReferencedObject(table);
    assertThat(synonym.getReferencedObject(), is(table));
    assertThat(synonym.getReferencedObjects(), contains(table));
  }

  @Test
  public void testMutableTableNoName() {
    assertThrows(
        IllegalArgumentException.class, () -> new MutableTable(new SchemaReference(), null));
  }

  @Test
  public void testMutableViewNoName() {
    assertThrows(
        IllegalArgumentException.class, () -> new MutableView(new SchemaReference(), null));
  }
}
