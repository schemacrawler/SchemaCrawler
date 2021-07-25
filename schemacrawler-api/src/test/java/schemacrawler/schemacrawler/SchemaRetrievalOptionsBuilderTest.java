package schemacrawler.schemacrawler;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.sap.db.jdbcext.wrapper.Connection;
import com.sap.db.jdbcext.wrapper.DatabaseMetaData;

public class SchemaRetrievalOptionsBuilderTest {

  @Test
  public void dbMetaData() throws SQLException {

    final DatabaseMetaData dbMetaData = mock(DatabaseMetaData.class);
    when(dbMetaData.supportsCatalogsInTableDefinitions()).thenReturn(false);
    when(dbMetaData.supportsSchemasInTableDefinitions()).thenReturn(true);

    final Connection connection = mock(Connection.class);
    when(connection.getMetaData()).thenReturn(dbMetaData);

    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());
    builder.fromConnnection(connection);
    assertThat(builder.supportsCatalogs, is(false));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isPresent());
  }

  @Test
  public void dbMetaData_none() throws SQLException {

    SchemaRetrievalOptionsBuilder builder;

    builder = SchemaRetrievalOptionsBuilder.builder();
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());
    builder.fromConnnection(null);
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());

    final Connection connection = mock(Connection.class);
    when(connection.getMetaData()).thenThrow(SQLException.class);

    builder = SchemaRetrievalOptionsBuilder.builder();
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());
    builder.fromConnnection(connection);
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isPresent());
  }

  @Test
  public void override_catalog_schema() {
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();

    assertThat(builder.overridesSupportsCatalogs, isEmpty());
    builder.withSupportsCatalogs();
    assertThat(builder.overridesSupportsCatalogs, isPresentAndIs(true));
    builder.withoutSupportsCatalogs();
    assertThat(builder.overridesSupportsCatalogs, isEmpty());
    builder.withDoesNotSupportCatalogs();
    assertThat(builder.overridesSupportsCatalogs, isPresentAndIs(false));

    assertThat(builder.overridesSupportsSchemas, isEmpty());
    builder.withSupportsSchemas();
    assertThat(builder.overridesSupportsSchemas, isPresentAndIs(true));
    builder.withoutSupportsSchemas();
    assertThat(builder.overridesSupportsSchemas, isEmpty());
    builder.withDoesNotSupportSchemas();
    assertThat(builder.overridesSupportsSchemas, isPresentAndIs(false));
  }
}
