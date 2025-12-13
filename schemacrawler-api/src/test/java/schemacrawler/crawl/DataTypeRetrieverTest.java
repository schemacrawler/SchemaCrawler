/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.typeInfoRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.model.implementation.MutableCatalog;
import schemacrawler.model.implementation.MutableDatabaseInfo;
import schemacrawler.model.implementation.MutableJdbcDriverInfo;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DataTypeRetrieverTest {

  private static String printColumnDataType(final ColumnDataType columnDataType) {
    final StringBuilder buffer = new StringBuilder();

    final boolean isUserDefined = columnDataType.getType() == user_defined;
    final String typeName = columnDataType.getFullName();
    final String dataType = (isUserDefined ? "user defined " : "") + "column data-type";
    final String nullable = (columnDataType.isNullable() ? "" : "not ") + "nullable";
    final String autoIncrementable =
        (columnDataType.isAutoIncrementable() ? "" : "not ") + "auto-incrementable";

    final String createParameters = columnDataType.getCreateParameters();
    final String definedWith =
        "defined with " + (isBlank(createParameters) ? "no parameters" : createParameters);

    final String literalPrefix = columnDataType.getLiteralPrefix();
    final String literalPrefixText =
        isBlank(literalPrefix) ? "no literal prefix" : "literal prefix " + literalPrefix;

    final String literalSuffix = columnDataType.getLiteralSuffix();
    final String literalSuffixText =
        isBlank(literalSuffix) ? "no literal suffix" : "literal suffix " + literalSuffix;

    final String javaSqlType = "java.sql.Types: " + columnDataType.getStandardTypeName();

    final String precision = "precision " + columnDataType.getPrecision();
    final String minimumScale = "minimum scale " + columnDataType.getMinimumScale();
    final String maximumScale = "maximum scale " + columnDataType.getMaximumScale();

    buffer
        .append(typeName)
        .append("\n")
        .append("  ")
        .append(dataType)
        .append("\n")
        .append("  ")
        .append(definedWith)
        .append("\n")
        .append("  ")
        .append(nullable)
        .append("\n")
        .append("  ")
        .append(autoIncrementable)
        .append("\n")
        .append("  ")
        .append(literalPrefixText)
        .append("\n")
        .append("  ")
        .append(literalSuffixText)
        .append("\n")
        .append("  ")
        .append(columnDataType.getSearchable().toString())
        .append("\n")
        .append("  ")
        .append(precision)
        .append("\n")
        .append("  ")
        .append(minimumScale)
        .append("\n")
        .append("  ")
        .append(maximumScale)
        .append("\n")
        .append("  ")
        .append(javaSqlType)
        .append("\n");

    if (isUserDefined) {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null) {
        baseTypeName = "";
      } else {
        baseTypeName = baseColumnDataType.getFullName();
      }
      buffer.append("\n").append("  ").append("based on ").append(baseTypeName).append("\n");
    }

    buffer.append("  attributes:\n");
    final Map<String, Object> attributes = new TreeMap<>(columnDataType.getAttributes());
    for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
      buffer
          .append("    ")
          .append(attribute.getKey())
          .append("=")
          .append(attribute.getValue())
          .append("\n");
    }

    return buffer.toString();
  }

  private static void verifyRetrieveColumnDataTypes(
      final Catalog catalog, final String expectedResultsResource) throws IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final List<ColumnDataType> columnDataTypes =
          (List<ColumnDataType>) catalog.getColumnDataTypes();
      assertThat("ColumnDataType count does not match", columnDataTypes, hasSize(25));
      Collections.sort(columnDataTypes, NamedObjectSort.alphabetical);
      for (final ColumnDataType columnDataType : columnDataTypes) {
        assertThat(columnDataType, notNullValue());
        out.println(printColumnDataType(columnDataType));
      }
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResultsResource)));
  }

  private MutableCatalog catalog;

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);
    final MutableDatabaseInfo databaseInfo =
        (MutableDatabaseInfo) connectionInfoBuilder.buildDatabaseInfo();
    final MutableJdbcDriverInfo jdbcDriverInfo =
        (MutableJdbcDriverInfo) connectionInfoBuilder.buildJdbcDriverInfo();

    catalog = new MutableCatalog("datatype_test", databaseInfo, jdbcDriverInfo);
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getDatabaseInfo().getServerInfo(), is(empty()));
  }

  @Test
  @DisplayName("Override type info from data dictionary")
  public void overrideTypeInfoFromDataDictionary(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final int magicNumber = 99;

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.TYPE_INFO,
                """
                SELECT
                  %d AS INJECTED_TEST_ATTRIBUTE,
                  TYPE_INFO.*
                FROM
                  INFORMATION_SCHEMA.SYSTEM_TYPEINFO TYPE_INFO
                """
                    .formatted(magicNumber))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder()
            .with(typeInfoRetrievalStrategy, data_dictionary_all)
            .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DataTypeRetriever dataTypeRetriever =
        new DataTypeRetriever(retrieverConnection, catalog, options);
    dataTypeRetriever.retrieveSystemColumnDataTypes();

    verifyRetrieveColumnDataTypes(catalog, testContext.testMethodFullName());
  }

  @Test
  @DisplayName("System data types")
  public void systemDataTypes(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptionsDefault);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DataTypeRetriever dataTypeRetriever =
        new DataTypeRetriever(retrieverConnection, catalog, options);
    dataTypeRetriever.retrieveSystemColumnDataTypes();

    final Collection<ColumnDataType> systemColumnDataTypes = catalog.getSystemColumnDataTypes();
    assertThat(systemColumnDataTypes, hasSize(25));

    // Additional catalog tests
    assertThat(
        catalog.getColumnDataTypes(new SchemaReference("catalog", "schema")),
        is(emptyCollectionOf(ColumnDataType.class)));
    assertThrows(NullPointerException.class, () -> catalog.getColumnDataTypes(null));
  }
}
