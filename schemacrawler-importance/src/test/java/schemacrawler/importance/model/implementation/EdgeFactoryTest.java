/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Synonym;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;

class EdgeFactoryTest {

  @Test
  void addsSynonymResolutionEdgeForMockedCatalogObjects() {
    final Synonym synonym = mock(Synonym.class);
    when(synonym.key()).thenReturn(new NamedObjectKey("PUBLIC", "ORDER_ALIAS"));
    final DatabaseObject orders = new LightTable(new SchemaReference(null, "PUBLIC"), "ORDERS");
    when(synonym.hasReferencedObject()).thenReturn(true);
    when(synonym.getReferencedObject()).thenReturn(null);

    final SchemaGraphAssembly assembly = new SchemaGraphAssembly();
    assembly.addNode(synonym);
    assembly.addNode(orders);

    EdgeFactory.addSynonymEdges(assembly, synonym);

    assertThat(assembly.catalogGraph().edgeSet(), hasSize(0));
  }
}
