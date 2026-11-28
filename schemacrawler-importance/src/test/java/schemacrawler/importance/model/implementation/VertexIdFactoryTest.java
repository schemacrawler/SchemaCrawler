package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.VertexUtility;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TableType;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightProcedure;
import schemacrawler.test.utility.crawl.LightSynonym;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class VertexIdFactoryTest {

  @Test
  void identifiesSupportedCatalogObjectTypes() {
    final View view = mock(View.class);
    when(view.key()).thenReturn(new NamedObjectKey("PUBLIC", "ORDER_SUMMARY"));
    when(view.getTableType()).thenReturn(new TableType("VIEW"));
    final SchemaReference schema = new SchemaReference(null, "PUBLIC");
    final Procedure procedure = new LightProcedure(schema, "REFRESH_ORDERS");
    final Synonym synonym = new LightSynonym(schema, "CUSTOMERS_ALIAS");

    assertThat(VertexUtility.createVertexId(view).type(), is(SimpleDatabaseObjectType.view));
    assertThat(
        VertexUtility.createVertexId(procedure).type(), is(SimpleDatabaseObjectType.procedure));
    assertThat(VertexUtility.createVertexId(synonym).type(), is(SimpleDatabaseObjectType.synonym));
  }
}
