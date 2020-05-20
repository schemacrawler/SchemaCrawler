/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseInfoRetrieverTest
{

  private static String printColumnDataType(final ColumnDataType columnDataType)
  {
    final StringBuffer buffer = new StringBuffer();

    final boolean isUserDefined = columnDataType.isUserDefined();
    final String typeName = columnDataType.getFullName();
    final String dataType = (isUserDefined? "user defined ": "") + "column data-type";
    final String nullable = (columnDataType.isNullable()? "": "not ") + "nullable";
    final String autoIncrementable = (columnDataType.isAutoIncrementable()? "": "not ") + "auto-incrementable";

    final String createParameters = columnDataType.getCreateParameters();
    final String definedWith = "defined with " + (isBlank(createParameters)? "no parameters": createParameters);

    final String literalPrefix = columnDataType.getLiteralPrefix();
    final String literalPrefixText = isBlank(literalPrefix)? "no literal prefix": "literal prefix " + literalPrefix;

    final String literalSuffix = columnDataType.getLiteralSuffix();
    final String literalSuffixText = isBlank(literalSuffix)? "no literal suffix": "literal suffix " + literalSuffix;

    final String javaSqlType = "java.sql.Types: " + columnDataType
      .getJavaSqlType()
      .getName();

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
      .append(columnDataType
                .getSearchable()
                .toString())
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
      .append(javaSqlType);
    if (isUserDefined)
    {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null)
      {
        baseTypeName = "";
      }
      else
      {
        baseTypeName = baseColumnDataType.getFullName();
      }
      buffer
        .append("\n")
        .append("  ")
        .append("based on ")
        .append(baseTypeName);
    }

    return buffer.toString();
  }

  public static void verifyRetrieveColumnDataTypes(final Catalog catalog, final String expectedResultsResource)
    throws IOException
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final List<ColumnDataType> columnDataTypes = (List<ColumnDataType>) catalog.getColumnDataTypes();
      assertThat("ColumnDataType count does not match", columnDataTypes, hasSize(23));
      Collections.sort(columnDataTypes, NamedObjectSort.alphabetical);
      for (final ColumnDataType columnDataType : columnDataTypes)
      {
        assertThat(columnDataType, notNullValue());
        out.println(printColumnDataType(columnDataType));
      }
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResultsResource)));
  }

  private MutableCatalog catalog;

  @Test
  @DisplayName("Override type info from data dictionary")
  public void overrideTypeInfoFromDataDictionary(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViewsBuilder()
      .withSql(InformationSchemaKey.OVERRIDE_TYPE_INFO, "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TYPEINFO");
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
      new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveSystemColumnDataTypes();

    verifyRetrieveColumnDataTypes(catalog, testContext.testMethodFullName());
  }

  @Test
  @DisplayName("Retrieve server info from data dictionary")
  public void serverInfoFromDataDictionary(final TestContext testContext, final Connection connection)
    throws Exception
  {
    assertThat(catalog
                 .getDatabaseInfo()
                 .getServerInfo(), is(empty()));

    final String name = "TEST Server Info Property - Name";
    final String description = "TEST Server Info Property - Description";
    final String value = "TEST Server Info Property - Value";

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withInformationSchemaViewsBuilder()
      .withSql(InformationSchemaKey.SERVER_INFORMATION,
               String.format("SELECT '%s' AS NAME, '%s' AS DESCRIPTION, '%s' AS VALUE "
                             + "FROM INFORMATION_SCHEMA.SYSTEM_TYPEINFO", name, description, value));
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
      new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveServerInfo();

    final List<Property> serverInfo = new ArrayList<>(catalog
                                                        .getDatabaseInfo()
                                                        .getServerInfo());
    assertThat(serverInfo, hasSize(1));
    final Property serverInfoProperty = serverInfo.get(0);
    assertThat(serverInfoProperty, is(new ImmutableServerInfoProperty(name, value, description)));
    assertThat(serverInfoProperty.getDescription(), is(description));
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection)
    throws SQLException
  {
    catalog = new MutableCatalog("database_info_test");
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog
                 .getDatabaseInfo()
                 .getServerInfo(), is(empty()));
  }

}
