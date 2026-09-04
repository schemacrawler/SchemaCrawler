package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.builder.NodeIdFactory;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TableType;
import schemacrawler.schema.View;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class NodeIdFactoryTest {

  @Test
  void identifiesSupportedCatalogObjectTypes() {
    final View view = mock(View.class);
    when(view.key()).thenReturn(new NamedObjectKey("PUBLIC", "ORDER_SUMMARY"));
    when(view.getTableType()).thenReturn(new TableType("VIEW"));
    final Procedure procedure = mock(Procedure.class);
    when(procedure.key()).thenReturn(new NamedObjectKey("PUBLIC", "REFRESH_ORDERS"));
    final Synonym synonym = mock(Synonym.class);
    when(synonym.key()).thenReturn(new NamedObjectKey("PUBLIC", "CUSTOMERS_ALIAS"));

    assertThat(NodeIdFactory.create(view).type(), is(SimpleDatabaseObjectType.view));
    assertThat(NodeIdFactory.create(procedure).type(), is(SimpleDatabaseObjectType.procedure));
    assertThat(NodeIdFactory.create(synonym).type(), is(SimpleDatabaseObjectType.synonym));
  }
}
