package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class DatabaseObjectNodeIdTest {

  @Test
  void distinguishesTypesWithTheSameKey() {
    final NamedObjectKey key = new NamedObjectKey("PUBLIC", "ORDERS");
    final DatabaseObjectNodeId table =
        new DatabaseObjectNodeId(key, SimpleDatabaseObjectType.table);
    final DatabaseObjectNodeId view = new DatabaseObjectNodeId(key, SimpleDatabaseObjectType.view);

    assertThat(table, is(not(view)));
    final DatabaseObjectNodeId equivalent =
        new DatabaseObjectNodeId(key, SimpleDatabaseObjectType.table);
    assertThat(table, is(equivalent));
    assertThat(table.hashCode(), is(equivalent.hashCode()));
    assertThat(table, comparesEqualTo(equivalent));
  }
}
