package schemacrawler.schemacrawler;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.sap.db.jdbcext.wrapper.Connection;
import com.sap.db.jdbcext.wrapper.DatabaseMetaData;

import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes;

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
  public void dbServerType() {
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();

    assertThat(builder.dbServerType, is(DatabaseServerType.UNKNOWN));

    builder.withDatabaseServerType(new DatabaseServerType("newdb", "New Database"));
    assertThat(builder.dbServerType.getDatabaseSystemIdentifier(), is("newdb"));

    builder.withDatabaseServerType(null);
    assertThat(builder.dbServerType, is(DatabaseServerType.UNKNOWN));
  }

  @Test
  public void enumDataTypeHelper() {
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();

    assertThat(
        builder.enumDataTypeHelper.getEnumDataTypeInfo(null, null, null).getType(),
        is(EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated));

    builder.withEnumDataTypeHelper(
        (column, columnDataType, connection) ->
            new EnumDataTypeInfo(EnumDataTypeTypes.enumerated_column, emptyList()));
    assertThat(
        builder.enumDataTypeHelper.getEnumDataTypeInfo(null, null, null).getType(),
        is(EnumDataTypeInfo.EnumDataTypeTypes.enumerated_column));

    builder.withEnumDataTypeHelper(null);
    assertThat(
        builder.enumDataTypeHelper.getEnumDataTypeInfo(null, null, null).getType(),
        is(EnumDataTypeInfo.EnumDataTypeTypes.not_enumerated));
  }

  @Test
  public void fromOptions() {
    final SchemaRetrievalOptions options =
        SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder(options);
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());
  }

  @Test
  public void fromOptions_null() {
    final SchemaRetrievalOptionsBuilder builder =
        SchemaRetrievalOptionsBuilder.builder().fromOptions(null);
    assertThat(builder.supportsCatalogs, is(true));
    assertThat(builder.supportsSchemas, is(true));
    assertThat(builder.overridesTypeMap, isEmpty());
  }

  @Test
  public void identifierQuoteString() {
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();

    assertThat(builder.identifierQuoteString, is(""));

    builder.withIdentifierQuoteString("@");
    assertThat(builder.identifierQuoteString, is("@"));

    builder.withIdentifierQuoteString(null);
    assertThat(builder.identifierQuoteString, is(""));

    builder.withIdentifierQuoteString("\t");
    assertThat(builder.identifierQuoteString, is(""));
  }

  @Test
  public void metadataRetrievalStrategy() {
    final SchemaRetrievalOptionsBuilder builder = SchemaRetrievalOptionsBuilder.builder();

    MetadataRetrievalStrategy metadataRetrievalStrategy;

    metadataRetrievalStrategy =
        builder.get(SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy);
    assertThat(metadataRetrievalStrategy, is(MetadataRetrievalStrategy.metadata));

    builder.with(
        SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy,
        MetadataRetrievalStrategy.data_dictionary_all);
    metadataRetrievalStrategy =
        builder.get(SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy);
    assertThat(metadataRetrievalStrategy, is(MetadataRetrievalStrategy.data_dictionary_all));

    assertThat(builder.get(null), is(nullValue()));
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
